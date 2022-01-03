package org.example.neo4jspringboot.entity;

import lombok.Data;
import org.neo4j.ogm.annotation.*;

/**
 * 函数调用函数
 */
@Data
@RelationshipEntity(type = "invoke")
public class FunctionInvokeFunctionR {
    @Id
    @GeneratedValue
    private Long id;

    @StartNode
    private CFunctionInfo parent;

    @EndNode
    private CFunctionInfo child;

    @Property
    private String relation;
}
