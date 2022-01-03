package org.example.neo4jspringboot.dao;

import org.example.neo4jspringboot.entity.CFunctionInfo;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CFunctionRepository extends Neo4jRepository<CFunctionInfo, Long> {
    @Query("match (n:`c-function-node` {belongToName:{0}}), (m:`c-function-node` {belongToName:{1}}) " +
            "create (n)-[:invoke{relation:{2}}]->(m)")
    void createFunctionInvokeFunctionR(String from, String to, String relation);

    @Query("match (n:`c-function-node`{name: {0}}) return n")
    List<CFunctionInfo> getFunctionFromName(String name);
}
