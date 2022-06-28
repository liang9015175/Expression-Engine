package com.bnc.expression.relation;

import com.bnc.expression.DimensionExpression;
import com.bnc.expression.ValueExpression;
import lombok.NoArgsConstructor;

/**
 * 小于等于
 *
 * @author songliangliang
 * @since 2022/6/16
 */
public class Lte extends RelationExpression {
    public static String symbol = "<=";

    public Lte(DimensionExpression dimensionExpression, ValueExpression<?> valueExpression) {
        super(dimensionExpression, symbol, valueExpression);
    }

    public Lte() {
        super(null, symbol, null);
    }

    @Override
    public RelationExpression copy() {
        return new Lte();
    }
}
