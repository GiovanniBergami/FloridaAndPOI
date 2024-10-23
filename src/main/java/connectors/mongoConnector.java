package connectors;

import com.mongodb.client.*;
import org.bson.Document;

public class mongoConnector {
    static MongoClient mongoClient;
    static MongoDatabase database;
    static MongoCollection<Document> collection;
    public mongoConnector(String nameDatabase,String nameCollection){
        mongoClient = MongoClients.create("mongodb://localhost:27017"); //sarebbe il caso di usare una sola istanza, quindi forse questa classe è inutile
        database = mongoClient.getDatabase(nameDatabase); //da vedere come hanno fatto gli altri
        collection = database.getCollection(nameCollection);
    }
    public static void printCollection(){

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
