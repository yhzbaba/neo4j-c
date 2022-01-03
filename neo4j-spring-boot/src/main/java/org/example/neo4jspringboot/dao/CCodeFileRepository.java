package org.example.neo4jspringboot.dao;

import org.example.neo4jspringboot.entity.CCodeFileInfo;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CCodeFileRepository extends Neo4jRepository<CCodeFileInfo, Long> {
    @Query("match (n:`c-code-file-node` {fileName:{0}}), (m:`c-code-file-node` {fileName:{1}}) " +
            "create (n)-[:include{relation:{2}}]->(m)")
    void createCodeFileIncludeCodeFileR(String from, String to, String relation);

    @Query("match (n:`c-code-file-node` {fileName:{0}}), (m:`c-struct-node` {name:{1}}) " +
            "create (n)-[:define{relation:{2}}]->(m)")
    void createCodeFileDefineDataStructureR(String from, String to, String relation);

    @Query("match (n:`c-code-file-node` {fileName:{0}}), (m:`c-variable-node` {name:{1}}) " +
            "create (n)-[:define{relation:{2}}]->(m)")
    void createCodeFileDefineVariableR(String from, String to, String relation);

    @Query("match (n:`c-code-file-node` {fileName:{0}}), (m:`c-function-node` {belongToName:{1}}) " +
            "create (n)-[:define{relation:{2}}]->(m)")
    void createCodeFileDefineFunctionR(String from, String to, String relation);

    @Query("match (n:c-code-file-node {name:{0}}), (m:c-alia-node {name:{1}}) " +
            "create (n)-[:define{relation:{2}}]->(m)")
    void createCodeFileDefineAliaR(String from, String to, String relation);
}
