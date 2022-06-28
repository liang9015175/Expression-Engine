package com.bnc.expression.logic;

import lombok.NonNull;

/**
 * 组合开(
 * @author songliangliang
 * @since 2022/6/16
 */
public class GroupOpen  extends LogicExpression{
    public static String symbol = "(";

    public GroupOpen() {
        super(symbol);
    }

    @Override
    public LogicExpression copy() {
        return new GroupOpen();
    }
}
