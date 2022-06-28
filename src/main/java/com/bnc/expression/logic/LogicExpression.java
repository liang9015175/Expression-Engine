package com.bnc.expression.logic;

import com.bnc.expression.Expression;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * 逻辑表达式
 *
 * @author songliangliang
 * @since 2022/6/16
 */
@Getter
@NoArgsConstructor
public abstract class LogicExpression implements Expression {

    /**
     * 逻辑表达式的值 and，or,（，）等
     */
    private String val;

    LogicExpression(String val) {
        this.val = val;
    }

    @Override
    public boolean eval(Map<String, Object> o) {
        return true;
    }

    public abstract LogicExpression copy();

}
