package com.bnc.expression.parse;

import com.bnc.expression.ExpressionUtil;
import com.bnc.expression.node.ExpressionNode;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * 表达式解析器
 * 表达式解析器负责
 *
 * @author songliangliang
 * @since 2022/6/16
 */
@Slf4j
public class ExpressionParser {

    /**
     * 输入表达式文本，解析成为一个节点
     *
     * @param expression 表达式节点
     * @return 表达式节点
     */
    public static ExpressionNode parse(String expression) {
        // 2.替换
        return ExpressionUtil.parse(ExpressionUtil.parseOrigin(expression));
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
}
