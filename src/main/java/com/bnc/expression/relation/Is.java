package com.bnc.expression.relation;

import com.bnc.expression.DimensionExpression;
import com.bnc.expression.ValueExpression;

/**
 * IS 表达式
 *
 * @author songliangliang
 * @since 2022/6/28
 */
public class Is extends RelationExpression {

    public static String symbol = " IS ";

    public Is(DimensionExpression dimensionExpression, ValueExpression<?> valueExpression) {
        super(dimensionExpression, symbol, valueExpression);
    }

    public Is() {
        super(null, symbol, null);
    }

    @Override
    public RelationExpression copy() {
        return new Is();
    }
}
