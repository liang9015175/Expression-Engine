package com.bnc.expression.relation;

import cn.hutool.core.collection.CollectionUtil;
import com.bnc.expression.DimensionExpression;
import com.bnc.expression.Expression;
import com.bnc.expression.ValueExpression;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;
import java.util.Objects;

/**
 * 关系表达式
 *
 * @author songliangliang
 * @since 2022/6/16
 */
@Getter
@NoArgsConstructor
@Setter
public abstract class RelationExpression implements Expression {


    /**
     * 维度
     */
    private  DimensionExpression dimensionExpression;

    /**
     * 表达式的值 =,!=,>=等
     */
    protected String val;

    /**
     * 值
     */
    private  ValueExpression<?> valueExpression;


    protected RelationExpression(DimensionExpression dimensionExpression,  String val, ValueExpression<?> valueExpression) {
        this.dimensionExpression = dimensionExpression;
        this.val = val;
        this.valueExpression = valueExpression;
    }


    @Override
    public boolean eval(Map<String, Object> o) {
        if (Objects.isNull(o) || CollectionUtil.isEmpty(o)) {
            return false;
        }

        if (!o.containsKey(dimensionExpression.getVal())) {
            return false;
        }
        // 表达式校验

        return o.get(dimensionExpression.getVal()).equals(valueExpression.getVal());
    }

    public abstract RelationExpression copy();
}
