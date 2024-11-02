package org.example;

import mongoConnectors.CityConnector;
import mongoConnectors.POIConnector;
import mongoConnectors.ReviewConnector;
import mongoConnectors.UserConnector;

import java.util.List;
import java.util.Scanner;

import neoConnectors.neoConnector;
import org.bson.Document;
import org.bson.types.ObjectId;


public class CLI {
     static Scanner scanner = new Scanner(System.in);
    public static Document sessionUser;
    public static void start(){
        boolean exit = false;
        System.out.println("Starting...");
        while(!exit) {
            System.out.println("Who are you? -- FASE PROVVISORIA, SI PARTE DA UNSIGNED USER");
            int c = chooseBetween(List.of("Admin","User","Unsigned User","Stop"),"> ");
            switch (c) {
                case 1:
                    admin();
                    break;
                case 2:
                    user();
                    break;
                case 3:
                    unsignedUser();
                    break;
                case 4:
                    exit = true;
                    break;
                default:
                    System.out.println("wrong input, retry");
            }
        }
    }
    public static void admin(){
        boolean exit = false;
        while(!exit){
            System.out.println("Choose option");
            int collection = chooseBetween(List.of("POIs","Reviews","Users","Cities","Analytics","Go back"),"admin> ");
            int action = 0;
            switch(collection){
                case 1:
                    action = chooseBetween(List.of("Create one","Read one","Update one","Delete one",
                            "Import collection from json","Read first n","Go back"),"admin/POI> ");
                    switch(action){
                        case 1: //create POI
                            createPOI();
                            break;
                        case 2:
                            Document poi = findPOI();
                            System.out.println(poi);
                            break;
                        case 3:
                            updatePOI();
                            break;
                        case 4:
                            deletePOI();
                            break;
                        case 5:
                            System.out.println("Insert POIs, specify path");
                            POIConnector.insertPOIs(scanner.nextLine());
                            POIConnector.count();
                            break;
                        case 6:
                            System.out.println("how many?");
                            int n = scanner.nextInt();
                            scanner.nextLine(); //toglie il \n del nextInt
                            POIConnector.printCollection(n);
                            POIConnector.count();
                            break;
                        case 7:
                            break;
                        default:
                            System.out.println("wrong input");
                    }
                    break;
                case 2:
                    action = chooseBetween(List.of("Read","Remove",
                            "Import collection from json","Read first n","Go back"),"admin/Reviews> ");
                    switch(action){
                        case 1:
                            findReviews(findPOI());
                            break;
                        case 2:
                            removeReview();
                            break;
                        case 3:
                            System.out.println("Insert Reviews, specify path");
                            ReviewConnector.insertReviews(scanner.nextLine());
                            ReviewConnector.count();
                            break;
                        case 4:
                            System.out.println("how many?");
                            int n = scanner.nextInt();
                            scanner.nextLine();
                            ReviewConnector.printCollection(n);
                            ReviewConnector.count();
                            break;
                        case 5:
                            break;
                        default:
                            System.out.println("wrong input");
                    }
                    break;
                case 3:
                    action = chooseBetween(List.of("Read","Delete","Update",
                            "Import collection from json","Read all collection","Go back"),"admin/Users> ");
                    switch(action){
                        case 1:
                            findUser();
                            break;
                        case 2:
                            removeUser();
                            break;
                        case 3:
                            System.out.println("insert id user -- funzione sperimentale");
                            ObjectId user = new ObjectId(scanner.nextLine());
                            System.out.println("insert field");
                            String field = scanner.nextLine();
                            System.out.println("insert value");
                            String value = scanner.nextLine();
                            UserConnector.updateFieldUser(user,field,value);
                            break;
                        case 4:
                            System.out.println("Insert Users, specify path");
                            UserConnector.insertUsers(scanner.nextLine());
                            UserConnector.count();
                            break;
                        case 5:
                            System.out.println("how many?");
                            int n = scanner.nextInt();
                            scanner.nextLine();
                            UserConnector.printCollection(n);
                            UserConnector.count();
                            break;
                        case 6:
                            break;
                        default:
                            System.out.println("wrong input");
                    }
                    break;
                case 4:
                    action = chooseBetween(List.of("Import",
                            "Import collection from json","Read all collection","Go back"),"admin/Cities>");
                    switch(action){
                        case 1:
                            System.out.println("Insert path");
                            CityConnector.insertCities(scanner.nextLine());
                            break;
                        case 2:
                            System.out.println("Insert Cities, specify path");
                            CityConnector.insertCities(scanner.nextLine());
                            CityConnector.count();
                            break;
                        case 3:
                            System.out.println("how many?");
                            int n = scanner.nextInt();
                            scanner.nextLine();
                            CityConnector.printCollection(n);
                            CityConnector.count();
                            break;
                        case 4:
                            break;
                        default:
                            System.out.println("wrong input");
                    }
                    break;
                case 5:
                    System.out.println("Analytics");
                    switch(chooseBetween(List.of("query1","query2","query3"),"")){
                        case 1:
                            break;
                        case 2:
                            System.out.println("todo");
                            break;
                        case 3:
                            System.out.println("todoo");
                            break;
                        default:
                            System.out.println("wrong input");
                    }
                    break;
                case 6:
                    exit = true;
                    break;
                default:
                    System.out.println("wrong input, retry");
            }
        }

    }


    public static void createPOI(){
        System.out.println("Insert city of the POI");
        String city = scanner.nextLine();
        Document cityOfPOI = CityConnector.findCity(city); //ci vorrà un index sui nomi di città
        if(cityOfPOI.getString("name").equals("0")){
            System.out.println("The city doesn't exist");
            return;
        }else{
            System.out.println("Insert name of POI");
            String name = scanner.nextLine();
            if(!POIConnector.findPOI(name).getString("name").equals("0")){
                System.out.println("This name already exists");
                return;
            }else{
                System.out.println("Insert address");
                String address = scanner.nextLine();
                ObjectId newPOIId =POIConnector.insertPOI(name,address,city);
                CityConnector.addPOIToCity(cityOfPOI.getObjectId("_id"),newPOIId);
                System.out.println("The POI has been added");
            }
        }
    }

    public static void deletePOI(){
        System.out.println("Insert the name of the POI you want to remove"); //bisogna rimuovere anche tutte le reviews
        String name = scanner.nextLine();
        Document poi = POIConnector.findPOI(name);
        if(poi.getString("name").equals("0")){
            System.out.println("poi not found");
            return;
        }

        ObjectId id = poi.getObjectId("_id");

        if(POIConnector.remove(id)){
            System.out.println("Deleted");
            List<ObjectId> review_ids = poi.getList("review_ids",ObjectId.class);
            for(ObjectId review_id : review_ids){
                ReviewConnector.remove(review_id,"");
            }
            CityConnector.removePOIFromCity(poi.getString("city"),poi.getObjectId("_id"));
        }else{
            System.out.println("Not deleted");
        };
    }

    public static void removeReview(){
        System.out.println("Insert the _id of the review you want to remove");
        ObjectId review_id = new ObjectId(scanner.nextLine());
        System.out.println("Insert the _id of the POI of the review");
        ObjectId poi_id = new ObjectId(scanner.nextLine());
        if(ReviewConnector.remove(review_id,"")){
            POIConnector.removeReviewFromPOI(poi_id,review_id);
            System.out.println("Deleted");
        }else{
            System.out.println("Not deleted, maybe not found");
        };
    }
    public static void removeUser(){
        System.out.println("Insert the _id of the user you want to remove");
        String id_exa = scanner.nextLine();
        ObjectId id = new ObjectId(id_exa);
        if(UserConnector.remove(id)){
            System.out.println("Deleted");
        }else{
            System.out.println("Not deleted, maybe not found");
        };
    }

    public static void findUser(){
        System.out.println("Enter user name");
        Document user = UserConnector.findUser(scanner.nextLine());
        if(user.getString("name").equals("0")){
            System.out.println("User not found");
        }else{
            System.out.println(user);
        }
    }

    public static void updatePOI(){
        System.out.println("Insert the name of the poi to update");
        Document poi = findPOI();
        System.out.println(poi);
        boolean exit = false;
        while(!exit){
            int c = chooseBetween(List.of("Update a field","Go back"),"admin/POI/update");
            switch(c){
                case 1:
                    System.out.println("enter the name of the field");
                    String field = scanner.nextLine();
                    System.out.println("enter the value");
                    String value = scanner.nextLine();
                    POIConnector.updateFieldPOI(poi.getObjectId("_id"),field,value);

                    break;
                case 2:
                    exit = true;
                    break;
                default:
                    System.out.println("wrong input, retry");
            }
        }
    }

    public static void user(){
        boolean exit = false;
        while(!exit){
            System.out.println("Which action do you want to take?");
            int action = chooseBetween(List.of("Search POI","Get suggestions","Delete Profile","Go back"),"user> ");
            switch(action){
                case 1:
                    Document poi = findPOI();
                    if(poi.getString("name").equals("0")){
                        System.out.println("POI not found");
                    }else{
                        System.out.println(poi.toJson());
                    }
                    int c = chooseBetween(List.of("See reviews","Add review","Remove review","Go back"),"user");
                    switch(c){
                        case 1:
                            findReviews(poi);
                            break;
                        case 2:
                            addReview(poi);
                            break;
                        case 3:
                            System.out.println("Insert the _id of the review you want to remove");
                            String id_exa = scanner.nextLine();
                            ObjectId review_id = new ObjectId(id_exa);
                            if(ReviewConnector.remove(review_id,sessionUser.getString("name"))){
                                System.out.println("Deleted");
                                POIConnector.removeReviewFromPOI(poi.getObjectId("_id"),review_id);
                            }else{
                                System.out.println("Not deleted, maybe not found");
                            };
                            break;
                        case 4:
                            break;
                        default:
                            System.out.println("wrong input, retry");
                    }


                    break;
                case 2:
                    System.out.println("to do");
                    break;
                case 3:
                    deleteProfile();
                    break;
                case 4:
                    exit = true;
                    break;
                default:
                    System.out.println("wrong input, retry");

            }
        }
    }

    public static void unsignedUser(){
        boolean exit = false;
        while(!exit){
            System.out.println("Hi Unsigned User, which action do you want to take?");
            int action = chooseBetween(List.of("Log in","Register","Search POI","Log in as Admin","TestNeo - sperimental","Stop"),"unsignedUser> ");
            switch(action){
                case 1:
                    if(logIn(false)){
                        user();
                    }else{
                        System.out.println("User not found");
                    }
                    break;
                case 2:
                    register();
                    break;
                case 3:
                    Document poi = findPOI();
                    if(poi.getString("name").equals("0")){
                        System.out.println("POI not found");
                    }else{
                        System.out.println(poi.toJson());
                    }
                    break;
                case 4:
                    if(logIn(true)){
                        admin();
                    }else{
                        System.out.println("Admin not found");
                    }
                    break;
                case 5:
                    neoSperimental();
                    break;
                case 6:
                    exit = true;
                    break;
                default:
                    System.out.println("wrong input, retry");
            }
        }
    }

    // UTILITIES

    public static Document findPOI(){
        System.out.println("Insert POI name");
        String POIname = scanner.nextLine();
        Document doc = POIConnector.findPOI(POIname);
        return doc;
    }

    public static boolean findReviews(Document poi){

        Document review;
        int reviewIndex = 0;
        List<ObjectId> review_ids = poi.getList("review_ids", ObjectId.class);
        if(review_ids.size()==0)
            return true;
        while(true) {
            System.out.println("see other reviews?");
            int c = chooseBetween(List.of("yes", "no"), "user");
            switch (c) {
                case 1:
                    for (int j = reviewIndex; j < reviewIndex + 3; j++) {
                        review = ReviewConnector.getReview(review_ids.get(j));
                        System.out.println(review.toJson());
                        if (j == (review_ids.size() - 1)) {
                            System.out.println("Reviews ended");
                            return true;
                        }
                    }
                    reviewIndex = reviewIndex + 3;
                    break;
                case 2:
                    return true;
            }

        }

    }
    public static boolean addReview(Document poi){
        System.out.println("Insert Review data:");
        System.out.println("insert your name");
        String name = scanner.nextLine();
        System.out.println("Stars");
        int stars = scanner.nextInt();
        scanner.nextLine();
        System.out.println("date");
        String date = scanner.nextLine();
        System.out.println("text");
        String text = scanner.nextLine();
        ObjectId review_id = ReviewConnector.insertReview(name,date,text,stars);
        POIConnector.addReviewToPOI(poi.getObjectId("_id"),review_id);

        return true;
    }


    public static boolean logIn(boolean admin){
        String username;
        String password;
        System.out.println("Insert username");
        username = scanner.nextLine();
        System.out.println("Insert password");
        password = scanner.nextLine();
        Document user = UserConnector.findUser(username,password,admin);
        if(user.getString("name").equals("0")){
            return false;
        }else{
            sessionUser = user;
            return true;
        }

    }
    public static boolean register(){
        String username;
        String password;
        int age;
        System.out.println("Insert username");
        username = scanner.nextLine();
        System.out.println("Insert password");
        password = scanner.nextLine();
        System.out.println("Insert age");
        age = scanner.nextInt();
        scanner.nextLine();
        boolean esito = false;
        if(UserConnector.findUser(username,password,false).getString("name").equals("0")){ //find user find a user with the same data. if it doesn't exists, the output is a doc {"name","0"}. if it is like that, is okay to insert a new user
            esito = UserConnector.createUser(username,password,age);
        }else{
            System.out.println("The user already exists");
        }


        return esito;
    }

    public static boolean deleteProfile(){
        System.out.println("Are you sure that you want to delete your profile?");
        int c = chooseBetween(List.of("yes","no"),"user");
        switch(c){
            case 1:
                if(UserConnector.remove(sessionUser.getObjectId("_id"))){
                    sessionUser = null;
                    System.out.println("User has been deleted");
                    unsignedUser();
                }else{
                    System.out.println("User hasn't been deleted due to some problems");
                }
                break;
            case 2:
                System.out.println("The user hasn't been deleted");
                break;
            default:
                System.out.println("wrong input, retry");
        }
        return true;
    }


    public static int chooseBetween(List<String> options,String prompt){
        int i = 1;
        for(String option : options){
            System.out.println(" "+i+" - "+option);
            i++;
        }
        //System.out.print(prompt);
        int r = scanner.nextInt();
        scanner.nextLine(); //nextInt leaves a \n into the buffer, so other nextLine will pick up that instead of the actual value
        //clearCLI();
        return r;
    }
    public static void clearCLI(){
        for(int f = 0;f < 20; f++){
            System.out.println();
        }
    }

    public static void neoSperimental(){
        boolean exit = false;
        while(!exit){
            System.out.println("cosa vuoi fare?");
            String name;
            String name1;
            String name2;
            switch(chooseBetween(List.of("go back","insert user","insert friendship","remove user",
                    "remove friendship","propose friendship","remove friendship propose","accept friendship"),"testing")){
                case 1:
                    exit = true;
                    break;
                case 2:
                    System.out.println("name");
                    name = scanner.nextLine();
                    System.out.println("età");
                    int eta = scanner.nextInt();
                    scanner.nextLine();
                    neoConnector.addUser(name,eta);
                    break;
                case 3:
                    System.out.println("name1");
                    name1 = scanner.nextLine();
                    System.out.println("name2");
                    name2 = scanner.nextLine();
                    neoConnector.addFriendship(name1,name2);
                    break;
                case 4:
                    System.out.println("name");
                    neoConnector.deleteUser(scanner.nextLine());
                    break;
                case 5:
                    System.out.println("name1");
                    name1 = scanner.nextLine();
                    System.out.println("name2");
                    name2 = scanner.nextLine();
                    neoConnector.deleteFriendship(name1,name2);
                    neoConnector.deleteFriendship(name2,name1);
                    break;
                case 6:
                    System.out.println("name1");
                    name1 = scanner.nextLine();
                    System.out.println("name2");
                    name2 = scanner.nextLine();
                    neoConnector.addFriendshipRequest(name1,name2);
                    break;
                case 7:
                    System.out.println("name1");
                    name1 = scanner.nextLine();
                    System.out.println("name2");
                    name2 = scanner.nextLine();
                    neoConnector.deleteRequest(name1,name2);
                    break;
                case 8:
                    System.out.println("to do");
                    break;
                default:
                    System.out.println("wrong input");
            }
        }

    }
}

//Templates

//switch(status){
//        case "1":
//
//        break;
//        case "2":
//
//        break;
//        case "3":
//
//        break;
//        }