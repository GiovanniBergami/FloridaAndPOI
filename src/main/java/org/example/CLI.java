package org.example;

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
                System.out.println("è 1");
                break;
            case "2":
                System.out.println("è 2");
                break;
            case "3":
                System.out.println("è 3");
                break;
        }
    }
}
