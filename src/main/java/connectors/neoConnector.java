package connectors;

import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.GraphDatabase;
import org.neo4j.driver.QueryConfig;

import java.util.Map;

public class neoConnector {

    final static String dbUri = "neo4j://localhost:7687"; // URI examples: "neo4j://localhost", "neo4j+s://xxx.databases.neo4j.io"
    final static String dbUser = "neo4j";
    final static String dbPassword = "12345678";
    public static void connect(){
        try (var driver = GraphDatabase.driver(dbUri, AuthTokens.basic(dbUser, dbPassword))) {
            driver.verifyConnectivity();
            System.out.println("Connection established.");
            var result = driver.executableQuery("MATCH (p:Person {born: $born}) RETURN p.name AS name")
                    .withParameters(Map.of("born", 1956))
                    .withConfig(QueryConfig.builder().withDatabase("neo4j").build())
                    .execute();
            System.out.println("il risultato è "+result.records().toString());

        }
    }
}
