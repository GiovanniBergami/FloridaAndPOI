package mongoConnectors;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.ReadPreference;
import com.mongodb.WriteConcern;
import com.mongodb.client.*;
import org.bson.Document;

public class mongoConnector {
//    static public MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017");
//    static public MongoDatabase database =  mongoClient.getDatabase("LargeScaleProject");

    static ConnectionString uri = new ConnectionString("mongodb://localhost:27018,localhost:27019,localhost27020/");
    static MongoClientSettings mcs = MongoClientSettings.builder()
            .applyConnectionString(uri)
            .readPreference(ReadPreference.nearest())
            .retryWrites(true)
            .writeConcern(WriteConcern.ACKNOWLEDGED).build();
    static public MongoClient mongoClient = MongoClients.create(mcs);

    //sopra nuovo modo
    static public MongoDatabase database =  mongoClient.getDatabase("LargeScale");

    public static void close(){
        mongoClient.close();
    }
}
