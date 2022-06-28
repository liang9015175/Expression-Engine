package com.bnc.expression.logic;

/**
 * 组合关）
 *
 * @author songliangliang
 * @since 2022/6/16
 */
public class GroupClose extends LogicExpression {
    public static String symbol = ")";

    public GroupClose() {
        super(symbol);
    }

    @Override
    public LogicExpression copy() {
        return new GroupClose();
    }
}
