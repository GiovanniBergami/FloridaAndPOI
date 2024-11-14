package mongoConnectors;

import com.mongodb.client.*;
import com.mongodb.client.result.DeleteResult;
import neoConnectors.neoConnector;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;



import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;



public class POIConnector {
    static MongoCollection<Document> POIs = mongoConnector.database.getCollection("POIs");

    //CRUD

    //CREATE

    public static org.bson.types.ObjectId insertPOI(String name, String address, String city){

        Document doc;
        doc =  new Document("name",name)
                .append("address",address)
                .append("city",city)
                .append("reviews_ids",new ArrayList<>())
                .append("reviews_count",0)
                .append("visit_count",0);
        POIs.insertOne(doc);

        return doc.getObjectId("_id");
    }
    public static int insertPOI(String jsonPOI){
        Document doc = Document.parse(jsonPOI);
        POIs.insertOne(doc);
        System.out.println("inserimento avvenuto: ");
        return 0;
    }
    public static int insertPOIs(String jsonPath){

//        String line;
//        Document doc;
//
//        try {
////            File file = new File(jsonPath);
////            Scanner fileReader = new Scanner(file);
////
////            while(fileReader.hasNextLine()){
////                line = fileReader.nextLine();
////                doc = Document.parse(line);
////                POIs.insertOne(doc);
////            }
//
//            //fileReader.close();  //check if it gives errors. it shouldnt. if there are, delete it
//        } catch(FileNotFoundException e){
//            System.out.println(e.getMessage());
//        }

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
    public static boolean updateFieldPOI(ObjectId poi_id,String field,String value){ //sarebbe più veloce fare una operazione sola, ma non è un problema tanto dovrebbe accadere molto raramente.
        Document filter = new Document("_id",poi_id);
        Document updateOperation = new Document("$set",new Document(field,value));
        POIs.updateOne(filter,updateOperation);
        return true;
    }

    public static boolean addReviewToPOI(ObjectId poi_id, ObjectId review_id){
        Document filter = new Document("_id",poi_id);
        Document updateOperation = new Document("$push",new Document("review_ids",review_id));
        POIs.updateOne(filter,updateOperation);
        return true;
    }
    //DELETE
    public static boolean remove(ObjectId id){
        Document filter = new Document("_id",id);
        DeleteResult result = POIs.deleteOne(filter);
        if(result.getDeletedCount()>0){
            return true;
        }else{
            return false;
        }

    }

    public static boolean removeReviewFromPOI(ObjectId poi_id, ObjectId review_id){
        Document filter = new Document("_id",poi_id);
        Document updateOperation = new Document("$pull",new Document("review_ids",review_id));
        POIs.updateOne(filter,updateOperation);
        return true;
    }

    //ALTRE FUNZIONI
    public static void count(){
        long count = POIs.countDocuments();
        System.out.println("number of POIs: " + count);
    }

    public static void createNeoCollection(){ //put POIs in neo db
        int i=0;
        try (MongoCursor<Document> cursor = POIs.find().iterator())
        {
            Document doc;
            while (cursor.hasNext())
            {
                doc = cursor.next();
                neoConnector.addPOI(doc.getString("name"),doc.getObjectId("_id").toString());
                i++;
            }
            System.out.println(i);
        }
    }
    public static void createNeoVisits(){ //put visits in neo based on reviews
        int i=0;
        try (MongoCursor<Document> cursor = POIs.find().iterator())
        {
            Document poi;
            Document review;
            String user_id;
            while (cursor.hasNext())
            {
                poi = cursor.next();
                String poi_id = poi.getObjectId("_id").toString();
                List<ObjectId> review_ids = poi.getList("review_ids", ObjectId.class);
                for(ObjectId review_id: review_ids){
                    review = ReviewConnector.getReview(review_id);
                   // System.out.println(review.toJson());
                    //System.out.println(review.getString("name"));
                    neoConnector.addVisit(poi_id,review.getString("username"),review.getDouble("stars"),review.getString("date")); //aggiungiamo una visita tra il poi e l'user. assumiamo ci sia un indice sul nome dell'user sennò è un casino
                }

                i++;
            }
            System.out.println(i);
        }
    }
    public static String poiOfcityStatisticsDeprecated(String cityName){
        //per una città si può vedere per ogni poi il numero di reviews, l'average rating, il numero di reviews e avg rating divisi per fascia di età della popolazione


        List<Document> pipeline = Arrays.asList(
                new Document("$match",new Document("city",cityName)),
                new Document("$project",new Document("reviews_count_for_age",1)
                        .append("reviews_count",1)
                        .append("name",1)
                        .append("stars",1)
                        .append("totStars",1)
                        .append("_id",0))

        );
        AggregateIterable<Document> results = POIs.aggregate(pipeline);
        String output = "";

        for(Document doc : results){
            if(doc.getInteger("reviews_count")==0){
                output = output +"\nName: " + doc.getString("name") +
                        " ".repeat(30 - doc.getString("name").length())+
                        "avg stars: Nessuna recensione";
            }else {
                output = output + "\nName: " + doc.getString("name") +
                        " ".repeat(30 - doc.getString("name").length())+
                        "avg stars: " + ((float) doc.getInteger("totStars")) / ((float) doc.getInteger("reviews_count"))+
                        "   n reviews: "+ doc.getInteger("reviews_count")+
                        "   tot stars: "+ doc.getInteger("totStars")+
                        "\n visits by age: "+ doc.getList("reviews_count_for_age", Integer.class).toString() +
                        "\n tot stars by age: "+ doc.getList("stars", Integer.class).toString() +
                        "\n stars by age: " + Arrays.toString(divideArrays(doc.getList("stars", Integer.class), doc.getList("reviews_count_for_age", Integer.class)));
            }

        }


//        results.into(new ArrayList<>())
        return output;
    }
    public static float[] divideArrays(List<Integer> a, List<Integer> b){
        float[] r = new float[a.size()];
        for(int i = 0;i<a.size();i++){
            r[i] = ((float)a.get(i))/((float)b.get(i));
        }
        return r;
    }
    public static String poiOfCityStatistics(String cityName){
        List<Document> pipeline = Arrays.asList(
                // Filtro per città
                new Document("$match", new Document("city", cityName)),

                // Proiezione dei campi con calcoli sui rapporti
                new Document("$project", new Document()
                        .append("name", 1)
                        .append("city", 1)
                        .append("totStars", 1)
                        .append("reviews_count", 1)
                        .append("avgStars", new Document("$cond", new Document()
                                .append("if", new Document("$gt", Arrays.asList("$reviews_count", 0)))
                                .append("then", new Document("$divide", Arrays.asList("$totStars", "$reviews_count")))
                                .append("else", 0)
                        ))
                        .append("ageGroupAvgStars", new Document("$map", new Document()
                                .append("input", new Document("$range", Arrays.asList(0, new Document("$size", "$stars"))))
                                .append("as", "index")
                                .append("in", new Document("$cond", new Document()
                                        .append("if", new Document("$gt", Arrays.asList(
                                                new Document("$arrayElemAt", Arrays.asList("$reviews_count_for_age", "$$index")), 0
                                        )))
                                        .append("then", new Document("$divide", Arrays.asList(
                                                new Document("$arrayElemAt", Arrays.asList("$stars", "$$index")),
                                                new Document("$arrayElemAt", Arrays.asList("$reviews_count_for_age", "$$index"))
                                        )))
                                        .append("else", 0)
                                ))
                        ))
                )
        );

        // Esecuzione della query di aggregazione
        AggregateIterable<Document> results = POIs.aggregate(pipeline);
        String output = "";
        // Iterazione e stampa dei risultati
        for (Document doc : results) {
            output = output + doc.toJson() + "\n";
        }
        return output;
    }

}
