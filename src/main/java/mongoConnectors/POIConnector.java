package mongoConnectors;

import com.mongodb.client.*;
import org.bson.Document;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class POIConnector {
    static MongoCollection<Document> POIs = mongoConnector.database.getCollection("POIs");

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
    public static void count(){
        long count = POIs.countDocuments();
        System.out.println("number of POIs: " + count);
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
        } catch(FileNotFoundException e){
            System.out.println(e.getMessage());
        }

        return 0;
    }

}
