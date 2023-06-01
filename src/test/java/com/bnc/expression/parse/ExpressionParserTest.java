package com.bnc.expression.parse;

import cn.hutool.json.JSONUtil;
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
        // 表达式内容
        String origin = "sex=女 and (age>19 and age<22) or name  in [张三,李四]";
        // 实际入参
        Map<String, Object> param = new HashMap<>();
        param.put("sex", "女");
        param.put("age", 24);
        param.put("name", "李四");
        // 校验
        boolean eval = ExpressionParser.eval(origin,param);
        // ExpressionNode parse = ExpressionParser.parse(origin); // 输出表达式解析后的节点结构信息,用于观察表达式的整体结构是否满足树状结构
        // boolean eval1 = ExpressionParser.parse(origin).eval(param); // 或者你也可以这样来评价整体的表达式
        // Queue<Expression> expressions = ExpressionUtil.parseOrigin(origin);// 输出表达式被解析后的每个表达式列表,用于观察解析后每个表达式是否正确

        System.out.println("整体评价结果: "+eval);
    }

    @org.junit.Test
    public void complexGt2() {
//        LoggerContext iLoggerFactory =(LoggerContext) LoggerFactory.getILoggerFactory();
//        iLoggerFactory.getLoggerList().forEach(v->v.setLevel(Level.OFF));
//        for (int i=0;i<100;i++){
//            Map<String, Object> bindings = new HashMap<>();
//            bindings.put("sex", "女");
//            bindings.put("age", "25");
//            bindings.put("tag", "makeup");
//
//            long a = System.currentTimeMillis();
//            ExpressionParser.eval("sex=女 or （(age≥20 and age≤30) and tag=makeup)", bindings);
//            long b = System.currentTimeMillis();
//            System.out.println("test1:" + (b - a));
//        }

        ExpressionNode parse = ExpressionParser.parse("sex=女 or ((age≥20 and age≤30) and tag=makeup)");
        System.out.println(JSONUtil.toJsonStr(parse));

    }
    @org.junit.Test
    public void complexGt22() {
        Map<String, Object> bindings = new HashMap<>();
        bindings.put("last1", "1");
        bindings.put("last2", "2");
        //@@aku->last1 in [2,3,4,5]@@mkp->last1 in[6,7,8,9]
        boolean eval = ExpressionParser.parse("last1 in[6,7,8,9]").eval(bindings);
        System.out.println(eval);

    }
}