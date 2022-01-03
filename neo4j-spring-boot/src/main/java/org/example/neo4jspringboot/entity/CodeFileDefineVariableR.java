package org.example.neo4jspringboot.entity;

import lombok.Data;
import org.neo4j.ogm.annotation.*;

/**
 * 文件中定义一个变量
 */
@Data
@RelationshipEntity(type = "define")
public class CodeFileDefineVariableR {
    @Id
    @GeneratedValue
    private Long id;

    @StartNode
    private CCodeFileInfo parent;

    @EndNode
    private CVariableInfo child;

    @Property
    private String relation;
}
