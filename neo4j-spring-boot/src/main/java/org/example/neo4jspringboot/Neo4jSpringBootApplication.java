package org.example.neo4jspringboot;

import org.eclipse.core.runtime.CoreException;
import org.example.neo4jspringboot.service.ProjectEntry;
import org.example.neo4jspringboot.utils.BeanUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.neo4j.repository.config.EnableNeo4jRepositories;

import java.io.IOException;

@SpringBootApplication
@EnableNeo4jRepositories
@ComponentScan(basePackages = {"org.example.neo4jspringboot.*"})
public class Neo4jSpringBootApplication {
    public static void main(String[] args) {
        SpringApplication.run(Neo4jSpringBootApplication.class, args);

        ProjectEntry entry = BeanUtils.getBean(ProjectEntry.class);
        try {
            entry.run();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (CoreException e) {
            e.printStackTrace();
        }
    }
}
