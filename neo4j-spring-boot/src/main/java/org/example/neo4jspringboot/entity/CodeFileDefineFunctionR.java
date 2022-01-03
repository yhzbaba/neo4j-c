package org.example.neo4jspringboot.entity;

import lombok.Data;
import org.neo4j.ogm.annotation.*;

/**
 * 文件定义函数
 */
@Data
@RelationshipEntity(type = "define")
public class CodeFileDefineFunctionR {
    @Id
    @GeneratedValue
    private Long id;

    @StartNode
    private CCodeFileInfo parent;

    @EndNode
    private CFunctionInfo child;

    @Property
    private String relation;
}
