package com.bnc.expression.logic;

/**
 * OR 表达式
 * @author songliangliang
 * @since 2022/6/16
 */
public class Or  extends LogicExpression{
    public static String symbol = " OR ";

    public Or() {
        super(symbol);
    }

    @Override
    public LogicExpression copy() {
        return new Or();
    }
}
