package connectors;

import com.mongodb.client.*;
import org.bson.Document;

public class POIConnector {
    static MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017");
    static MongoDatabase database =  mongoClient.getDatabase("LargeScaleProject");
    static MongoCollection<Document> users = database.getCollection("users");
    static MongoCollection<Document> cities = database.getCollection("cities");
    static MongoCollection<Document> POIs = database.getCollection("POIs");
    static MongoCollection<Document> reviews = database.getCollection("reviews");

    public static void printCollection(String nomeCollection){
        MongoCollection<Document> collection;
        switch(nomeCollection){
            case "users":
                collection = users;
                break;
            case "POIs":
                collection = POIs;
                break;
            case "cities":
                collection = cities;
                break;
            case "reviews":
                collection = reviews;
                break;
            default:
                throw new IllegalArgumentException("collection name missing");

        }
        try (MongoCursor<Document> cursor = collection.find().iterator())
        {
            while (cursor.hasNext())
            {
                System.out.println(cursor.next().toJson());
            }
        }
    }
    public static int insertMultiplePOIs(String jsonPath){
        //codice che dato il path carica tutto sul mongoDB
        return 0;
    }
    public static void close(){
        mongoClient.close();
    }
}
