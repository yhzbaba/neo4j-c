package org.example.neo4jspringboot.entity;

import lombok.Data;
import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Property;

@Data
@NodeEntity(label = "c-field-node")
public class CFieldInfo {
    @Id
    @GeneratedValue
    private Long id;

    @Property
    private String name;

    /**
     * 默认为int 因为enum
     */
    @Property
    private String type = "int";
}
