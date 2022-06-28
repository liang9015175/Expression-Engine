package com.bnc.expression;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.json.JSONUtil;
import lombok.Getter;

import java.lang.reflect.ParameterizedType;
import java.util.Map;
import java.util.Objects;

/**
 * 值表达式
 *
 * @param <T> 值的类型
 * @author songliangliang
 * @since 2022/6/16
 */
@Getter
public class ValueExpression<T> implements Expression {

    /**
     * 值，可能是个int,string,甚至是list
     */
    private final T val;

    public ValueExpression(T val) {
        if (val instanceof String) {
            val = (T) val.toString().replace("'", "").trim();
        }
        this.val = val;


    }

    @Override
    public boolean eval(Map<String, Object> o) {
        if (Objects.isNull(o) || CollectionUtil.isEmpty(o)) {
            return false;
        }
        Object value = o.values().toArray()[0];
        T t = JSONUtil.toBean(JSONUtil.toJsonStr(value), getClazz());
        return t.equals(val);
    }


    /**
     * 获取泛型参数的类型
     *
     * @return 泛型类型
     */
    @SuppressWarnings("unchecked")
    private Class<T> getClazz() {
        return (Class<T>) ((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    }

}
