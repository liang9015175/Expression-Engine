package com.bnc.expression.relation;

import com.bnc.expression.DimensionExpression;
import com.bnc.expression.ValueExpression;
import lombok.NoArgsConstructor;
import lombok.NonNull;

/**
 * 大于等于
 *
 * @author songliangliang
 * @since 2022/6/16
 */
public class Gte extends RelationExpression {
    public static String symbol = ">=";

    public Gte(DimensionExpression dimensionExpression, ValueExpression<?> valueExpression) {
        super(dimensionExpression, symbol, valueExpression);
    }

    public Gte() {
        super(null, symbol, null);
    }

    @Override
    public RelationExpression copy() {
        return new Gte();
    }
}
