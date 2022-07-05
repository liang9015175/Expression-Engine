package com.bnc.expression;

import com.bnc.expression.logic.*;
import com.bnc.expression.relation.*;
import com.google.common.collect.Lists;

import java.util.List;

/**
 * @author songliangliang
 * @since 2022/7/5
 */
public class Operations {

    public static final List<LogicExpression> logicExpressionList = Lists.newArrayList();

    public static final List<RelationExpression> relationExpressionList = Lists.newArrayList();

    static {
        logicExpressionList.add(new And());
        logicExpressionList.add(new Or());
        logicExpressionList.add(new GroupOpen());
        logicExpressionList.add(new GroupClose());

        relationExpressionList.add(new Equals());
        relationExpressionList.add(new Gt());
        relationExpressionList.add(new Gte());
        relationExpressionList.add(new In());
        relationExpressionList.add(new Is());
        relationExpressionList.add(new Lt());
        relationExpressionList.add(new Lte());
        relationExpressionList.add(new NotEquals());
        relationExpressionList.add(new NotIn());
    }
}
