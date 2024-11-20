package mongoConnectors;

import com.mongodb.client.*;
import com.mongodb.client.model.Filters;
import com.mongodb.client.result.DeleteResult;
import neoConnectors.neoConnector;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Scanner;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;


public class UserConnector {
    static MongoCollection<Document> users = mongoConnector.database.getCollection("users");

    //CREATE
    public static int insertUsers(String jsonPath) {

        String line;
        Document doc;
        try {
            File file = new File(jsonPath);
            Scanner fileReader = new Scanner(file);

            while (fileReader.hasNextLine()) {
                line = fileReader.nextLine();
                doc = Document.parse(line);
                users.insertOne(doc);
            }
            fileReader.close();  //check if it gives errors. it shouldnt. if there are, delete it
        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
        }
        return 0;
    }

    public static ObjectId createUser(String username,String password,int age){

        Document doc;

        doc =  new Document("name",username)
                .append("password",password)
                .append("age",age);
        try {
            users.insertOne(doc);
            return doc.getObjectId("_id");
        }catch(Exception e){
            System.out.println(e.getMessage());
            return null;
        }

    }

    //READ

    public static Document findUser(String username,String password,boolean admin){
        Document doc;
        if(admin) {
            doc = users.find(and(eq("name", username), eq("password", password), eq("admin", "1"))).first();
        }else {
            doc = users.find(and(eq("name", username), eq("password", password))).first();
        }

        if(doc == null) {
            doc = new Document("name","0");
        }
        return doc;
    }

    public static Document findUser(String username){
        Document doc;
        doc = users.find(eq("name", username)).first();
        if(doc == null) {
            doc = new Document("name","0");
        }
        return doc;
    }
    public static void printCollection(){

        try (MongoCursor<Document> cursor = users.find().iterator())
        {
            while (cursor.hasNext())
            {
                System.out.println(cursor.next().toJson());
            }
        }
    }
    public static void printCollection(int number){
        int i=0;
        try (MongoCursor<Document> cursor = users.find().iterator())
        {
            while (cursor.hasNext() && i<number)
            {
                System.out.println(cursor.next().toJson());
                i++;
            }
        }
    }

    public static void createNeoCollection(){ //put users in neo db
        int i=0;
        try (MongoCursor<Document> cursor = users.find().iterator())
        {
            Document doc;
            while (cursor.hasNext())
            {
                doc = cursor.next();
                neoConnector.addUser(doc.getString("name"),doc.getObjectId("_id").toString());
                i++;
            }
            System.out.println(i);
        }
    }
    public static void createNeoFriendship(){ //put users in neo db
        int i=0;
        try (MongoCursor<Document> cursor = users.find().iterator())
        {
            Document doc;
            while (cursor.hasNext())
            {
                doc = cursor.next();
                String current = doc.getObjectId("_id").toString();
                List<ObjectId> friends = doc.getList("friends", ObjectId.class);
                for(ObjectId f : friends){
                    neoConnector.addFriendshipGivenMongoId(current,f.toString());
                }
                i++;
            }
            System.out.println(i);
        }
    }
    public static void count(){
        long count = users.countDocuments();
        System.out.println("number of users: " + count);
    }

    //UPDATE

    //DELETE
    public static boolean remove(ObjectId id){
        Document filter = new Document("_id",id);
        try {
            DeleteResult result = users.deleteOne(filter);
            if(result.getDeletedCount()>0){
                return true;
            }else{
                return false;
            }
        }catch(Exception e){
            System.out.println(e.getMessage());
            return false;
        }
    }
    public static boolean updateFieldUser(ObjectId user_id,String field,String value){ //sarebbe più veloce fare una operazione sola, ma non è un problema tanto dovrebbe accadere molto raramente.
        Document filter = new Document("_id",user_id);
        Document updateOperation = new Document("$set",new Document(field,value));
        users.updateOne(filter,updateOperation);
        return true;
    }


}
