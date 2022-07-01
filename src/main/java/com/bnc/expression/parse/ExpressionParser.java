package com.bnc.expression.parse;

import cn.hutool.json.JSONUtil;
import com.bnc.expression.DimensionExpression;
import com.bnc.expression.ExpressionFactory;
import com.bnc.expression.ValueExpression;
import com.bnc.expression.logic.LogicExpression;
import com.bnc.expression.node.ExpressionNode;
import com.bnc.expression.relation.RelationExpression;
import org.apache.commons.lang.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Stack;

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
        // 节点栈，默认认为只要构造一个节点，就优先认为它是左节点
        Stack<ExpressionNode> nodeStack = new Stack<>();

        // 表达式栈，解析到一个关系符之后，就可以创建一个维度表达式，当解析到一个 逻辑符时候，补充完整这个表达式
        Stack<RelationExpression> expressionStack = new Stack<>();

        // 消消乐栈，优先级栈，用于解决( )组合而创建的单个字符栈
        Stack<Character> xxlStack = new Stack<>();

        // (用户=1000) and ((IP='192.168.1.x' or 地址='深圳') AND 张三='哈哈哈')
        for (char character : expression.toCharArray()) {
            xxlStack.push(character);
            if (character == ')') {
                // pop till the touch the first character '('
                String subExpression = buildSubExpression(xxlStack);
                // parse
                pushToStack(nodeStack, expressionStack, subExpression);
            }
        }
        if (!xxlStack.isEmpty()) {
            String subExpression = buildSubExpression(xxlStack);
            pushToStack(nodeStack, expressionStack, subExpression);
        }
        ExpressionNode pop = nodeStack.pop();
        System.out.println(JSONUtil.toJsonStr(pop));



        return pop;



    }


    private static String buildSubExpression(Stack<Character> xxlStack) {
        StringBuilder charSequence = new StringBuilder();
        while (true) {
            Character pop = xxlStack.pop();
            charSequence.append(pop);
            if (pop == '(' || xxlStack.isEmpty()) {
                break;
            }
        }
        return charSequence.reverse().toString().replace("(", "").replace(")", "");
    }

    private static void pushToStack(Stack<ExpressionNode> nodeStack, Stack<RelationExpression> expressionStack, String expression) {

        List<LogicExpression> logicExpressionList = ExpressionFactory.getLogicExpressionList();
        List<RelationExpression> relationExpressionList = ExpressionFactory.getRelationExpressionList();

        StringBuilder dimensionOrValueBuilder = new StringBuilder();

        subOut:
        for (int j = 0; j < expression.length(); j++) {
            int subLength = expression.length();
            for (RelationExpression relationExpression : relationExpressionList) {
                String val = relationExpression.getVal();
                int min = Math.min(subLength - j, val.length());
                // 比较
                String subStr = expression.substring(j, j + min);
                if (subStr.equalsIgnoreCase(val)) {
                    j = j + val.length() - 1;
                    // 关系表达式左边是左语句，右边是右表达式
                    DimensionExpression dimensionExpression = new DimensionExpression(dimensionOrValueBuilder.toString());
                    RelationExpression relation = relationExpression.copy();
                    relation.setDimensionExpression(dimensionExpression);
                    expressionStack.push(relation);
                    dimensionOrValueBuilder = new StringBuilder(); // clear 维度
                    continue subOut;
                }
            }
            // 遇到 逻辑关系符之前所有的值都是value
            for (LogicExpression logicExpression : logicExpressionList) {
                String val = logicExpression.getVal();
                int min = Math.min(subLength - j, val.length());

                String subStr = expression.substring(j, j + min);
                // 用户=1000 and (IP='192.168.1.x' or 地址='深圳')
                if (subStr.equalsIgnoreCase(val)) {
                    j = j + val.length() - 1;
                    ValueExpression<String> valueExpression = new ValueExpression<>(dimensionOrValueBuilder.toString());
                    RelationExpression pop = expressionStack.pop();
                    if (Objects.nonNull(pop)) {
                        pop.setValueExpression(valueExpression);
                        expressionStack.push(pop);
                    }
                    ExpressionNode parent = new ExpressionNode().setLogicExpression(logicExpression.copy());

                    ExpressionNode left = nodeStack.isEmpty() ? null : nodeStack.pop();
                    if (Objects.isNull(left)) {
                        left = parent.setLeft(new ExpressionNode().setRelationExpression(pop));
                    } else {
                        if (Objects.isNull(left.getRight())) {
                            left = parent.setLeft(left.setRight(new ExpressionNode().setRelationExpression(pop)));
                        } else {
                            left = parent.setLeft(left).setRight(new ExpressionNode().setRelationExpression(pop));
                        }
                    }
                    nodeStack.push(left);
                    dimensionOrValueBuilder = new StringBuilder(); // clear 值
                    continue subOut;
                }
            }
            // 无法匹配关系表达式和逻辑表达式的时候，认为是普通的维度
            dimensionOrValueBuilder.append(expression.toCharArray()[j]);
        }
        // 遇到逻辑符之前 || 遇到结尾的之前， 剩余的字符串统一当做值来处理
        if (!StringUtils.isBlank(dimensionOrValueBuilder.toString())) {
            RelationExpression pop = expressionStack.isEmpty() ? null : expressionStack.pop();
            if (Objects.nonNull(pop)) {
                pop.setValueExpression(new ValueExpression<>(dimensionOrValueBuilder.toString()));
                expressionStack.push(pop);

                ExpressionNode left = nodeStack.isEmpty() ? null : nodeStack.pop();

                if (Objects.isNull(left)) {
                    left = new ExpressionNode().setRelationExpression(pop);
                } else {
                    // 先补全树
                    if (Objects.isNull(left.getRight())) {
                        left = left.setRight(new ExpressionNode().setRelationExpression(pop));
                    } else {
                        left = new ExpressionNode().setLeft(left).setRight(new ExpressionNode().setRelationExpression(pop));
                    }
                }
                nodeStack.push(left);
            }
        }
    }

    /**
     * 解析实际入参，将其转化成为k-v键值对
     *
     * @param expression 实际入参表达式
     * @return 实际入参的key-value集合，用于代入expression 内部进行求值
     */
    public static Map<String, Object> convert(String expression) {
        return null;
    }

    /**
     * 对某个输入，按照表达式进行校验
     *
     * @param expression       表达式
     * @param actualExpression 实际入参
     * @return 校验结果为真 or false
     */
    public static boolean eval(String expression, String actualExpression) {
        return parse(expression).eval(convert(actualExpression));
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
    public static boolean eval(ExpressionNode expressionNode, String actualExpression) {

        return expressionNode.eval(convert(actualExpression));
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

}
