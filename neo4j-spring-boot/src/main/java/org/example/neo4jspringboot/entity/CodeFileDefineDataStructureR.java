package org.example.neo4jspringboot.entity;

import lombok.Data;
import org.neo4j.ogm.annotation.*;

/**
 * 文件中定义结构体的关系
 */
@Data
@RelationshipEntity(type = "define")
public class CodeFileDefineDataStructureR {
    @Id
    @GeneratedValue
    private Long id;

    @StartNode
    private CCodeFileInfo parent;

    @EndNode
    private CDataStructureInfo child;

    @Property
    private String relation;
}
