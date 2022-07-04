package com.bnc.expression.relation;

import cn.hutool.core.collection.CollectionUtil;
import com.bnc.expression.DimensionExpression;
import com.bnc.expression.ValueExpression;
import lombok.extern.slf4j.Slf4j;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;

/**
 * IS 表达式
 *
 * @author songliangliang
 * @since 2022/6/28
 */
@Slf4j
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

    @Override
    public boolean eval(Map<String, Object> param) {
        if (!CollectionUtil.isEmpty(param)) {
            Object actual = param.get(getDimensionExpression().getVal());
            String expect = getValueExpression().getVal();
            if (expect.equalsIgnoreCase("null")) {
                if (Objects.isNull(actual)) {
                    return true;
                }
                log.warn("dimension:{} eval fail ,expect:{},actual:{}", getDimensionExpression().getVal(), expect, actual);
                return false;
            }
            if (expect.equalsIgnoreCase("not null")) {
                if (Objects.nonNull(actual)) {
                    return true;
                }
                log.warn("dimension:{} eval fail ,expect:{},actual:{}", getDimensionExpression().getVal(), expect, actual);
                return false;
            }
        }
        return false;
    }
}
