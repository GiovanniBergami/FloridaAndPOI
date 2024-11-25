package org.service;

import mongoConnectors.CityConnector;
import mongoConnectors.POIConnector;
import mongoConnectors.ReviewConnector;
import mongoConnectors.UserConnector;

import java.util.ArrayList;
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
            int collection = chooseBetween(List.of("POIs","Reviews","Users","Cities","Analytics","Update Plans","Go back"),"admin> ");
            int action = 0;
            List<String> data;
            switch(collection){
                case 1:
                    action = chooseBetween(List.of("Create one","Read one","Update one","Delete one",
                            "Import collection from json","Read first n","Go back"),"admin/POI> ");
                    switch(action){
                        case 1: //create POI
                            createPOI();
                            break;
                        case 2:
                            Document poi = findPOIs();
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
                            POIConnector.createPOIsFromJsonFile(scanner.nextLine());
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
                            findReviews(findPOIs());
                            break;
                        case 2:
                            removeReview();
                            break;
                        case 3:
                            System.out.println("Insert Reviews, specify path");
                            ReviewConnector.createReviewsFromJsonPath(scanner.nextLine());
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
                    action = chooseBetween(List.of("Create","Delete",
                            "Import collection from json","Read all collection","read city","Go back"),"admin/Cities>");
                    switch(action){
                        case 1:
                            System.out.println("Create city");
                            data = insert(List.of("Name"));
                            if(CityConnector.readCity(data.get(0)).getString("name").equals("0")){
                                CityConnector.createCity(data.get(0));
                            }else{
                                System.out.println("name already present");
                            }
                            break;
                        case 2:
                            System.out.println("Delete city");
                            data = insert(List.of("Name"));
                            CityConnector.remove(data.get(0));
                            break;
                        case 3:
                            System.out.println("Insert Cities, specify path");
                            CityConnector.createCitiesFromJsonFile(scanner.nextLine());
                            CityConnector.count();
                            break;
                        case 4:
                            System.out.println("how many?");
                            int n = scanner.nextInt();
                            scanner.nextLine();
                            CityConnector.printCollection(n);
                            CityConnector.count();
                            break;
                        case 5:
                            System.out.println("insert name");
                            System.out.println(CityConnector.readCity(scanner.nextLine()).toJson());
                            break;
                        case 6:
                            break;
                        default:
                            System.out.println("wrong input");
                    }
                    break;
                case 5:
                    System.out.println("Analytics");
                    switch(chooseBetween(List.of("Stat of POIs of a given city","best poi given age","best pois given city","citySummary"),"")){
                        case 1:
                            data = insert(List.of("city"));
                            System.out.println(POIConnector.poiOfCityStatistics(data.get(0)));
                            break;
                        case 2:
                            System.out.println("insert age span");
                            int ageSpan = scanner.nextInt();
                            scanner.nextLine();
                            System.out.println(POIConnector.bestPOIForAge(ageSpan));
                            break;
                        case 3:
                            data = insert(List.of("city"));
                            System.out.println(POIConnector.bestPOIinCity(data.get(0)));
                            break;
                        case 4:
                            System.out.println(POIConnector.citySummary());
                            break;
                        default:
                            System.out.println("wrong input");
                    }
                    break;
                case 6:
                    neoConnector.updatePlans();
                    break;
                case 7:
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
        Document cityOfPOI = CityConnector.readCity(city);
        if(cityOfPOI.getString("name").equals("0")){
            System.out.println("The city doesn't exist");
            return;
        }else{
            System.out.println("Insert name of POI");
            String name = scanner.nextLine();
//            if(!POIConnector.findPOI(name).getString("name").equals("0")){
//                System.out.println("This name already exists");
//                return;
//            }else{
            System.out.println("Insert address");
            String address = scanner.nextLine();
            ObjectId newPOIId =POIConnector.createPOI(name,address,city);
            if(newPOIId != null) {
                if(CityConnector.addPOIToCity(cityOfPOI.getObjectId("_id"), newPOIId)){
                    if(neoConnector.addPOI(name, newPOIId.toString())){
                        System.out.println("The POI has been added");
                    }else{
                        CityConnector.removePOIFromCity(cityOfPOI.getString("name"),newPOIId);
                        POIConnector.remove(newPOIId);
                        System.out.println("no poi added");
                    }
                }else{
                    POIConnector.remove(newPOIId);
                    System.out.println("no poi added");
                }
            }else{
                System.out.println("no poi added");
            }
            //}
        }
    }

    public static void deletePOI(){
        System.out.println("Insert the name of the POI you want to remove"); //bisogna rimuovere anche tutte le reviews
        //String name = scanner.nextLine();
        //Document poi = POIConnector.findPOI(name);
        Document poi = findPOIs();
        if(poi.getString("name").equals("0")){
            System.out.println("poi not found");
            return;
        }

        ObjectId id = poi.getObjectId("_id");
        List<Document> reviews = new ArrayList<>();
        List<ObjectId> review_ids = poi.getList("review_ids", ObjectId.class);
        for(ObjectId review_id : review_ids ){
            reviews.add(ReviewConnector.readReview(review_id));
        }

        if(POIConnector.remove(id)){
            if(CityConnector.removePOIFromCity(poi.getString("city"),poi.getObjectId("_id"))) {

                if (review_ids != null) {
                    if(ReviewConnector.removeMany(review_ids)){
                        if(neoConnector.deletePOI(id.toString())){
                            System.out.println("deleted");
                        }else{
                            for(Document r: reviews)
                                ReviewConnector.createReview(r.toJson());
                            CityConnector.addPOIToCityName(poi.getString("city"),poi.getObjectId("_id"));
                            POIConnector.createPOI(poi.toJson());
                            System.out.println("not deleted");
                        }
                    }else{
                        CityConnector.addPOIToCityName(poi.getString("city"),poi.getObjectId("_id"));
                        POIConnector.createPOI(poi.toJson());
                        System.out.println("not deleted");
                    }
                }else{
                    if(neoConnector.deletePOI(id.toString())){
                        System.out.println("deleted");
                    }else{
                        CityConnector.addPOIToCityName(poi.getString("city"),poi.getObjectId("_id"));
                        POIConnector.createPOI(poi.toJson());
                        System.out.println("not deleted");
                    }

                }
            }else{
                POIConnector.createPOI(poi.toJson()); //controllare se _id resta uguale, in caso migliorare le reviews
                System.out.println("not deleted");
            }
        }else{
            System.out.println("Not deleted");
        };
    }

    public static void removeReview(){
        System.out.println("Insert the _id of the review you want to remove");
        ObjectId review_id = new ObjectId(scanner.nextLine());
        System.out.println("Insert the _id of the POI of the review");
        ObjectId poi_id = new ObjectId(scanner.nextLine());
        System.out.println("insert user name");
        String userName = scanner.nextLine();
        System.out.println("insert date");
        String date = scanner.nextLine();
        Document review = ReviewConnector.readReview(review_id);

        if(ReviewConnector.remove(review_id,"")){
            if(POIConnector.removeReviewFromPOI(poi_id,review_id)){
                if(neoConnector.removeVisit(poi_id.toString(),userName,date)){
                    System.out.println("Deleted");
                }else{
                    ObjectId newId = ReviewConnector.createReview(review.getString("username"),review.getString("date"),review.getString("text"),review.getInteger("stars").intValue());
                    POIConnector.addReviewToPOI(poi_id,newId);
                    System.out.println("not deleted");
                }
            }else{
                ReviewConnector.createReview(review.getString("username"),review.getString("date"),review.getString("text"),review.getInteger("stars").intValue());
                System.out.println("not deleted");
            }
        }else{
            System.out.println("Not deleted");
        }
    }
    public static void removeUser(){
        System.out.println("Insert the name of the user you want to remove");
        Document user = UserConnector.readUser(scanner.nextLine());
        if(user.getString("name").equals("0")){
            System.out.println("User not found");
        }else{
            if(UserConnector.remove(user.getObjectId("_id"))){
                if(neoConnector.deleteUser(user.getString("name"))){
                    System.out.println("user has been deleted");
                }else{
                    UserConnector.createUser(user.toJson());
                    System.out.println("user hasn't been deleted");
                }
            }else{
                System.out.println("user hasn't been deleted");
            }

        }

    }

    public static void findUser(){
        System.out.println("Enter user name");
        Document user = UserConnector.readUser(scanner.nextLine());
        if(user.getString("name").equals("0")){
            System.out.println("User not found");
        }else{
            System.out.println(user);
        }
    }

    public static void updatePOI(){
        System.out.println("Insert the name of the poi to update");
        Document poi = findPOIs();
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
                    if(field.equals("name")){
                        neoConnector.updatePOI(poi.getObjectId("_id").toString(),value);
                    }
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
        boolean exitInnerLoops = false;
        List<String> data;
        while(!exit){
            System.out.println("What do you want to do?");
//            int action = chooseBetween(List.of("Search POI","Get suggestions","Delete Profile","Search Users","See friends",
//                    "See friendship proposal","See plans","propose similar users","see friends plans","Go back"),"user> ");
            int section = chooseBetween(List.of("Visit","Friends","Profile","Log out"),"user>");
            int action;
            switch(section){
                case 1:
                    exitInnerLoops = false;
                    while(!exitInnerLoops) {
                        System.out.println("\nChoose Between:");
                        action = chooseBetween(List.of("Search POI", "Get suggestions", "See plans", "best POI for age", "best POI for city","read city", "go back"), "user/visit>");
                        switch (action) {
                            case 1:
                                searchPOI();
                                break;
                            case 2:
                                data = neoConnector.recommendPOI(sessionUser.getString("name"));
                                for (String d : data) {
                                    System.out.println(d);
                                    //System.out.println("");
                                }
                                break;
                            case 3:
                                List<String> plans = neoConnector.getPlans(sessionUser.getString("name"));
                                for (String d : plans) {
                                    System.out.println(d);
                                    System.out.println("");
                                }
                                System.out.println("do you want to remove one of them?");
                                if (chooseBetween(List.of("yes", "no"), "") == 1) {
                                    List<String> input = insert(List.of("poi id", "date yyyy-MM-dd"));
                                    neoConnector.removePlan(input.get(0), sessionUser.getString("name"), input.get(1));
                                }
                                break;
                            case 4:
                                System.out.println("insert an age span");
                                int a = scanner.nextInt();
                                scanner.nextLine();
                                System.out.println(POIConnector.bestPOIForAge(a));
                                break;
                            case 5:
                                data = insert(List.of("Insert a city"));
                                System.out.println(POIConnector.bestPOIinCity(data.get(0)));
                                break;
                            case 6:
                                readCity();
                                break;
                            case 7:
                                exitInnerLoops = true;
                                break;
                            default:
                                System.out.println("wrong input");
                        }
                    }
                    break;
                case 2:
                    exitInnerLoops = false;
                    while(!exitInnerLoops) {
                        System.out.println("\nChoose between:");
                        action = chooseBetween(List.of("search users", "See Friends", "see friendship requests", "Get friends recommendation", "visualize friends calendar", "go back"), "user/friends>");
                        switch (action) {
                            case 1:
                                System.out.println("Search user: insert name");
                                String searchedName = scanner.nextLine();
                                Document user = UserConnector.readUser(searchedName);
                                if (user.getString("name").equals("0")) {
                                    System.out.println("User not found");
                                } else {
                                    System.out.println(user.toJson());
                                    System.out.println("Do you want to propose friendship?");
                                    switch (chooseBetween(List.of("Yes", "No"), "")) {
                                        case 1:
                                            neoConnector.addFriendshipRequest(sessionUser.getString("name"), searchedName);
                                            break;
                                        case 2:
                                            break;
                                        default:
                                            System.out.println("wrong input");
                                    }
                                }

                                //se user viene trovato è possibile proporgli l'amicizia, se non è già amico
                                break;
                            case 2:
                                System.out.println("Friends list:");
                                neoConnector.getFriends(sessionUser.getString("name"));
                                System.out.println("Do you want to delete a friendship?");
                                switch (chooseBetween(List.of("Yes", "No"), "")) {
                                    case 1:
                                        System.out.println("Insert name");
                                        neoConnector.deleteFriendship(sessionUser.getString("name"), scanner.nextLine());
                                        break;
                                    case 2:
                                        break;
                                    default:
                                        System.out.println("wrong input");
                                }
                                break;
                            case 3:
                                System.out.println("Friendship proposal:");   //CONTROLLARE SE FUNZIONA
                                String sessionUserName = sessionUser.getString("name");
                                neoConnector.getRequested(sessionUserName); //SE SPLITTI CLIENT SERVER BISOGNA PASSARE IL RISULTATO
                                System.out.println("do you want to accept some request?");
                                switch (chooseBetween(List.of("yes", "no"), "")) {
                                    case 1:
                                        System.out.println("enter the name of the requester");
                                        String requester = scanner.nextLine();
                                        neoConnector.deleteRequest(requester, sessionUserName);
                                        neoConnector.addFriendship(requester, sessionUserName);
                                        break;
                                    case 2:
                                        break;
                                    default:
                                        System.out.println("wrong input");
                                }

                                break;

                            case 4:
                                data = neoConnector.similarUser(sessionUser.getString("name"));
                                for (String d : data)
                                    System.out.println(d);
                                break;
                            case 5:
                                data = neoConnector.findFriendPlans(sessionUser.getString("name"));
                                for (String d : data) {
                                    System.out.println(d);
                                }
                                break;
                            case 6:
                                exitInnerLoops = true;
                                break;
                            default:
                                System.out.println("wrong input, retry");
                        }
                    }
                    break;
                case 3:
                    exitInnerLoops = false;
                    while(!exitInnerLoops) {
                        System.out.println("\nChoose between:");
                        action = chooseBetween(List.of("Delete Profile", "Update profile", "go back"), "user/profile>");
                        switch (action) {
                            case 1:
                                deleteProfile();
                                break;
                            case 2:
                                System.out.println("todo");
                            case 3:
                                exitInnerLoops = true;
                                break;
                            default:
                                System.out.println("wrong input, retry");
                        }
                    }
                    break;
                case 4:
                    exit = true;
                    break;
                default:
                    System.out.println("wrong input");
            }
        }
    }

    public static void searchPOI(){
        List<String> data;
        Document poi = findPOIs();
        if (poi.getString("name").equals("0")) {
            System.out.println("POI not found");
        } else {
            System.out.println(poi.toJson());
        }
        int c = chooseBetween(List.of("See reviews", "Add review", "Remove review", "Plan to visit", "Go back"), "user");
        switch (c) {
            case 1:
                findReviews(poi);
                break;
            case 2:
                addReview(poi);
                break;
            case 3:
                data = insert(List.of("Insert the _id of the review you want to remove", "date"));
//                            String id_exa = scanner.nextLine();
                ObjectId review_id = new ObjectId(data.get(0));

                Document review = ReviewConnector.readReview(review_id);

                if (POIConnector.removeReviewFromPOI(poi.getObjectId("_id"), review_id)) {
                    if (ReviewConnector.remove(review_id, sessionUser.getString("name"))) {
                        if (neoConnector.removeVisit(poi.getObjectId("_id").toString(), sessionUser.getString("name"), data.get(1))) {
                            System.out.println("deleted");
                        } else {
                            ReviewConnector.createReview(review.toJson());
                            POIConnector.addReviewToPOI(poi.getObjectId("_id"), review_id);
                            System.out.println("Not deleted");
                        }
                    } else {
                        POIConnector.addReviewToPOI(poi.getObjectId("_id"), review_id);
                        System.out.println("Not deleted");
                    }
                } else {
                    System.out.println("not deleted");
                }
                break;
            case 4:
                System.out.println("plan to visit");
                System.out.println("Coincidences found among your frinds:");
                data = neoConnector.findCoincidence(poi.getObjectId("_id").toString(), sessionUser.getString("name"));
                if (data.size() != 0) {
                    for (String coincidence : data) {
                        System.out.println(coincidence);
                    }
                } else {
                    System.out.println("no coincidence found");
                }
                System.out.println("Insert date yyyy-MM-dd");
                String date = scanner.nextLine();
                neoConnector.addPlan(poi.getObjectId("_id").toString(), sessionUser.getString("name"), date);
                //sout eventuali coincidenze
                break;
            case 5:
                break;
            default:
                System.out.println("wrong input, retry");
        }
    }

    public static void readCity(){
        List<String> data;
        System.out.println("Insert name of the city");
        Document city = CityConnector.readCity(scanner.nextLine());
        if (city.getString("name").equals("0")) {
            System.out.println("city not found");
            return;
        } else {
            System.out.println(city.toJson());
        }
        int c = chooseBetween(List.of("See pois", "Go back"), "user");
        switch (c) {
            case 1:
                Document poi;
                int poiIndex = 0;
                List<ObjectId> poi_ids = city.getList("POI_ids", ObjectId.class);
                if(poi_ids.size()==0)
                    return;
                while(true) {
                    System.out.println("see other pois?");
                    int d = chooseBetween(List.of("yes", "no"), "user");
                    switch (d) {
                        case 1:
                            for (int j = poiIndex; j < poiIndex + 3; j++) {
                                poi = POIConnector.readPOI(poi_ids.get(j));
                                System.out.println(poi.toJson());
                                if (j == (poi_ids.size() - 1)) {
                                    System.out.println("pois ended");
                                    return;
                                }
                            }
                            poiIndex = poiIndex + 3;
                            break;
                        case 2:
                            return;
                    }

                }

            case 2:
                break;
            default:
                System.out.println("wrong input, retry");
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
                    Document poi = findPOIs();
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
                    Main.exit();
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
        Document doc = POIConnector.readPOI(POIname);
        return doc;
    }

    public static Document findPOIs(){
        System.out.println("insert poi name");
        String poiName = scanner.nextLine();
        List<Document> pois = POIConnector.readMultiplePOI(poiName);
        if(pois.size()==0)
            return new Document("name","0");
        if(pois.size()==1)
            return pois.get(0);
        int i = 1;
        for(Document poi: pois){
            System.out.println("\n"+i+" - "+poi.toJson());
            i++;
        }
        System.out.println("\nChoose and insert the number");
        int c = scanner.nextInt();
        scanner.nextLine();
        return pois.get(c-1);

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
                        review = ReviewConnector.readReview(review_ids.get(j));
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

        System.out.println("date");
        String date = scanner.nextLine();
        System.out.println("text");
        String text = scanner.nextLine();

        System.out.println("Stars");
        int stars = scanner.nextInt();
        scanner.nextLine();

        ObjectId review_id = ReviewConnector.createReview(name,date,text,stars);
        if(review_id != null) {
            if(POIConnector.addReviewToPOI(poi.getObjectId("_id"), review_id)){
                if(neoConnector.addVisit(poi.getObjectId("_id").toString(),sessionUser.getString("name"),Double.valueOf(stars),date)){
                    System.out.println("added");
                    return true;
                }else{
                    POIConnector.removeReviewFromPOI(poi.getObjectId("_id"),review_id);
                    ReviewConnector.remove(review_id,"");
                }
            }else{
                ReviewConnector.remove(review_id,"");
            }
        }

        System.out.println("not added");
        return false;
    }


    public static boolean logIn(boolean admin){
        String username;
        String password;
        System.out.println("Insert username");
        username = scanner.nextLine();
        System.out.println("Insert password");
        password = scanner.nextLine();
        Document user = UserConnector.readUser(username,password,admin);
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
        ObjectId mongoId;
        if(UserConnector.readUser(username).getString("name").equals("0")){ //find user find a user with the same data. if it doesn't exists, the output is a doc {"name","0"}. if it is like that, is okay to insert a new user
            mongoId = UserConnector.createUser(username,password,age);
            if(mongoId != null) {
                if (neoConnector.addUser(username, mongoId.toString())) {
                    System.out.println("user added");
                } else {
                    System.out.println("user not added");
                    UserConnector.remove(mongoId);
                }
            }else{
                System.out.println("user not added");
            }
        }else{
            System.out.println("The username is already taken");
        }


        return true;
    }

    public static boolean deleteProfile(){
        System.out.println("Are you sure that you want to delete your profile?");
        int c = chooseBetween(List.of("yes","no"),"user");
        switch(c){
            case 1:
                if(UserConnector.remove(sessionUser.getObjectId("_id"))){
                    if(neoConnector.deleteUser(sessionUser.getString("name"))){
                        sessionUser = null;
                        System.out.println("User has been deleted");
                        unsignedUser();
                    }else{
                        UserConnector.createUser(sessionUser.toJson());
                        System.out.println("User hasn't been deleted due to some problems");
                    }

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
        System.out.print("    ");
        for(String option : options){
            System.out.print(option+" ("+i+")");
            for(int j = 0; j < (30 - option.length() - i/10);j++)
                System.out.print(" ");
            if(i%5==0){
                System.out.println("");
                System.out.print("    ");
            }
            i++;
        }
        System.out.println("");
        //System.out.print(prompt);
        int r = scanner.nextInt();
        scanner.nextLine(); //nextInt leaves a \n into the buffer, so other nextLine will pick up that instead of the actual value
        //clearCLI();
        return r;
    }

    public static List<String> insert(List<String> questions){
        List<String> answers = new ArrayList<>();
        for(String question: questions){
            System.out.println("Insert "+question);
            answers.add(scanner.nextLine());
        }
        return answers;
    }

    public static void clearCLI(){
        for(int f = 0;f < 20; f++){
            System.out.println();
        }
    }

    public static String ageSpan(){
        int age = sessionUser.getInteger("age").intValue();
        if(age<=15)
            return "0";
        if(age<=30)
            return "1";
        if(age<=50)
            return "2";
        if(age<=70)
            return "3";
        return "4";
    }
    public static String ageSpan(String username){

        int age = UserConnector.readUser(username).getInteger("age").intValue();
        if(age<=15)
            return "0";
        if(age<=30)
            return "1";
        if(age<=50)
            return "2";
        if(age<=70)
            return "3";
        return "4";
    }

    public static void neoSperimental(){
        boolean exit = false;
        while(!exit){
            System.out.println("cosa vuoi fare?");
            String name;
            String name1;
            String name2;
            List<String> data;
            switch(chooseBetween(List.of("go back","insert user","insert friendship","remove user",
                    "remove friendship","propose friendship","remove friendship propose","getRequested",
                    "accept friendship","importUser","addVisit","add plan","remove plan","get plans","recommend","similar","friend plans"),"testing")){
                case 1:
                    exit = true;
                    break;
                case 2:
                    System.out.println("name");
                    name = scanner.nextLine();
                    System.out.println("età");
                    int eta = scanner.nextInt();
                    scanner.nextLine();
                    System.out.println("mongoId");
                    String mongoId = scanner.nextLine();
                    neoConnector.addUser(name,mongoId); //FATTO
                    break;
                case 3:
                    data = insert(List.of("name1","name2"));
                    neoConnector.addFriendship(data.get(0),data.get(1));; //INUTILE
                    break;
                case 4:
                    System.out.println("name");
                    neoConnector.deleteUser(scanner.nextLine()); //implementato
                    break;
                case 5:
                    data = insert(List.of("name1","name2"));
                    neoConnector.deleteFriendship(data.get(0),data.get(1)); //implementato

                    break;
                case 6:
                    List<String> ReqNames = insert(List.of("name1","name2"));
                    neoConnector.addFriendshipRequest(ReqNames.get(0),ReqNames.get(1)); //implementato
                    break;
                case 7:
                    List<String> names = insert(List.of("name1","name2"));
                    neoConnector.deleteRequest(names.get(0),names.get(1)); //implementato
                    break;
                case 8:
                    System.out.println("name");
                    neoConnector.getRequested(scanner.nextLine()); //implementato
                    break;
                case 9:
                    System.out.println("enter name");
                    String user = scanner.nextLine();
                    System.out.println("hai queste richieste:");
                    //String user = sessionUser.getString("name"); DEFINITIVO
                    neoConnector.getRequested(user);
                    System.out.println("enter the name of the one of the request");
                    String requester = scanner.nextLine();
                    neoConnector.deleteRequest(requester,user);
                    neoConnector.addFriendship(requester,user); //implementato
                    break;
                case 10:
                    //UserConnector.createNeoCollection();
                    //UserConnector.createNeoFriendship();
                    //POIConnector.createNeoCollection();
                    //POIConnector.createNeoVisits();
                    break;
                case 11:
                    List<String> inp = insert(List.of(" poi id","username","date"));
                    System.out.println("stars");
                    Double stars = scanner.nextDouble();
                    scanner.nextLine();
                    neoConnector.addVisit(inp.get(0),inp.get(1),stars,inp.get(2));

                    break;
                case 12:
                    List<String> inputs = insert(List.of("poi id","username","date yyyy-MM-dd")); //implementato
                    neoConnector.addPlan(inputs.get(0),inputs.get(1),inputs.get(2));
                    break;
                case 13:
                    List<String> input = insert(List.of("poi id","username","date yyyy-MM-dd")); //implementato
                    neoConnector.removePlan(input.get(0),input.get(1),input.get(2));
                    break;
                case 14:
                    data = insert(List.of("username")); //implementato
                    data =neoConnector.getPlans(data.get(0));
                    for(String d: data){
                        System.out.println(d);
                        System.out.println("");
                    }
                    break;
                case 15:
                    data = insert(List.of("username"));
                    data = neoConnector.recommendPOI(data.get(0));
                    for(String d: data){
                        System.out.println(d);
                        //System.out.println("");
                    }
                    break;
                case 16:
                    data = insert(List.of("username"));  //implementato
                    data = neoConnector.similarUser(data.get(0));
                    for(String d: data){
                        System.out.println(d);
                        //System.out.println("");
                    }
                    break;
                case 17:
                    data = insert(List.of("username"));  //implementato
                    data = neoConnector.findFriendPlans(data.get(0));
                    for(String d: data){
                        System.out.println(d);
                        //System.out.println("");
                    }
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