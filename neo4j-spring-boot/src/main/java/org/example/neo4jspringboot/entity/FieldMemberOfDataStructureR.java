package org.example.neo4jspringboot.entity;

import lombok.Data;
import org.neo4j.ogm.annotation.*;

@Data
@RelationshipEntity(type = "memberof")
public class FieldMemberOfDataStructureR {
    @Id
    @GeneratedValue
    private Long id;

    @StartNode
    private CFieldInfo parent;

    @EndNode
    private CDataStructureInfo child;

    @Property
    private String relation;
}
