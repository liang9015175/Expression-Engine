package com.bnc.expression.relation;

import com.bnc.expression.DimensionExpression;
import com.bnc.expression.ValueExpression;

/**
 * in表达式
 *
 * @author songliangliang
 * @since 2022/6/16
 */
public class In extends RelationExpression {
    public static String symbol = " IN ";

    public In(DimensionExpression dimensionExpression, ValueExpression<?> valueExpression) {
        super(dimensionExpression, symbol, valueExpression);
    }

    public In() {
        super(null, symbol, null);
    }

    @Override
    public RelationExpression copy() {
        return new In();
    }
}
