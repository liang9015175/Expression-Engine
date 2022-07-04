package com.bnc.expression.relation;

import com.bnc.expression.DimensionExpression;
import com.bnc.expression.ValueExpression;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.Objects;

/**
 * 等号表达式
 *
 * @author songliangliang
 * @since 2022/6/16
 */
@Slf4j
public class Equals extends RelationExpression {

    public static String symbol = "=";

    public Equals(DimensionExpression dimensionExpression, ValueExpression<?> valueExpression) {
        super(dimensionExpression, symbol, valueExpression);
    }

    public Equals() {
        super(null, symbol, null);
    }

    @Override
    public RelationExpression copy() {
        return new Equals();
    }

    @Override
    public boolean eval(Map<String, Object> param) {
        if (super.eval(param)) {
            Object actual = param.get(getDimensionExpression().getVal());
            String expect = getValueExpression().getVal();
            boolean b = expect.equals(Objects.isNull(actual)?null:actual.toString());
            if (!b) {
                log.warn("dimension:{} eval fail ,expect:{},actual:{}", getDimensionExpression().getVal(), expect, actual);
                return false;
            }
            return true;
        }
        return false;
    }
}
