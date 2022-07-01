package com.bnc.expression.relation;

import com.bnc.expression.DimensionExpression;
import com.bnc.expression.Expression;
import com.bnc.expression.ValueExpression;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.Set;

/**
 * 关系表达式
 *
 * @author songliangliang
 * @since 2022/6/16
 */
@Getter
@NoArgsConstructor
@Setter
@Accessors(chain = true)
@Slf4j
public abstract class RelationExpression implements Expression {


    /**
     * 维度
     */
    private DimensionExpression dimensionExpression;

    /**
     * 表达式的值 =,!=,>=等
     */
    protected String val;

    /**
     * 值
     */
    private ValueExpression<?> valueExpression;


    protected RelationExpression(DimensionExpression dimensionExpression, String val, ValueExpression<?> valueExpression) {
        this.dimensionExpression = dimensionExpression;
        this.val = val;
        this.valueExpression = valueExpression;
    }


    @Override
    public boolean eval(Map<String, Object> param) {
        Set<String> keys = param.keySet();
        if (param.isEmpty() || !keys.contains(getDimensionExpression().getVal())) {
            log.warn("paramMap did not contain dimension:{}", this.getDimensionExpression().getVal());
            return false;
        }
        return true;
    }

    public abstract RelationExpression copy();
}
