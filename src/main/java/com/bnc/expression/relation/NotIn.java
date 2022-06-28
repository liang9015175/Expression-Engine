package com.bnc.expression.relation;

import com.bnc.expression.DimensionExpression;
import com.bnc.expression.ValueExpression;
import lombok.NoArgsConstructor;

/**
 * 不在某个区间
 *
 * @author songliangliang
 * @since 2022/6/16
 */
public class NotIn extends RelationExpression {
    public static String symbol = " NOT IN ";

    public NotIn(DimensionExpression dimensionExpression, ValueExpression<?> valueExpression) {
        super(dimensionExpression, symbol, valueExpression);
    }

    public NotIn() {
        super(null, symbol, null);
    }

    @Override
    public RelationExpression copy() {
        return new NotIn();
    }
}
