package com.bnc.expression.parse;

import com.bnc.expression.node.ExpressionNode;

import java.util.HashMap;
import java.util.Map;

/**
 * @author songliangliang
 * @since 2022/6/27
 */
public class ExpressionParserTest {

    @org.junit.Test
    public void equal() {

        String origin = "sex=女";
        ExpressionNode parse = ExpressionParser.parse(origin);
        Map<String, Object> param = new HashMap<>();
        param.put("sex", "女");
        boolean eval = parse.eval(param);
        System.out.println(eval);

    }

    @org.junit.Test
    public void equalAnd() {

        String origin = "sex=女 and age!=19";
        ExpressionNode parse = ExpressionParser.parse(origin);
        Map<String, Object> param = new HashMap<>();
        param.put("sex", "女");
        param.put("age", "19"); // u can modify this param to test the expression. eg. age=20
        boolean eval = parse.eval(param);
        System.out.println(eval);

    }

    @org.junit.Test
    public void baseAndIs() {

        String origin = "sex=女 and age is not null";
        ExpressionNode parse = ExpressionParser.parse(origin);
        Map<String, Object> param = new HashMap<>();
        param.put("sex", "女");
        param.put("age", null);// u can modify this param, eg. not set this item  or set age explicit value
        boolean eval = parse.eval(param);
        System.out.println(eval);

    }

    @org.junit.Test
    public void baseOr() {

        String origin = "sex=女 or age=19";
        ExpressionNode parse = ExpressionParser.parse(origin);
        Map<String, Object> param = new HashMap<>();
        param.put("sex", "女");
        param.put("age", 20);// u can modify this param, eg. not set this item  or set age explicit value
        boolean eval = parse.eval(param);
        System.out.println(eval);

    }

    @org.junit.Test
    public void baseGroup() {

        String origin = "sex=女 and (age=19 or age=20)";
        ExpressionNode parse = ExpressionParser.parse(origin);
        Map<String, Object> param = new HashMap<>();
        param.put("sex", "女");
        param.put("age", 21);// u can modify this param, eg. not set this item  or set age explicit value
        boolean eval = parse.eval(param);
        System.out.println(eval);
    }

    @org.junit.Test
    public void complexIn() {

        String origin = "sex=女 and (age=19 or age=20) and name  in [张三,李四]";
        ExpressionNode parse = ExpressionParser.parse(origin);
        Map<String, Object> param = new HashMap<>();
        param.put("sex", "女");
        param.put("age", 20);
        param.put("name", "王五");// u can modify this param, eg. not set this item  or set age explicit value
        boolean eval = parse.eval(param);
        System.out.println(eval);
    }

    @org.junit.Test
    public void complexNotIn() {

        String origin = "sex=女 and (age=19 or age=20) or name not in [张三,李四]";
        ExpressionNode parse = ExpressionParser.parse(origin);
        Map<String, Object> param = new HashMap<>();
        param.put("sex", "女");
        param.put("age", 20);
        param.put("name", "李四");// u can modify this param, eg. not set this item  or set age explicit value
        boolean eval = parse.eval(param);
        System.out.println(eval);
    }

    @org.junit.Test
    public void complexGt() {

        String origin = "sex=女 and (age>19 and age<22) or name  in [张三,李四]";
        ExpressionNode parse = ExpressionParser.parse(origin);
        System.out.println(parse.getVal());
        //System.out.println(JSONUtil.toJsonStr(parse.index()));
        Map<String, Object> param = new HashMap<>();
        param.put("sex", "女");
        param.put("age", 24);
        param.put("name", "李四");// u can modify this param, eg. not set this item  or set age explicit value
        boolean eval = parse.eval(param);
        System.out.println(eval);
    }

    @org.junit.Test
    public void complexGt2() {
        Map<String, Object> bindings = new HashMap<>();
        bindings.put("a", "123");
        bindings.put("b", "123");
        bindings.put("c", "aa");
        bindings.put("d", "ff");

        long a = System.currentTimeMillis();
        ExpressionParser.parse("a='123' AND b!='123' OR (c='aa' AND d='ff')").eval(bindings);
        long b = System.currentTimeMillis();
        System.out.println("test1:" + (b - a));

        ExpressionParser.parse("a='123' AND b!='123' OR (c='aa' AND d='33')").eval(bindings);
        long c = System.currentTimeMillis();
        System.out.println("test2:" + (c - b));

        ExpressionParser.parse("a='123' AND b!='123' OR (c='aa' AND d='44')").eval(bindings);
        long d = System.currentTimeMillis();
        System.out.println("test3:" + (d - c));}

}