package com.bnc.expression.parse;

/**
 * @author songliangliang
 * @since 2022/6/27
 */
public class ExpressionParserTest {

    @org.junit.Test
    public void parse() {
        ExpressionParser.parse("用户=1000 and IP='192.168.1.x' or 地址='深圳'");
    }
}