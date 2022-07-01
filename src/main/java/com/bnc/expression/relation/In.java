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
 * in表达式
 *
 * @author songliangliang
 * @since 2022/6/16
 */
@Slf4j
public class In extends RelationExpression {
    public static String symbol = " IN ";

    public In(DimensionExpression dimensionExpression, ValueExpression<?> valueExpression) {
        super(dimensionExpression, symbol, valueExpression);
    }

    public In() {
        super(null, symbol, null);
    }

    @Override
    public RelationExpression copy() {
        return new In();
    }

    @Override
    public boolean eval(Map<String, Object> param) {
        if (super.eval(param)) {
            List<String> expect = Arrays.asList(getValueExpression().getVal().split(","));
            List<String> actual = Splitter.on(",").splitToList(param.get(getDimensionExpression().getVal()).toString());
            boolean b = new HashSet<>(expect).containsAll(actual);
            if (!b) {
                log.warn("dimension:{} eval fail ,expect:{},actual:{}", getDimensionExpression().getVal(), expect, actual);
                return false;
            }
            return true;
        }
        return false;
    }
}
