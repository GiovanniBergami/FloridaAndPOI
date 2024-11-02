package neoConnectors;

import org.neo4j.driver.*;

import java.util.Map;
import java.util.concurrent.TimeUnit;

public class neoConnector {

    final static String dbUri = "neo4j://localhost:7687"; // URI examples: "neo4j://localhost", "neo4j+s://xxx.databases.neo4j.io"
    final static String dbUser = "neo4j";
    final static String dbPassword = "12345678";

    public static Driver driver;

    public static void connect(){
        var config = Config.builder()
                .withMaxConnectionLifetime(30, TimeUnit.MINUTES)
                .withMaxConnectionPoolSize(10)
                .withConnectionAcquisitionTimeout(2,TimeUnit.MINUTES)
                .build();
        driver = GraphDatabase.driver(dbUri, AuthTokens.basic(dbUser, dbPassword),config);
        driver.verifyConnectivity();
    }

    public static void prova(){

        var result = driver.executableQuery("MATCH (p:Person {born: $born}) RETURN p.name AS name")
                .withParameters(Map.of("born", 1956))
                .withConfig(QueryConfig.builder().withDatabase("neo4j").build())
                .execute();
        System.out.println("il risultato è " + result.records().toString());



    }
    public static void close() {
    driver.close();
    }

}
