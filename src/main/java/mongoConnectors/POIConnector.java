package mongoConnectors;

import com.mongodb.client.*;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;

public class POIConnector {
    static MongoCollection<Document> POIs = mongoConnector.database.getCollection("POIs");

    //CRUD

    //CREATE
    public static int insertPOI(String jsonPOI){
        Document doc = Document.parse(jsonPOI);
        POIs.insertOne(doc);
        System.out.println("inserimento avvenuto: ");
        return 0;
    }
    public static int insertPOIs(String jsonPath){

        String line;
        Document doc;
        try {
            File file = new File(jsonPath);
            Scanner fileReader = new Scanner(file);

            while(fileReader.hasNextLine()){
                line = fileReader.nextLine();
                doc = Document.parse(line);
                POIs.insertOne(doc);
            }
            fileReader.close();  //check if it gives errors. it shouldnt. if there are, delete it
        } catch(FileNotFoundException e){
            System.out.println(e.getMessage());
        }

        return 0;
    }

    //READ
    public static void printCollection(){

        try (MongoCursor<Document> cursor = POIs.find().iterator())
        {
            while (cursor.hasNext())
            {
                System.out.println(cursor.next().toJson());
            }
        }
    }
    public static void printCollection(int number){
        int i=0;
        try (MongoCursor<Document> cursor = POIs.find().iterator())
        {
            while (cursor.hasNext() && i<number)
            {
                System.out.println(cursor.next().toJson());
                i++;
            }
        }
    }

    public static Document findPOI(String name){
        Document doc;
        doc = POIs.find(eq("name", name)).first();

        if(doc != null) {
            return doc;
        }
        doc = new Document("name","0");
        return doc;
    }
    //UPDATE


    public static boolean addReviewToPOI(ObjectId poi_id, ObjectId review_id){
        Document filter = new Document("_id",poi_id);
        Document updateOperation = new Document("$push",new Document("review_ids",review_id));
        POIs.updateOne(filter,updateOperation);
        return true;
    }
    //DELETE


    //ALTRE FUNZIONI
    public static void count(){
        long count = POIs.countDocuments();
        System.out.println("number of POIs: " + count);
    }

}
