package org.example;

import mongoConnectors.POIConnector;
import mongoConnectors.ReviewConnector;

import java.util.Scanner;

public class CLI {
     static Scanner scanner = new Scanner(System.in);

    public static void start(){

        System.out.println("Starting...");

        //parte iniziale per dividere tra unsigned user , user e admin, con autenticazione

        //cose messe ora per provare
        System.out.println("Insert POIs (1) / Insert Users (2) / Insert Reviews (3)");

        String activity = scanner.nextLine();

        switch(activity){
            case "1":
                System.out.println("Insert POIs");
                POIConnector.insertPOIs("dataset/florida_business_yelp.json"); //questo verrà eventualmente inserito da tastiera
                POIConnector.count();
                break;
            case "2":
                System.out.println("Insert Users");
                break;
            case "3":
                System.out.println("Insert Reviews");
                ReviewConnector.insertReviews("dataset/review_florida_reduced.json");
                ReviewConnector.count();
                ReviewConnector.printCollection(3);
                break;
        }
    }
}
