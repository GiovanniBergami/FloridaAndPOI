package org.example;

import mongoConnectors.POIConnector;
import mongoConnectors.ReviewConnector;

import java.util.List;
import java.util.Scanner;

public class CLI {
     static Scanner scanner = new Scanner(System.in);

    public static void start(){
        boolean exit = false;
        System.out.println("Starting...");
        while(!exit) {
            System.out.println("Who are you?");
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
            int action = chooseBetween(List.of("Create","Remove","Update","Delete","Go back"),"admin> ");
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
                    System.out.println("to do");
                    break;
                case 5:
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
                    System.out.println("to do");
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
            System.out.println("Which action do you want to take?");
            int action = chooseBetween(List.of("Sign In","Search POI","Go back"),"unsignedUser> ");
            switch(action){
                case 1:
                    System.out.println("todo");
                    break;
                case 2:
                    System.out.println("to do");
                    break;
                case 3:
                    exit = true;
                    break;
                default:
                    System.out.println("wrong input, retry");
            }
        }
    }

    public static void adminInsert(){
        int activity = chooseBetween(List.of("Create POIs","Create Users","Create Reviews"," go back"),"admin/insert> ");
        String path;
        switch(activity){
            case 1:
                System.out.println("Insert POIs, specify path");
                path = scanner.nextLine();
                POIConnector.insertPOIs(path); //questo verrà eventualmente inserito da tastiera
                POIConnector.count();
                break;
            case 2:
                System.out.println("Insert Users");
                break;
            case 3:
                System.out.println("Insert Reviews, specify path");
                path = scanner.nextLine();
                ReviewConnector.insertReviews(path);
                ReviewConnector.count();
                ReviewConnector.printCollection(3);
                break;
            case 4:
                break;
            default:
                System.out.println("wrong input, retry");
        }
    }

    public static int chooseBetween(List<String> options,String prompt){
        int i = 1;
        for(String option : options){
            System.out.println(" "+i+" - "+option);
            i++;
        }
        System.out.print(prompt);
        int r = scanner.nextInt();
        clearCLI();
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