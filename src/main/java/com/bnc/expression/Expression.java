package com.bnc.expression;

import java.util.Map;

/**
 * 表达式
 *
 * @author songliangliang
 * @since 2022/6/16
 */
public interface Expression {

    /**
     * 对某个对象进行评价，结果为true or false
     *
     * @param o 对象
     * @return true/false
     */
    boolean eval(Map<String, Object> o);

    String getVal();
}
