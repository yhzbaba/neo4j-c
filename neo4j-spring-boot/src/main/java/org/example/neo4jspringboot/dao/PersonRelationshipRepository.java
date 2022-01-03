package org.example.neo4jspringboot.dao;

import org.example.neo4jspringboot.entity.PersonRelationship;
import org.springframework.data.neo4j.repository.Neo4jRepository;

public interface PersonRelationshipRepository extends Neo4jRepository<PersonRelationship, Long> {
}
