package com.bnc.expression.relation;

import com.bnc.expression.DimensionExpression;
import com.bnc.expression.ValueExpression;
import lombok.NoArgsConstructor;

/**
 * 大于表达式
 *
 * @author songliangliang
 * @since 2022/6/16
 */
public class Gt extends RelationExpression {

    public static String symbol = ">";

    public Gt(DimensionExpression dimensionExpression, ValueExpression<?> valueExpression) {
        super(dimensionExpression, symbol, valueExpression);
    }

    public Gt() {
        super(null, symbol, null);
    }

    @Override
    public RelationExpression copy() {
        return new Gt();
    }
}
