package org.example.neo4jspringboot.dao;

import org.example.neo4jspringboot.entity.CAliaInfo;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CAliaRepository extends Neo4jRepository<CAliaInfo, Long> {
}
