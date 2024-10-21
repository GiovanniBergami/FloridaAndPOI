package connectors;

import com.mongodb.client.*;
import org.bson.Document;

public class mongoConnector {
    MongoClient mongoClient;
    MongoDatabase database;
    MongoCollection<Document> collection;
    public mongoConnector(String nameDatabase,String nameCollection){
        mongoClient = MongoClients.create("mongodb://localhost:27017"); //sarebbe il caso di usare una sola istanza, quindi forse questa classe è inutile
        database = mongoClient.getDatabase(nameDatabase); //da vedere come hanno fatto gli altri
        collection = database.getCollection(nameCollection);
    }
    public void printCollection(){

        try (MongoCursor<Document> cursor = collection.find().iterator())
        {
            while (cursor.hasNext())
            {
                System.out.println(cursor.next().toJson());
            }
        }
    }
    public void close(){
        mongoClient.close();
    }
}
