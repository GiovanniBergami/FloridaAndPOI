package mongoConnectors;

import com.mongodb.client.*;
import org.bson.Document;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class ReviewConnector {
    static MongoCollection<Document> reviews = mongoConnector.database.getCollection("reviews");

    public static void printCollection(){

        try (MongoCursor<Document> cursor = reviews.find().iterator())
        {
            while (cursor.hasNext())
            {
                System.out.println(cursor.next().toJson());
            }
        }
    }
    public static void printCollection(int number){
        int i=0;
        try (MongoCursor<Document> cursor = reviews.find().iterator())
        {
            while (cursor.hasNext() && i<number)
            {
                System.out.println(cursor.next().toJson());
                i++;
            }
        }
    }
    public static void count(){
        long count = reviews.countDocuments();
        System.out.println("number of POIs: " + count);
    }
    public static int insertReviews(String jsonPath) {

        String line;
        Document doc;
        try {
            File file = new File(jsonPath);
            Scanner fileReader = new Scanner(file);

            while (fileReader.hasNextLine()) {
                line = fileReader.nextLine();
                doc = Document.parse(line);
                reviews.insertOne(doc);
            }
        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
        }
        return 0;
    }
}
