package mongoConnectors;

import com.mongodb.client.*;
import org.bson.Document;

public class mongoConnector {
    static public MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017");
    static public MongoDatabase database =  mongoClient.getDatabase("LargeScaleProject");

    public static void close(){
        mongoClient.close();
    }
}
