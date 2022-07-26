package com.bnc.expression;

import com.bnc.expression.logic.*;
import com.bnc.expression.node.ExpressionNode;
import com.bnc.expression.relation.*;
import com.google.common.collect.Lists;
import org.apache.commons.lang.StringUtils;

import java.util.*;

/**
 * @author songliangliang
 * @since 2022/7/5
 */
public class ExpressionUtil {

    public static final List<LogicExpression> logicExpressionList = Lists.newArrayList();

    public static final List<RelationExpression> relationExpressionList = Lists.newArrayList();

    static {
        logicExpressionList.add(new And());
        logicExpressionList.add(new Or());
        logicExpressionList.add(new GroupOpen());
        logicExpressionList.add(new GroupClose());

        relationExpressionList.add(new Equals());
        relationExpressionList.add(new Gt());
        relationExpressionList.add(new Gte());
        relationExpressionList.add(new In());
        relationExpressionList.add(new Is());
        relationExpressionList.add(new Lt());
        relationExpressionList.add(new Lte());
        relationExpressionList.add(new NotEquals());
        relationExpressionList.add(new NotIn());
    }

    public static ExpressionNode parse(Queue<Expression> queue) {
        Stack<Expression> stack = new Stack<>();
        // 消消乐逻辑
        for (Expression expression : queue) {
            stack.push(expression);
            // 替换成新的表达式
            if (expression.getVal().equals(")")) {
                parseExpression(stack);
            }
        }
        parseExpression(stack);
        return (ExpressionNode) stack.pop();
    }

    /**
     * 在subStack中存在三种类型的数据
     * 节点类型  比如 A=xx and C=xxx
     * 逻辑符  比如 AND
     * 关系表达式 比如 A=xx
     *
     * @param stack (用户=1000) and ((IP='192.168.1.x' or 地址='深圳') AND (张三='哈哈哈') AND (王五='bababa'))
     */
    public static void parseExpression(Stack<Expression> stack) {
        Stack<ExpressionNode> leftStack = new Stack<>();
        Stack<Expression> subStack = subStack(stack);
        if (!subStack.isEmpty()) {
            List<Expression> subExpressions = Lists.newArrayList(subStack.toArray(new Expression[0]));
            Collections.reverse(subExpressions);
            for (Expression sub : subExpressions) {
                if (sub instanceof LogicExpression) {
                    LogicExpression l = ((LogicExpression) sub).copy();
                    ExpressionNode parent = new ExpressionNode().setLogicExpression(l).setLeaf(false);
                    // 先补充子树完整性
                    if (!leftStack.isEmpty()) {
                        ExpressionNode left = leftStack.pop();
                        left = parent.setLeft(left);
                        leftStack.push(left);
                    }
                }
                if (sub instanceof RelationExpression) {
                    if (leftStack.isEmpty()) {
                        leftStack.push(new ExpressionNode().setRelationExpression((RelationExpression) sub)).setLeaf(true);
                    } else {
                        ExpressionNode left = leftStack.pop();
                        // 先判断树是否完整
                        if (left.getRight() == null) {
                            left = left.setRight(new ExpressionNode().setRelationExpression((RelationExpression) sub).setLeaf(true));
                            leftStack.push(left);
                        }
                    }

                }
                if (sub instanceof ExpressionNode) {
                    if (leftStack.isEmpty()) {
                        leftStack.push((ExpressionNode) sub);
                    } else {
                        ExpressionNode left = leftStack.pop();
                        // 先判断树是否完整
                        if (left.getRight() == null) {
                            left = left.setRight((ExpressionNode) sub);
                            leftStack.push(left);
                        }
                    }
                }
            }
        }
        if (Objects.nonNull(leftStack.peek())) {
            stack.push(leftStack.pop());
        }
    }

    private static Stack<Expression> subStack(Stack<Expression> stack) {
        DimensionExpression d = null;
        ValueExpression<?> v = null;
        RelationExpression r = null;
        Stack<Expression> subStack = new Stack<>();
        while (!stack.isEmpty()) {
            Expression pop = stack.pop();
            if (pop instanceof DimensionExpression) {
                d = (DimensionExpression) pop;
            }
            if (pop instanceof RelationExpression) {
                r = ((RelationExpression) pop).copy();
            }
            if (pop instanceof ValueExpression) {
                v = (ValueExpression<?>) pop;
            }
            if ((pop instanceof LogicExpression) && pop.getVal().equals(" AND ") || pop.getVal().equals(" OR ")) {
                subStack.push(pop);
            }
            if (pop instanceof ExpressionNode) {
                subStack.push(pop);
            }
            if (Objects.nonNull(d) && Objects.nonNull(v) && Objects.nonNull(r)) {
                RelationExpression relationExpression = r.setDimensionExpression(d).setValueExpression(v);
                relationExpression.setVal(d.getVal() + r.getVal() + v.getVal());
                subStack.push(relationExpression);
                d = null;
                v = null;
                r = null;
            }
            if (pop.getVal().equals("(")) {
                break;
            }
        }
        return subStack;
    }

    public static Queue<Expression> parseOrigin(String origin) {
        Queue<Expression> expressionStack = new ArrayDeque<>();
        char[] chars = origin.toCharArray();
        StringBuilder dimensionOrValue = new StringBuilder();
        out:
        for (int i = 0; i < chars.length; i++) {
            for (RelationExpression r : relationExpressionList) {
                String val = r.getVal();
                int min = Math.min(origin.length(), i + val.length());
                String substring = origin.substring(i, min);

                // 匹配到关系表达式 比如 = !=,则左边是维度，右边是值
                if (substring.equalsIgnoreCase(val)) {
                    i = i + val.length() - 1;
                    if (!StringUtils.isBlank(dimensionOrValue.toString().trim())) {
                        expressionStack.add(new DimensionExpression(dimensionOrValue.toString().trim().replace("[", "").replace("]", "")));
                    }
                    expressionStack.add(r.copy());
                    dimensionOrValue = new StringBuilder(); // clear
                    continue out;
                }
            }
            for (LogicExpression l : logicExpressionList) {
                String val = l.getVal();
                int min = Math.min(origin.length(), i + val.length());
                String substring = origin.substring(i, min);

                // 如果匹配到逻辑表达式 比如 AND  OR 等.则右边的是值
                if (substring.equalsIgnoreCase(val)) {
                    i = i + val.length() - 1;
                    if (!StringUtils.isBlank(dimensionOrValue.toString().trim())) {
                        expressionStack.add(new ValueExpression<>(dimensionOrValue.toString().trim().replace("[", "").replace("]", "")));
                    }
                    expressionStack.add(l.copy());
                    dimensionOrValue = new StringBuilder();
                    continue out;
                }
            }
            dimensionOrValue.append(chars[i]);
        }
        if (StringUtils.isNotBlank(dimensionOrValue.toString().trim())) {
            expressionStack.add(new ValueExpression<>(dimensionOrValue.toString().trim().replace("[", "").replace("]", "")));
        }
        return expressionStack;
    }
}
