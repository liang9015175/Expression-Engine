package com.bnc.expression.parse;

import cn.hutool.json.JSONUtil;
import com.bnc.expression.node.ExpressionNode;

/**
 * @author songliangliang
 * @since 2022/6/27
 */
public class ExpressionParserTest {

    @org.junit.Test
    public void parse() {

        String origin = "user=1000 and sex=男 and name is null and xx in [1,2,34]";
        ExpressionNode parse = ExpressionParser.parse(origin);
        System.out.println(JSONUtil.toJsonStr(parse));
    }
}