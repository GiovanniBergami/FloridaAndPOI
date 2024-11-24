package mongoConnectors;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.result.DeleteResult;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

import static com.mongodb.client.model.Filters.eq;

public class CityConnector {
    static MongoCollection<Document> cities = mongoConnector.database.getCollection("cities");

    //CRUD

    //CREATE

    public static ObjectId createCity(String name){

        Document doc;
        doc =  new Document("name",name)
                .append("POI_count",0)
                .append("POI_ids",new ArrayList<>());
        cities.insertOne(doc);

        return doc.getObjectId("_id");
    }
    public static int createCityFromJson(String jsonPOI){
        Document doc = Document.parse(jsonPOI);
        cities.insertOne(doc);
        System.out.println("inserimento avvenuto: ");
        return 0;
    }
    public static int createCitiesFromJsonFile(String jsonPath){

        String line;
        Document doc;
        try {
            File file = new File(jsonPath);
            Scanner fileReader = new Scanner(file);

            while(fileReader.hasNextLine()){
                line = fileReader.nextLine();
                doc = Document.parse(line);
                cities.insertOne(doc);
            }
            fileReader.close();  //check if it gives errors. it shouldnt. if there are, delete it
        } catch(FileNotFoundException e){
            System.out.println(e.getMessage());
        }

        return 0;
    }

    //READ
    public static void printCollection(){

        try (MongoCursor<Document> cursor = cities.find().iterator())
        {
            while (cursor.hasNext())
            {
                System.out.println(cursor.next().toJson());
            }
        }
    }
    public static void printCollection(int number){
        int i=0;
        try (MongoCursor<Document> cursor = cities.find().iterator())
        {
            while (cursor.hasNext() && i<number)
            {
                System.out.println(cursor.next().toJson());
                i++;
            }
        }
    }

    public static Document readCity(String name){
        Document doc;
        doc = cities.find(eq("name", name)).first();

        if(doc != null) {
            return doc;
        }
        doc = new Document("name","0");
        return doc;
    }
    //UPDATE


    public static boolean addPOIToCity(ObjectId city_id, ObjectId poi_id){
        Document filter = new Document("_id",city_id);
        Document updateOperation = new Document("$push",new Document("POI_ids",poi_id));
        try {
            cities.updateOne(filter, updateOperation);
            return true;
        }catch(Exception e){
            System.out.println(e.getMessage());
            return false;
        }
    }

    public static boolean addPOIToCityName(String city_name, ObjectId poi_id){ //sperimental
        Document filter = new Document("name",city_name);
        Document updateOperation = new Document("$push",new Document("POI_ids",poi_id));
        try {
            cities.updateOne(filter, updateOperation);
            return true;
        }catch(Exception e){
            System.out.println(e.getMessage());
            return false;
        }
    }
    //DELETE
    public static boolean remove(String name){
        Document filter = new Document("name",name);
        DeleteResult result = cities.deleteOne(filter);
        if(result.getDeletedCount()>0){
            return true;
        }else{
            return false;
        }

    }

    public static boolean removePOIFromCity(String city_name, ObjectId poi_id){
        Document filter = new Document("name",city_name);
        Document updateOperation = new Document("$pull",new Document("POI_ids",poi_id));
        try {
            cities.updateOne(filter, updateOperation);
            return true;
        }catch(Exception e){
            System.out.println(e.getMessage());
            return false;
        }
    }

    //ALTRE FUNZIONI
    public static void count(){
        long count = cities.countDocuments();
        System.out.println("number of cities: " + count);
    }

}
