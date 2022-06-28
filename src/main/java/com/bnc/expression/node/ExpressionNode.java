package com.bnc.expression.node;

import com.bnc.expression.DimensionExpression;
import com.bnc.expression.Expression;
import com.bnc.expression.logic.LogicExpression;
import com.bnc.expression.relation.RelationExpression;
import com.google.common.collect.Lists;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.LinkedList;
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
public class ExpressionNode implements Expression {

    /**
     * 表达式名称，系统程序自定义
     */
    //private String name;

    /**
     * 是否是虚拟节点，虚拟节点代表的是逻辑关系的占位符节点
     */
    //private boolean virtual;

    /**
     * 是否是叶子节点，叶子节点是单个表达式的最小单位，包含了维度和值，以及基本的关系比如 = != 等等
     */
    //private boolean leaf;

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
        // todo 校验表达式
        return false;
    }


    /**
     * 采用中序遍历法将节点进行遍历输出，递归
     *
     * @return 所有叶子表达式集合
     */
    public List<ExpressionNode> convert() {
        LinkedList<ExpressionNode> stack = Lists.newLinkedList();
        recursion(this, stack);
        // todo 对所有的叶子节点进行占位
        return stack;
    }

    private void recursion(ExpressionNode expressionNode, LinkedList<ExpressionNode> stack) {
        if (Objects.nonNull(expressionNode)) {
            recursion(this.left, stack);
            stack.add(this);
            recursion(this.right, stack);
        }
    }

}
