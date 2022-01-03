package org.example.neo4jspringboot.dao;

import org.example.neo4jspringboot.entity.CFieldInfo;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CFieldRepository extends Neo4jRepository<CFieldInfo, Long> {
    @Query("match (n:`c-field-node` {name:{0}}), (m:`c-struct-node` {name:{1}}) " +
            "create (n)-[:memberof{relation:{2}}]->(m)")
    void createFieldMemberOfDataStructureR(String from, String to, String relation);
}
