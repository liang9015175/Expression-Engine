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
        int length = expression.length();

        List<LogicExpression> logicExpressionList = ExpressionFactory.getLogicExpressionList();

        List<RelationExpression> relationExpressionList = ExpressionFactory.getRelationExpressionList();


        Stack<ExpressionNode> expressionNodeStack = new Stack<>();

        Stack<RelationExpression> relationExpressionStack = new Stack<>();

        // 外层循环遍历表达式长度
        // 假设匹配的字符串是  用户=1000 and IP='192.168.1.x' or 地址='深圳'
        // 用户=1000 and (IP='192.168.1.x' or 地址='深圳')
        StringBuilder dimensionBuilder = new StringBuilder();
        out:
        for (int i = 0; i < length; i++) {
            // 遇到关系符之前，所以的值都是维度
            for (RelationExpression relationExpression : relationExpressionList) {
                String val = relationExpression.getVal();
                int min = Math.min(length - i, val.length());
                // 比较
                String subStr = expression.substring(i, i + min);

                if (subStr.equalsIgnoreCase(val)) {
                    i = i + val.length() - 1;
                    // 关系表达式左边是左语句，右边是右表达式
                    DimensionExpression dimensionExpression = new DimensionExpression(dimensionBuilder.toString());
                    RelationExpression relation = relationExpression.copy();
                    relation.setDimensionExpression(dimensionExpression);
                    relationExpressionStack.push(relation);
                    dimensionBuilder = new StringBuilder(); // clear 维度
                    continue out;
                }
            }
            // 遇到 逻辑关系符之前所有的值都是value
            for (LogicExpression logicExpression : logicExpressionList) {
                String val = logicExpression.getVal();
                int min = Math.min(length - i, val.length());

                String subStr = expression.substring(i, i + min);
                // 用户=1000 and IP='192.168.1.x' or 地址='深圳'
                if (subStr.equalsIgnoreCase(val)) {
                    i = i + val.length() - 1;
                    ValueExpression<String> valueExpression = new ValueExpression<>(dimensionBuilder.toString());
                    RelationExpression pop = relationExpressionStack.pop();
                    if (Objects.nonNull(pop)) {
                        pop.setValueExpression(valueExpression);
                        relationExpressionStack.push(pop);
                    }
                    ExpressionNode parent = new ExpressionNode().setLogicExpression(logicExpression.copy());

                    ExpressionNode left = expressionNodeStack.isEmpty() ? null : expressionNodeStack.pop();
                    if (Objects.isNull(left)) {
                        left = parent.setLeft(new ExpressionNode().setRelationExpression(pop));
                    } else {
                        if (Objects.isNull(left.getRight())) {
                            left = parent.setLeft(left.setRight(new ExpressionNode().setRelationExpression(pop)));
                        } else {
                            left =parent.setLeft(left);
                        }
                    }
                    expressionNodeStack.push(left);
                    dimensionBuilder = new StringBuilder(); // clear 值
                    continue out;
                }
            }
            // 无法匹配关系表达式和逻辑表达式的时候，认为是普通的维度
            dimensionBuilder.append(expression.toCharArray()[i]);

        }
        // 遇到逻辑符之前 || 遇到结尾的之前， 剩余的字符串统一当做值来处理
        if (!StringUtils.isBlank(dimensionBuilder.toString())) {
            RelationExpression pop = relationExpressionStack.isEmpty() ? null : relationExpressionStack.pop();
            if (Objects.nonNull(pop)) {
                pop.setValueExpression(new ValueExpression<>(dimensionBuilder.toString()));
                relationExpressionStack.push(pop);
                ExpressionNode parent = new ExpressionNode();
                ExpressionNode left = expressionNodeStack.isEmpty() ? null : expressionNodeStack.pop();
                if (Objects.isNull(left)) {
                    left = parent.setLeft(new ExpressionNode().setRelationExpression(pop));
                } else {
                    if (Objects.isNull(left.getRight())) {
                        left = parent.setLeft(left.setRight(new ExpressionNode().setRelationExpression(pop)));
                    } else {
                        left =parent.setLeft(left);
                    }
                }
                expressionNodeStack.push(left);

            }
        }
        ExpressionNode pop = expressionNodeStack.pop();
        System.out.println(JSONUtil.toJsonStr(pop));
        return pop;

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
