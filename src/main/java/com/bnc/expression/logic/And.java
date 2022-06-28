package com.bnc.expression.logic;

/**
 * And符号
 *
 * @author songliangliang
 * @since 2022/6/16
 */
public class And extends LogicExpression {
    public static String symbol = " AND ";

    public And() {
        super(symbol);
    }

    @Override
    public LogicExpression copy() {
        return new And();
    }
}
