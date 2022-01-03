package org.example.neo4jspringboot.entity;

import com.sun.org.apache.xpath.internal.operations.Bool;
import lombok.Data;
import org.eclipse.cdt.core.dom.ast.IASTDeclSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTSimpleDeclaration;
import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Property;

@Data
@NodeEntity(label = "c-variable-node")
public class CVariableInfo {
    @Id
    @GeneratedValue
    private Long id;

    /**
     * 变量名(如果是宏那就是宏的内容)
     */
    @Property
    private String name;

    /**
     * 内容
     */
    @Property
    private String content;

    /**
     * 属于哪里，或者是一个最外层的变量而已
     */
    @Property
    private String belongTo;

    @Property
    private Boolean isDefine;

    /**
     * 是不是一个结构体变量，进一步筛选是不是自己定义的结构体的变量
     */
    private Boolean isStructVariable;

    /**
     * 非存储属性，用来后续处理修饰符
     */
    private IASTDeclSpecifier specifier;

    /**
     * 他自己所有信息都在里面
     */
    private IASTSimpleDeclaration simpleDeclaration;
}
