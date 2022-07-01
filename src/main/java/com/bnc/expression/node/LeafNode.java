package com.bnc.expression.node;

import cn.hutool.json.JSONUtil;
import com.bnc.expression.DimensionExpression;
import com.bnc.expression.Expression;
import com.bnc.expression.ValueExpression;
import com.bnc.expression.relation.RelationExpression;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.Map;
import java.util.Objects;

/**
 * 各自节点
 *
 * @author songliangliang
 * @since 2022/6/30
 */
@Accessors(chain = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LeafNode implements Expression {

    private DimensionExpression dimension;

    private RelationExpression relation;

    private ValueExpression<?> value;

    @Override
    public boolean eval(Map<String, Object> o) {
        return false;
    }

    @Override
    public String getVal() {
        return JSONUtil.toJsonStr(this);
    }

    public boolean isFull() {
        return Objects.nonNull(dimension) && Objects.nonNull(relation) && Objects.nonNull(value);
    }
}
