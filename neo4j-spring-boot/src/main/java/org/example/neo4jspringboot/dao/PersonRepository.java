package org.example.neo4jspringboot.dao;

import org.example.neo4jspringboot.entity.Person;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PersonRepository extends Neo4jRepository<Person, Long> {
    @Query("match (n:person {name:{0}}), (m:person {name:{2}}) " +
    "create (n)-[:xiyou{relation:{1}}]->(m)")
    void createRelationship(String from, String relation, String to);
}
