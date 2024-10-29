package org.example;

import mongoConnectors.POIConnector;
import mongoConnectors.ReviewConnector;
import mongoConnectors.UserConnector;

import java.util.List;
import java.util.Scanner;
import org.bson.Document;

public class CLI {
     static Scanner scanner = new Scanner(System.in);

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
            System.out.println("Which action do you want to take?");
            int action = chooseBetween(List.of("Create","Remove","Update","Read","Go back"),"admin> ");
            switch(action){
                case 1:
                    adminInsert();
                    break;
                case 2:
                    System.out.println("to do");
                    break;
                case 3:
                    System.out.println("to do");
                    break;
                case 4:
                    adminRead();
                    break;
                case 5:
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
            int action = chooseBetween(List.of("Create Review","Update Profile","Delete Profile","Search POI","Go back"),"user> ");
            switch(action){
                case 1:
                    System.out.println("todo");
                    break;
                case 2:
                    System.out.println("to do");
                    break;
                case 3:
                    System.out.println("to do");
                    break;
                case 4:
                    if(!findPOIandReviews()){
                        System.out.println("poi not found");
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

    public static void unsignedUser(){
        boolean exit = false;
        while(!exit){
            System.out.println("Hi Unsigned User, which action do you want to take?");
            int action = chooseBetween(List.of("Log in","Register","Search POI","Log in as Admin","Go back"),"unsignedUser> ");
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
                    if(!findPOI()){
                        System.out.println("POI not found");
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

    public static boolean findPOI(){
        System.out.println("Insert POI name");
        String POIname = scanner.nextLine();
        Document doc = POIConnector.findPOI(POIname);
        if(doc.getString("name").equals("0")){
            return false;
        }else{
            System.out.println(doc.toJson());
            return true;
        }
    }

    public static boolean findPOIandReviews(){
        System.out.println("Insert POI name");
        String POIname = scanner.nextLine();
        Document doc = POIConnector.findPOI(POIname);
        if(doc.getString("name").equals("0")){
            return false;
        }else{
            System.out.println(doc.toJson());
            Document review;
            int reviewIndex = 0;
            List<String> review_ids = doc.getList("review_ids", String.class);
            boolean exit = false;
            while(!exit){
                System.out.println("see other reviews?");
                int c = chooseBetween(List.of("yes","no"),"user");
                switch(c){
                    case 1:
                        for(int j = reviewIndex; j< Math.min(review_ids.size(),reviewIndex+3);j++){
                            review = ReviewConnector.getReview(review_ids.get(j));
                        }
                        reviewIndex = reviewIndex +3;
                        break;
                    case 2:
                        exit = true;
                        break;
                }

            }
            return true;
        }
    }

    public static boolean logIn(boolean admin){
        String username;
        String password;
        System.out.println("Insert username");
        username = scanner.nextLine();
        System.out.println("Insert password");
        password = scanner.nextLine();
        boolean esito = UserConnector.findUser(username,password,admin);

        return esito;
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
        if(!UserConnector.findUser(username,password,false)){
            esito = UserConnector.createUser(username,password,age);
        }else{
            System.out.println("The user already exists");
        }


        return esito;
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