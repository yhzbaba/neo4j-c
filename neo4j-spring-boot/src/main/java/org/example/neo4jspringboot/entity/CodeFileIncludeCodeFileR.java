package org.example.neo4jspringboot.entity;

import lombok.Data;
import org.neo4j.ogm.annotation.*;

@Data
@RelationshipEntity(type = "include")
public class CodeFileIncludeCodeFileR {
    @Id
    @GeneratedValue
    private Long id;

    @StartNode
    private CCodeFileInfo parent;

    @EndNode
    private CCodeFileInfo child;

    @Property
    private String relation;
}
