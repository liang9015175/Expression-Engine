package com.bnc.expression.relation;

import com.bnc.expression.DimensionExpression;
import com.bnc.expression.ValueExpression;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.Map;

/**
 * 小于等于
 *
 * @author songliangliang
 * @since 2022/6/16
 */
@Slf4j
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

    @Override
    public boolean eval(Map<String, Object> param) {
        boolean eval = super.eval(param);
        if (eval) {
            Object o = param.get(getDimensionExpression().getVal());
            BigDecimal actual = new BigDecimal(o.toString());
            BigDecimal expect = new BigDecimal(getValueExpression().getVal());
            if (actual.compareTo(expect) <= 0) {
                return true;
            } else {
                log.warn("dimension:{} eval fail ,expect:{},actual:{}", getDimensionExpression().getVal(), symbol+expect, actual);
            }
        }
        return false;
    }
}
