package org.example.neo4jspringboot.dao;

import org.example.neo4jspringboot.entity.CDataStructureInfo;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CDataStructureRepository extends Neo4jRepository<CDataStructureInfo, Long> {
}
