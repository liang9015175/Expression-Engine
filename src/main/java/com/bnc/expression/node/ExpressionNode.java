package com.bnc.expression.node;

import cn.hutool.json.JSONUtil;
import com.bnc.expression.Expression;
import com.bnc.expression.logic.And;
import com.bnc.expression.logic.LogicExpression;
import com.bnc.expression.relation.RelationExpression;
import com.google.common.collect.Lists;
import lombok.Data;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 表达式节点
 *
 * @author songliangliang
 * @since 2022/6/16
 */
@Data
@Accessors(chain = true)
@Slf4j
public class ExpressionNode implements Expression {

    /**
     * 是否是叶子节点，叶子节点是单个表达式的最小单位，包含了维度和值，以及基本的关系比如 = != 等等
     */
    private boolean leaf;

    /**
     * 关系表达式，包含了维度和值，注意 如果是虚拟节点，则此值不存在,只有叶子节点才有
     */
    private RelationExpression relationExpression;

    /**
     * 逻辑关系，包含了基本的是AND 还是 OR 还是( ) ,注意如果是叶子节点，则此值不存在
     */
    private LogicExpression logicExpression;

    /**
     * 左节点
     */
    private ExpressionNode left;

    /**
     * 右节点
     */
    private ExpressionNode right;


    /**
     * 整个节点进行评价，返回true or false
     *
     * @param param Map类型的参数k-v集合，我们会根据key 一次进行节点遍历匹配，然后给出最终的评价结果
     * @return true/false
     */
    @Override
    public boolean eval(Map<String, Object> param) {
        if (this.leaf) {
            // 叶子节点
            return this.relationExpression.eval(param);
        } else {
            boolean leftHand = Objects.isNull(this.left) || this.left.eval(param);
            boolean rightHand = Objects.isNull(this.right) || this.right.eval(param);
            boolean b;
            if (this.logicExpression instanceof And) {
                b = leftHand && rightHand;
            } else {
                b = leftHand || rightHand;
            }
            if (!b) {
                log.error("expression eval 【false】,expect:{},actual:{}", getVal(), JSONUtil.toJsonStr(param));
            }else {
                log.info("expression eval 【true】,expect:{},actual:{}",getVal(),JSONUtil.toJsonStr(param));
            }
            return b;
        }
    }

    @Override
    public String getVal() {
        return JSONUtil.toJsonStr(this);
    }

    /**
     * 表达式节点进行序列化索引，方便按照维度进行入库索引检索
     *
     * @return {@link RelationExpression}
     */
    public List<Expression> index() {
        List<Expression> expressions = Lists.newArrayList();
        index(this, expressions);
        return expressions;
    }

    private void index(ExpressionNode node, List<Expression> expressions) {
        if (Objects.nonNull(node)) {
            index(node.left, expressions);
            if (node.isLeaf()) {
                expressions.add(node.getRelationExpression());
            }
            index(node.right, expressions);
        }
    }
}
