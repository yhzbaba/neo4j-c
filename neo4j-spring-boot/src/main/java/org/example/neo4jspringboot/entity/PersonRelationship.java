package org.example.neo4jspringboot.entity;

import lombok.Data;
import org.neo4j.ogm.annotation.*;

@Data
@RelationshipEntity(type = "personRelationship")
public class PersonRelationship {
    @Id
    @GeneratedValue
    private Long id;

    @StartNode
    private Person parent;

    @EndNode
    private Person child;

    @Property
    private String relation;
}
