package com.bnc.expression.relation;

import com.bnc.expression.DimensionExpression;
import com.bnc.expression.ValueExpression;

/**
 * 等号表达式
 *
 * @author songliangliang
 * @since 2022/6/16
 */
public class Equals extends RelationExpression {

    public static String symbol = "=";

    public Equals(DimensionExpression dimensionExpression, ValueExpression<?> valueExpression) {
        super(dimensionExpression, symbol, valueExpression);
    }
    public Equals(){
        super(null,symbol,null);
    }

    @Override
    public RelationExpression copy() {
        return new Equals();
    }
}
