package com.bnc.expression.relation;

import com.bnc.expression.DimensionExpression;
import com.bnc.expression.ValueExpression;
import com.google.common.base.Splitter;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

/**
 * 不在某个区间
 *
 * @author songliangliang
 * @since 2022/6/16
 */
@Slf4j
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

    @Override
    public boolean eval(Map<String, Object> param) {
        if (super.eval(param)) {
            List<String> expect = Arrays.asList(getValueExpression().getVal().split(","));
            List<String> actual = Splitter.on(",").splitToList(param.get(getDimensionExpression().getVal()).toString());
            boolean b = expect.stream().anyMatch(actual::contains);
            if (b) {
                log.warn("dimension:{} eval fail ,expect:{},actual:{}", getDimensionExpression().getVal(), expect, actual);
                return false;
            }
            return true;
        }
        return false;
    }
}
