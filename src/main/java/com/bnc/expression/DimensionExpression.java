package com.bnc.expression;

import cn.hutool.core.collection.CollectionUtil;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.Map;
import java.util.Objects;

/**
 * 维度表达式
 *
 * @author songliangliang
 * @since 2022/6/16
 */
@Getter
@RequiredArgsConstructor
public class DimensionExpression implements Expression {

    /**
     * 维度值 比如是user, ip ,location, http，date等等,系统并没有对维度的穷举做约束，这些交给用户去自定义，系统只是用其去做equal比较
     */
    @NonNull
    private String val;


    /**
     * 对表达式进行评价
     *
     * @param o Map结构，key为维度的字面量,value同key一样，比如 {用户:用户}
     * @return 对其key进行比较，是否是一致的，只有维度是一样的才会进行匹配计算，比如如果用户的表达式里面没有user=1 这个维度，那么对于输入 ip=111 那这个匹配就不会成立
     */
    public boolean eval(Map<String, Object> o) {
        if (Objects.isNull(o) || CollectionUtil.isEmpty(o)) {
            return false;
        }
        return o.values().toArray()[0].equals(val);
    }
}
