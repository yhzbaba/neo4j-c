package org.example.neo4jspringboot.dao;

import org.example.neo4jspringboot.entity.CVariableInfo;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CVariableRepository extends Neo4jRepository<CVariableInfo, Long> {

}
