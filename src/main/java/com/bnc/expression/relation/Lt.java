package com.bnc.expression.relation;

import com.bnc.expression.DimensionExpression;
import com.bnc.expression.ValueExpression;

/**
 * 小于表达式
 *
 * @author songliangliang
 * @since 2022/6/16
 */
public class Lt extends RelationExpression {
    public static String symbol = "<";

    public Lt(DimensionExpression dimensionExpression, ValueExpression<?> valueExpression) {
        super(dimensionExpression, symbol, valueExpression);
    }

    public Lt() {
        super(null, symbol, null);
    }

    @Override
    public RelationExpression copy() {
        return new Lt();
    }
}
