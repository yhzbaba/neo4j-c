package org.example.neo4jspringboot.entity;

import lombok.Data;
import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Property;

/**
 * typedef long long ll
 */
@Data
@NodeEntity(label = "c-alia-node")
public class CAliaInfo {
    @Id
    @GeneratedValue
    private Long id;

    @Property
    private String name;

    @Property
    private String originType;

    @Property
    private String comment;
}
