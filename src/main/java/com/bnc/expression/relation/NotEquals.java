package com.bnc.expression.relation;

import com.bnc.expression.DimensionExpression;
import com.bnc.expression.ValueExpression;

/**
 * 不等号表达式
 *
 * @author songliangliang
 * @since 2022/6/16
 */
public class NotEquals extends RelationExpression {
    public static String symbol = "!=";

    public NotEquals(DimensionExpression dimensionExpression, ValueExpression<?> valueExpression) {
        super(dimensionExpression, symbol, valueExpression);
    }

    public NotEquals() {
        super(null, symbol, null);
    }

    @Override
    public RelationExpression copy() {
        return new NotEquals();
    }
}
