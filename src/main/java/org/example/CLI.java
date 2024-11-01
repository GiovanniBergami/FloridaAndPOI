package org.example;

import mongoConnectors.CityConnector;
import mongoConnectors.POIConnector;
import mongoConnectors.ReviewConnector;
import mongoConnectors.UserConnector;

import java.util.List;
import java.util.Scanner;
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
            System.out.println("Choose collection");
            int collection = chooseBetween(List.of("POIs","Reviews","Users","Go back"),"admin> ");
            int action = 5;
            if(collection != 4)
                action = chooseBetween(List.of("Create","Read","Update","Delete","Go back"),"admin/POI> ");
            switch(collection){
                case 1:
                    switch(action){
                        case 1: //create POI
                            System.out.println("Insert city of the POI");
                            String city = scanner.nextLine();
                            Document cityOfPOI = CityConnector.findCity(city); //ci vorrà un index sui nomi di città
                            if(cityOfPOI.getString("name").equals("0")){
                                System.out.println("The city doesn't exist");
                                break;
                            }else{
                                System.out.println("Insert name of POI");
                                String name = scanner.nextLine();
                                if(!POIConnector.findPOI(name).getString("name").equals("0")){
                                    System.out.println("This name already exists");
                                    break;
                                }else{
                                    System.out.println("Insert address");
                                    String address = scanner.nextLine();
                                    ObjectId newPOIId =POIConnector.insertPOI(name,address,city);
                                    CityConnector.addPOIToCity(cityOfPOI.getObjectId("_id"),newPOIId);
                                    System.out.println("The POI has been added");
                                }
                            }
                            break;
                        case 2:
                            break;
                        case 3:
                            break;
                        case 4:
                            System.out.println("Insert the name of the POI you want to remove"); //bisogna rimuovere anche tutte le reviews
                            String name = scanner.nextLine();
                            Document poi = POIConnector.findPOI(name);
                            if(poi.getString("name").equals("0")){
                                System.out.println("poi not found");
                                break;
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

                            break;
                        case 5:
                            break;
                        default:
                            System.out.println("wrong input");
                    }
                    break;
                case 2:
                    switch(action){
                        case 1:
                            break;
                        case 2:
                            System.out.println("todo");
                            break;
                        case 3:
                            break;
                        case 4:
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
                            break;
                        case 5:
                            break;
                        default:
                            System.out.println("wrong input");
                    }
                    break;
                case 3:
                    switch(action){
                        case 1:
                            break;
                        case 2:
                            break;
                        case 3:
                            System.out.println("todo");
                            break;
                        case 4:
                            System.out.println("Insert the _id of the user you want to remove");
                            String id_exa = scanner.nextLine();
                            ObjectId id = new ObjectId(id_exa);
                            if(UserConnector.remove(id)){
                                System.out.println("Deleted");
                            }else{
                                System.out.println("Not deleted, maybe not found");
                            };
                            break;
                        case 5:
                            break;
                        default:
                            System.out.println("wrong input");
                    }
                    break;
                case 4:
                    exit = true;
                    break;
                default:
                    System.out.println("wrong input, retry");
            }
        }

    }


    public static void adminInsert(){
        int activity = chooseBetween(List.of("Create POIs","Create Users","Create Reviews"," go back"),"admin/insert> ");

        switch(activity){
            case 1:
                System.out.println("Insert POIs, specify path");
                POIConnector.insertPOIs(scanner.nextLine());
                POIConnector.count();
                break;
            case 2:
                System.out.println("Insert Users");
                int prova;
                prova = scanner.nextInt();
                System.out.println(prova);
                break;
            case 3:
                System.out.println("Insert Reviews, specify path");
                ReviewConnector.insertReviews(scanner.nextLine());
                ReviewConnector.count();
                ReviewConnector.printCollection(3);
                break;
            case 4:
                break;
            default:
                System.out.println("wrong input, retry");
        }
    }
    public static void adminRead(){
        int activity = chooseBetween(List.of("Read POIs","Read Users","Read Reviews"," go back"),"admin/insert> ");
        System.out.println("how many?");
        int n = scanner.nextInt();
        scanner.nextLine(); //toglie il \n del nextInt
        switch(activity){
            case 1:
                POIConnector.printCollection(n);
                POIConnector.count();
                break;
            case 2:
                UserConnector.printCollection(n);
                UserConnector.count();
                break;
            case 3:
                ReviewConnector.printCollection(n);
                ReviewConnector.count();
                break;
            case 4:
                break;
            default:
                System.out.println("wrong input, retry");
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
            int action = chooseBetween(List.of("Log in","Register","Search POI","Log in as Admin","Stop"),"unsignedUser> ");
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