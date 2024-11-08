package neoConnectors;

import org.bson.Document;
import org.neo4j.driver.*;
import org.neo4j.driver.types.Relationship;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
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
    public static void prova2(){
        try(var session = driver.session(SessionConfig.builder().withDatabase(dbUser).build())){
            var people = session.executeRead(tx -> {
                var result = tx.run(""" 
                                           MATCH (p:Person) WHERE p.name STARTS WITH $filter
                                           RETURN p.name AS name ORDER BY name
                                           """,Map.of("filter","A"));
                return result.list();
            });
            people.forEach(person -> {
                System.out.println(person);
            });
        }
    }

    public static void importAll(){  //se non va non è necessario. in un uso reale gli user si segnano loro mano a mano. solo poi e città devono essere importati in bulk quando il servizio viene lanciato
        try{
            String line;
            Document doc;
            File file = new File("dataset/users.json");
            Scanner fileReader = new Scanner(file);

            while(fileReader.hasNextLine()){
                line = fileReader.nextLine();
                doc = Document.parse(line);
                //addUser(doc.getString("name"),doc.getInteger("age"),doc.getObjectId("_id").toString());
                System.out.println(""+doc.getString("name")+doc.getInteger("age")+doc.getObjectId("_id").toString());
            }

            fileReader.close();  //check if it gives errors. it shouldnt. if there are, delete it
        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void addUser(String name,String mongoId){
        try(Session session = driver.session()){
            String query = "CREATE (u:User {name: $name,mongoId: $mongoId}) RETURN u";
            Result result = session.run(query,
                    Values.parameters("name", name,"mongoId",mongoId));

            // Recupera e stampa i dettagli dell'utente creato
//            var createdUser = result.single().get(0).asNode();
//            System.out.println("Utente creato: " + createdUser.get("name").asString() +
//                    ", mongoId: "+ createdUser.get("mongoId"));
        }
    }

    public static void addPOI(String name,String mongoId){
        try(Session session = driver.session()){
            String query = "CREATE (u:POI {name: $name,mongoId: $mongoId}) RETURN u";
            Result result = session.run(query,
                    Values.parameters("name", name,"mongoId",mongoId));

            // Recupera e stampa i dettagli dell'utente creato
//            var createdUser = result.single().get(0).asNode();
//            System.out.println("Utente creato: " + createdUser.get("name").asString() +
//                    ", mongoId: "+ createdUser.get("mongoId"));
        }
    }
    public static void addFriendshipRequest(String requester, String objective){
        try(Session session = driver.session()) {
            // Esegui la query per creare una relazione di amicizia
            String query = """
                        MATCH (u1:User {name: $userName1}), (u2:User {name: $userName2})
                        WHERE NOT (u1)-[:FRIENDS]-(u2)
                        CREATE (u1)-[:REQUESTED]->(u2)
                        RETURN u1, u2
                    """;

            Result result = session.run(query,
                    org.neo4j.driver.Values.parameters("userName1", objective, "userName2", requester));

            // Recupera e stampa i dettagli degli utenti coinvolti
            if (result.hasNext()) {
                var record = result.single();
                var user1 = record.get(0).asNode();
                var user2 = record.get(1).asNode();
                System.out.println("Richiesta di amicizia creata tra: " +
                        user1.get("name").asString() + " e " +
                        user2.get("name").asString());
            }
        }
    }

    public static void addFriendship(String name1,String name2){
        try(Session session = driver.session()) {
            // Esegui la query per creare una relazione di amicizia
            String query = """
                        MATCH (u1:User {name: $userName1}), (u2:User {name: $userName2})
                        WHERE NOT (u1)-[:FRIENDS]-(u2)
                        CREATE (u2)-[:FRIENDS]->(u1)
                        RETURN u1, u2
                    """;

            Result result = session.run(query,
                    org.neo4j.driver.Values.parameters("userName1", name1, "userName2", name2));

            // Recupera e stampa i dettagli degli utenti coinvolti
            if (result.hasNext()) {
                var record = result.single();
                var user1 = record.get(0).asNode();
                var user2 = record.get(1).asNode();
                System.out.println("Relazione di amicizia creata tra: " +
                        user1.get("name").asString() + " e " +
                        user2.get("name").asString());
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public static void deleteRequest(String requester,String objective){
        try(Session session = driver.session()){
            String query = """
                MATCH (u1:User {name: $userName1})-[r:REQUESTED]->(u2:User {name: $userName2})
                DELETE r
                RETURN u1, u2
            """;

            Result result = session.run(query,
                    org.neo4j.driver.Values.parameters("userName1", objective, "userName2", requester));

            // Recupera e stampa i dettagli degli utenti coinvolti
            if (result.hasNext()) {
                var record = result.single();
                var user1 = record.get(0).asNode();
                var user2 = record.get(1).asNode();
                System.out.println("Richiesta di amicizia rimossa tra: " +
                        user1.get("name").asString() + " e " +
                        user2.get("name").asString());
            } else {
                System.out.println("Nessuna relazione rimossa. Verifica che la relazione esista.");
            }
        }
    }
    public static void deleteFriendship(String name1,String name2){
        try(Session session = driver.session()){
            String query = """
                MATCH (u1:User {name: $userName1})-[r:FRIENDS]-(u2:User {name: $userName2})
                DELETE r
                RETURN u1, u2
            """;

            Result result = session.run(query,
                    org.neo4j.driver.Values.parameters("userName1", name1, "userName2", name2));

            // Recupera e stampa i dettagli degli utenti coinvolti
            if (result.hasNext()) {
                var record = result.single();
                var user1 = record.get(0).asNode();
                var user2 = record.get(1).asNode();
                System.out.println("Relazione di amicizia rimossa tra: " +
                        user1.get("name").asString() + " e " +
                        user2.get("name").asString());
            } else {
                System.out.println("Nessuna relazione rimossa. Verifica che la relazione esista.");
            }
        }
    }

    public static void deleteUser(String userNameToRemove){
        try(Session session = driver.session()){
            String query = """
                MATCH (u:User {name: $userName})
                DETACH DELETE u
                RETURN u
            """;

            Result result = session.run(query,
                    org.neo4j.driver.Values.parameters("userName", userNameToRemove));

            // Controlla se l'utente è stato effettivamente rimosso
            if (result.hasNext()) {
                System.out.println("Utente rimosso: " + userNameToRemove);
            } else {
                System.out.println("Nessun utente trovato con il nome: " + userNameToRemove);
            }
        }
    }
    public static void getRequested(String userName){
        try(Session session = driver.session()){
            String query = """
                    MATCH (u:User {name : $userName} )-[r:REQUESTED]->(u2: User)
                    RETURN u2.name as name
                    """;
            Result result = session.run(query,
                    org.neo4j.driver.Values.parameters("userName", userName));
            var ris = result.list();
            ris.forEach(person -> {
                System.out.println(person.get("name").asString());
            });
        }
    }

    public static void getFriends(String userName){
        try(Session session = driver.session()){
            String query = """
                    MATCH (u:User {name : $userName} )-[r:FRIENDS]-(u2: User)
                    RETURN u2.name as name
                    """;
            Result result = session.run(query,
                    org.neo4j.driver.Values.parameters("userName", userName));
            var ris = result.list();
            ris.forEach(person -> {
                System.out.println(person.get("name").asString());
            });
        }
    }

    public static void close() {
    driver.close();
    }

    public static void addFriendshipGivenMongoId(String name1, String name2) {
        try(Session session = driver.session()) {
            // Esegui la query per creare una relazione di amicizia
            String query = """
                        MATCH (u1:User {mongoId: $userName1}), (u2:User {mongoId: $userName2})
                        WHERE NOT (u1)-[:FRIENDS]-(u2)
                        CREATE (u2)-[:FRIENDS]->(u1)
                        RETURN u1, u2
                    """;

            Result result = session.run(query,
                    org.neo4j.driver.Values.parameters("userName1", name1, "userName2", name2));

             //Recupera e stampa i dettagli degli utenti coinvolti
//            if (result.hasNext()) {
//                var record = result.single();
//                var user1 = record.get(0).asNode();
//                var user2 = record.get(1).asNode();
//                System.out.println("Relazione di amicizia creata tra: " +
//                        user1.get("name").asString() + " e " +
//                        user2.get("name").asString());
//            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public static void addVisit(String poiId, String name, Double stars, String date) {
        try(Session session = driver.session()) {
            // Esegui la query per creare una relazione di visita. ci sarà da vedere se le stelle servono in entrambi i versi, in caso si fa in fretta a toglierle
            String query = """
                        MATCH (u1:POI {mongoId: $poiId}), (u2:User {name: $userName})
                        WHERE NOT (u1)-[:VISIT{date:$date}]-(u2)
                        CREATE (u2)-[:VISIT{stars:$stars,date:$date}]->(u1)  
                        RETURN u1, u2
                    """;
//            System.out.println(poiId+" " + name + " " + stars.intValue()+ " " +date);
            Result result = session.run(query,
                    org.neo4j.driver.Values.parameters("poiId", poiId, "userName", name,"stars",stars.intValue(),"date",date));

            //Recupera e stampa i dettagli degli utenti coinvolti
//            if (result.hasNext()) {
//                var record = result.single();
//                var user1 = record.get(0).asNode();
//                var user2 = record.get(1).asNode();
//                System.out.println("Relazione di amicizia creata tra: " +
//                        user1.get("name").asString() + " e " +
//                        user2.get("name").asString());
//            }
        }
    }
    public static void addPlan(String poiId, String name,  String date) {
        try(Session session = driver.session()) {
            // Esegui la query per creare una relazione di visita. ci sarà da vedere se le stelle servono in entrambi i versi, in caso si fa in fretta a toglierle
            String query = """
                        MATCH (u1:POI {mongoId: $poiId}), (u2:User {name: $userName})
                        WHERE NOT (u1)-[:VISIT{date:$date}]-(u2)
                        CREATE (u2)-[:PLAN{date:$date}]->(u1)  
                        RETURN u1, u2
                    """;
//            System.out.println(poiId+" " + name + " " + stars.intValue()+ " " +date);
            Result result = session.run(query,
                    org.neo4j.driver.Values.parameters("poiId", poiId, "userName", name,"date",date));

            //Recupera e stampa i dettagli degli utenti coinvolti
//            if (result.hasNext()) {
//                var record = result.single();
//                var user1 = record.get(0).asNode();
//                var user2 = record.get(1).asNode();
//                System.out.println("Relazione di amicizia creata tra: " +
//                        user1.get("name").asString() + " e " +
//                        user2.get("name").asString());
//            }
        }
    }
    public static void removePlan(String poiId, String name,  String date) {
        try(Session session = driver.session()) {
            // Esegui la query per creare una relazione di visita. ci sarà da vedere se le stelle servono in entrambi i versi, in caso si fa in fretta a toglierle
            String query = """
                        MATCH (u1:POI {mongoId: $poiId})-[r:PLAN{date:$date}]-(u2:User {name: $userName})
                        DELETE  r  
                        RETURN u1, u2
                    """;
//            System.out.println(poiId+" " + name + " " + stars.intValue()+ " " +date);
            Result result = session.run(query,
                    org.neo4j.driver.Values.parameters("poiId", poiId, "userName", name,"date",date));

            //Recupera e stampa i dettagli degli utenti coinvolti
//            if (result.hasNext()) {
//                var record = result.single();
//                var user1 = record.get(0).asNode();
//                var user2 = record.get(1).asNode();
//                System.out.println("Relazione di amicizia creata tra: " +
//                        user1.get("name").asString() + " e " +
//                        user2.get("name").asString());
//            }
        }
    }
    public static List<String> getPlans(String userName){
        List<String> plans = new ArrayList<>();
        try(Session session = driver.session()) {
            // Esegui la query per creare una relazione di visita. ci sarà da vedere se le stelle servono in entrambi i versi, in caso si fa in fretta a toglierle
            String query = """
                        MATCH (u1:User {name: $userName})-[r:PLAN]-(poi)
                        RETURN r as rel, poi.name as name
                    """;
//            System.out.println(poiId+" " + name + " " + stars.intValue()+ " " +date);
            Result result = session.run(query,
                    org.neo4j.driver.Values.parameters( "userName", userName));
            var ris = result.list();
            ris.forEach(plan -> {
                Relationship relationship = plan.get("rel").asRelationship();

                plans.add(""+relationship.get("date")+ plan.get("name").toString()+plan.keys().toString());
            });
        }
        return plans;
    }

}

/* CANVAS
public static void deleteFriendship(String name1,String name2){
        try(Session session = driver.session()){

        }
    }

// CALL apoc.import.graphml('/tmp/complete-graph.graphml', {batchSize: 10000, storeNodeIds: false}) per ripristinare il graph


 */