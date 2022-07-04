package com.bnc.expression.parse;

import com.bnc.expression.DimensionExpression;
import com.bnc.expression.Expression;
import com.bnc.expression.ExpressionFactory;
import com.bnc.expression.ValueExpression;
import com.bnc.expression.logic.LogicExpression;
import com.bnc.expression.node.ExpressionNode;
import com.bnc.expression.relation.RelationExpression;
import com.google.common.collect.Lists;
import org.apache.commons.lang.StringUtils;

import java.util.*;

/**
 * 表达式解析器
 * 表达式解析器负责
 *
 * @author songliangliang
 * @since 2022/6/16
 */
public class ExpressionParser {


    /**
     * 输入表达式文本，解析成为一个节点
     *
     * @param expression 表达式节点
     * @return 表达式节点
     */
    public static ExpressionNode parse(String expression) {
        // 1.解析
        Expression[] expressions = parseOrigin(expression);
        // 2.替换
        return parse(expressions);
    }


    /**
     * 对某个输入，按照表达式进行校验
     *
     * @param expression       表达式
     * @param actualExpression 实际入参
     * @return 校验结果为真 or false
     */
    public static boolean eval(String expression, Map<String, Object> actualExpression) {
        return parse(expression).eval(actualExpression);
    }

    /**
     * 对某个输入，按照表达式进行校验
     *
     * @param expressionNode   表达式
     * @param actualExpression 实际入参
     * @return 校验结果为真 or false
     */
    public static boolean eval(ExpressionNode expressionNode, Map<String, Object> actualExpression) {
        return expressionNode.eval(actualExpression);
    }

    private static ExpressionNode parse(Expression[] expressions) {
        Stack<Expression> stack = new Stack<>();
        for (Expression expression : expressions) {
            stack.push(expression);
            // pop till the first '('
            // 替换成新的表达式
            if (expression.getVal().equals(")")) parseExpression(stack);
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
    private static void parseExpression(Stack<Expression> stack) {

        Stack<ExpressionNode> leftStack = new Stack<>();

        // IP='192.168.1.x' or 地址='深圳'
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
            if (pop.getVal().equalsIgnoreCase(" and ") || pop.getVal().equalsIgnoreCase(" or ")) {
                subStack.push(pop);
            }
            if (pop instanceof ExpressionNode) {
                subStack.push(pop);
            }
            if (Objects.nonNull(d) && Objects.nonNull(v) && Objects.nonNull(r)) {
                RelationExpression relationExpression = r.setDimensionExpression(d).setValueExpression(v);
                relationExpression.setVal(d.getVal()+r.getVal()+v.getVal());
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

    private static Expression[] parseOrigin(String origin) {

        Stack<Expression> expressionStack = new Stack<>();

        List<LogicExpression> logicList = ExpressionFactory.getLogicExpressionList();
        List<RelationExpression> relationList = ExpressionFactory.getRelationExpressionList();

        char[] chars = origin.toCharArray();

        StringBuilder dimensionOrValue = new StringBuilder();
        out:
        for (int i = 0; i < chars.length; i++) {
            for (RelationExpression r : relationList) {
                String val = r.getVal();
                int min = Math.min(origin.length(), i + val.length());
                String substring = origin.substring(i, min);

                // 匹配到关系表达式 比如 = !=,则左边是维度，右边是值
                if (substring.equalsIgnoreCase(val)) {
                    i = i + val.length() - 1;
                    if (!StringUtils.isBlank(dimensionOrValue.toString().trim())) {
                        expressionStack.push(new DimensionExpression(dimensionOrValue.toString().trim()));
                    }
                    expressionStack.push(r.copy());
                    dimensionOrValue = new StringBuilder(); // clear
                    continue out;
                }

            }
            for (LogicExpression l : logicList) {
                String val = l.getVal();
                int min = Math.min(origin.length(), i + val.length());
                String substring = origin.substring(i, min);

                // 如果匹配到逻辑表达式 比如 AND  OR 等.则右边的是值
                if (substring.equalsIgnoreCase(val)) {
                    i = i + val.length() - 1;
                    if (!StringUtils.isBlank(dimensionOrValue.toString().trim().replace("[", "").replace("]", ""))) {
                        expressionStack.push(new ValueExpression<>(dimensionOrValue.toString()));
                    }
                    expressionStack.push(l.copy());
                    dimensionOrValue = new StringBuilder();
                    continue out;
                }
            }
            dimensionOrValue.append(chars[i]);
        }
        if (StringUtils.isNotBlank(dimensionOrValue.toString().trim())) {
            expressionStack.push(new ValueExpression<>(dimensionOrValue.toString().trim().replace("[", "").replace("]", "")));
        }
        return expressionStack.toArray(new Expression[0]);
    }

}
