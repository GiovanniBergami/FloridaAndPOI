package mongoConnectors;

import com.mongodb.client.*;
import org.bson.Document;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import static com.mongodb.client.model.Filters.eq;

public class ReviewConnector {
    static MongoCollection<Document> reviews = mongoConnector.database.getCollection("reviews");
//READ
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
        System.out.println("number of reviews: " + count);
    }


    public static Document getReview(String id){
        Document review = reviews.find(eq("review_id",id)).first();

        if(review != null) {
            return review;
        }
        review = new Document("name","0");
        return review;

    }
    //CREATE
    public static int insertReviews(String jsonPath) {

        String line;
        Document doc;
        try {
            File file = new File(jsonPath);
            Scanner fileReader = new Scanner(file);

            while (fileReader.hasNextLine()) {
                line = fileReader.nextLine();
                doc = Document.parse(line);
                reviews.insertOne(doc);  //check if it gives errors. it shouldnt. if there are, delete it
            }
            fileReader.close();
        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
        }
        return 0;
    }

    public static org.bson.types.ObjectId insertReview(String username, String date, String text, int rating){

        Document doc;
        doc =  new Document("name",username)
                        .append("date",date)
                        .append("text",text)
                        .append("stars",rating);
        reviews.insertOne(doc);

        return doc.getObjectId("_id");
    }

}
