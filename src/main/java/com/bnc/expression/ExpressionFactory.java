package com.bnc.expression;

import com.bnc.expression.logic.*;
import com.bnc.expression.relation.*;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.Getter;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 表达式工厂
 *
 * @author songliangliang
 * @since 2022/6/17
 */
public class ExpressionFactory {

    @Getter
    static List<LogicExpression> logicExpressionList = Lists.newArrayList();

    @Getter
    static List<RelationExpression> relationExpressionList = Lists.newArrayList();
    static Map<String, LogicExpression> logicMap = Maps.newHashMap();

    static Map<String, RelationExpression> relationMap = Maps.newHashMap();

    static {
        registerLogicExpression();
        registerRelationExpression();
    }

    private static void registerRelationExpression() {
        relationExpressionList.add(new Equals());
        relationExpressionList.add(new Gt());
        relationExpressionList.add(new Gte());
        relationExpressionList.add(new In());
        relationExpressionList.add(new Is());
        relationExpressionList.add(new Lt());
        relationExpressionList.add(new Lte());
        relationExpressionList.add(new NotEquals());
        relationExpressionList.add(new NotIn());
        relationMap = relationExpressionList.stream().collect(Collectors.toMap(RelationExpression::getVal, Function.identity()));
    }

    private static void registerLogicExpression() {
        logicExpressionList.add(new And());
        logicExpressionList.add(new Or());
        logicExpressionList.add(new GroupOpen());
        logicExpressionList.add(new GroupClose());

        logicMap = logicExpressionList.stream().collect(Collectors.toMap(LogicExpression::getVal, Function.identity()));


    }

    public static LogicExpression logic(String logic) {
        return logicMap.get(logic).copy();
    }

    public static RelationExpression relation(String relation) {
        return relationMap.get(relation).copy();
    }
}
