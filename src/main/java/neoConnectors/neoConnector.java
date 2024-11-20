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

    public static boolean addUser(String name,String mongoId){
        try(Session session = driver.session()){
            String query = "CREATE (u:User {name: $name,mongoId: $mongoId}) RETURN u";
            Result result = session.run(query,
                    Values.parameters("name", name,"mongoId",mongoId));

            // Recupera e stampa i dettagli dell'utente creato
//            var createdUser = result.single().get(0).asNode();
//            System.out.println("Utente creato: " + createdUser.get("name").asString() +
//                    ", mongoId: "+ createdUser.get("mongoId"));
        }catch(Exception e){
            System.out.println(e.getMessage());
            return false;
        }
        return true;
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

    public static boolean deleteUser(String userNameToRemove){
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
                return true;
            } else {
                System.out.println("Nessun utente trovato con il nome: " + userNameToRemove);
                return false;
            }
        }catch(Exception e){
            System.out.println(e.getMessage());
            return false;
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

    public static boolean addVisit(String poiId, String name, Double stars, String date) {
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
            return true;
        }catch(Exception e){
            System.out.println(e.getMessage());
            return false;
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

    public static boolean removeVisit(String poiId, String name,  String date) {
        try(Session session = driver.session()) {
            // Esegui la query per creare una relazione di visita. ci sarà da vedere se le stelle servono in entrambi i versi, in caso si fa in fretta a toglierle
            String query = """
                        MATCH (u1:POI {mongoId: $poiId})-[r:VISIT{date:$date}]-(u2:User {name: $userName})
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
            return true;
        }catch(Exception e){
            System.out.println(e.getMessage());
            return false;
        }
    }
    public static List<String> getPlans(String userName){
        List<String> plans = new ArrayList<>();
        try(Session session = driver.session()) {
            // Esegui la query per creare una relazione di visita. ci sarà da vedere se le stelle servono in entrambi i versi, in caso si fa in fretta a toglierle
            String query = """
                        MATCH (u1:User {name: $userName})-[r:PLAN]-(poi)
                        RETURN r as rel, poi as poi
                    """;
//            System.out.println(poiId+" " + name + " " + stars.intValue()+ " " +date);
            Result result = session.run(query,
                    org.neo4j.driver.Values.parameters( "userName", userName));
            var ris = result.list();
            ris.forEach(plan -> {
                plans.add("Date: "+ plan.get("rel").get("date").asString()+
                        "\nWhere: " +plan.get("poi").get("name").asString()+
                        "\nPOI id: " + plan.get("poi").get("mongoId").asString());
            });
        }
        return plans;
    }

    public static void updatePlans(){  //remove any plan already expired
        try(Session session = driver.session()){
            String query = """
                        MATCH (:User)-[r:PLAN]->(:POI)
                        WHERE date(r.date) < date()
                        DELETE r
                    """;
            Result result = session.run(query);
        }
    }
    public static void deletePOI(String mongoIdToRemove){
        try(Session session = driver.session()){
            String query = """
                MATCH (u:POI {mongoId: $mongoId})
                DETACH DELETE u
                RETURN u
            """;

            Result result = session.run(query,
                    org.neo4j.driver.Values.parameters("mongoId", mongoIdToRemove));

            // Controlla se l'utente è stato effettivamente rimosso
            if (result.hasNext()) {
                System.out.println("poi rimosso da neo: " + mongoIdToRemove);
            } else {
                System.out.println("Nessun poi trovato con il nome: " + mongoIdToRemove);
            }
        }
    }

    public static void updatePOI(String mongoId,String newName){
        try(Session session = driver.session()){
            String query = """
                MATCH (u:POI {mongoId: $mongoId})
                SET u.name = $newName
                RETURN u
            """;

            Result result = session.run(query,
                    org.neo4j.driver.Values.parameters("mongoId", mongoId, "newName",newName));

            // Controlla se l'utente è stato effettivamente rimosso
            if (result.hasNext()) {
                System.out.println("poi aggiornato su neo: " + mongoId);
            } else {
                System.out.println("Nessun poi trovato con il nome: " + mongoId);
            }
        }
    }

    public static List<String> findCoincidence(String mongoId,String userName){
        List<String> coincidences = new ArrayList<>();
        try(Session session = driver.session()){
            String query = """
                MATCH (p:POI {mongoId: $mongoId})<-[r:PLAN]-(f:User)-[:FRIENDS]-(u:User{name : $username})
                RETURN f as friend,r as rel
            """;

            Result result = session.run(query,
                    org.neo4j.driver.Values.parameters("mongoId", mongoId, "username",userName));

            var ris = result.list();
            ris.forEach(plan -> {
                coincidences.add("Date: "+ plan.get("rel").get("date").asString()+
                        "   User: " + plan.get("friend").get("name").asString());
            });

        }
        return coincidences;
    }
    public static List<String> recommendPOI(String userName){
        List<String> recommendations = new ArrayList<>();

        try(Session session = driver.session()){
            String query = """
                MATCH (u:User{name:$userName})-[:FRIENDS]-(f:User)-[v:VISIT]-(p:POI)
                RETURN f.name as friend, p as poi , v.stars as stars
                ORDER BY stars DESC
                LIMIT 10
            """;

            Result result = session.run(query,
                    org.neo4j.driver.Values.parameters( "userName",userName));

            var ris = result.list();
            ris.forEach(r -> {
                recommendations.add("Friend Name: "+ r.get("friend").asString()+
                        "   POI: " + r.get("poi").get("name").asString()+ " "+r.get("poi").get("mongoId").asString()+
                        " stars: "+r.get("stars").toString());
            });

        }
        return recommendations;
    }
    public static List<String> similarUser(String userName){ //provarla con Jfliv
        List<String> similars = new ArrayList<>();

        try(Session session = driver.session()){
            String query = """
                MATCH (u1:User{name:$userName})-[v1:VISIT]->(p:POI)<-[v2:VISIT]-(u2:User)
                WHERE u1.name <> u2.name
                AND abs(v1.stars - v2.stars) < 3
                WITH u1,u2,COLLECT(DISTINCT {mongoId: p.mongoId,POIname: p.name}) as commonPOIs, COUNT(p) as count
                RETURN u2.name as name, commonPOIs as commonPOIs, count as count
                ORDER BY count DESC
                LIMIT 10
            """;

            Result result = session.run(query,
                    org.neo4j.driver.Values.parameters( "userName",userName));
            List<String> collection = new ArrayList<>();

            var ris = result.list();
            ris.forEach(r -> {
                similars.add("Username: "+ r.get("name").asString()+
                        " count: "+r.get("count").toString()+
                        "poi: "+ r.get("commonPOIs").asList());

            });

        }
        return similars;
    }
    public static List<String> findFriendPlans(String userName){
        List<String> coincidences = new ArrayList<>();
        try(Session session = driver.session()){
            String query = """
                MATCH (p:POI)<-[r:PLAN]-(f:User)-[:FRIENDS]-(u:User{name : $username})
                RETURN f as friend,r as rel, p as poi
            """;

            Result result = session.run(query,
                    org.neo4j.driver.Values.parameters( "username",userName));

            var ris = result.list();
            ris.forEach(plan -> {
                coincidences.add("POI name: "+plan.get("poi").get("name").asString()+
                        "POI id: "+plan.get("poi").get("mongoId").asString()+
                        "Date: "+ plan.get("rel").get("date").asString()+
                        "   User: " + plan.get("friend").get("name").asString());
            });

        }
        return coincidences;
    }

}

/* CANVAS
public static void deleteFriendship(String name1,String name2){
        try(Session session = driver.session()){

        }
    }

// CALL apoc.import.graphml('/tmp/complete-graph.graphml', {batchSize: 10000, storeNodeIds: false}) per ripristinare il graph


 */