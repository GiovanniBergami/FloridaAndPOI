package mongoConnectors;

import com.mongodb.client.*;
import com.mongodb.client.result.DeleteResult;
import org.bson.Document;
import org.bson.types.ObjectId;

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


    public static Document getReview(ObjectId id){
        Document review = reviews.find(eq("_id",id)).first();

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
        doc =  new Document("username",username)
                        .append("date",date)
                        .append("text",text)
                        .append("stars",rating);
        try {
            reviews.insertOne(doc);
            return doc.getObjectId("_id");
        }catch(Exception e){
            System.out.println(e.getMessage());
            return null;
        }
    }

    //DELETE
    public static boolean remove(ObjectId id,String name){
        if(name.equals("")){
            Document filter = new Document("_id",id);
            try {
                DeleteResult result = reviews.deleteOne(filter);
                if(result.getDeletedCount()>0){
                    return true;
                }else{
                    return false;
                }
            }catch(Exception e){
                System.out.println(e.getMessage());
                return false;
            }

        }else{

            Document filter = new Document("_id",id)
                    .append("username",name);
            try {
                DeleteResult result = reviews.deleteOne(filter);
                if (result.getDeletedCount() > 0) {
                    return true;
                } else {
                    return false;
                }
            }catch(Exception e){
                System.out.println(e.getMessage());
                return false;
            }
        }


    }


}
