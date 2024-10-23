package org.example;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Map;
import java.util.Scanner;

import connectors.neoConnector;
import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.GraphDatabase;


import com.mongodb.client.*;
import connectors.mongoConnector;
import org.bson.Document;
import org.neo4j.driver.QueryConfig;


// Press Shift twice to open the Search Everywhere dialog and type `show whitespaces`,
// then press Enter. You can now see whitespace characters in your code.
public class Main {
    public static void main(String[] args) {
        // Press Alt+Invio with your caret at the highlighted text to see how
        // IntelliJ IDEA suggests fixing it.
        System.out.printf("Hello and welcome!");

        // Press Maiusc+F10 or click the green arrow button in the gutter to run the code.
        for (int i = 1; i <= 5; i++) {

            // Press Maiusc+F9 to start debugging your code. We have set one breakpoint
            // for you, but you can always add more by pressing Ctrl+F8.
            System.out.println("i = " + i);
        }
        String data;
        try {
            File osm = new File("dataset/florida_osm.json");
            Scanner osm_reader = new Scanner(osm);
            data = osm_reader.nextLine();
            System.out.println(data);
        } catch(FileNotFoundException e){
            System.out.println(e.getMessage());
        }

        //DB TEST
//        mongoConnector prova = new mongoConnector("prova1","myCollection");
//        prova.printCollection();
//        prova.close();
//
//
//
//        neoConnector.connect();
        CLI.start();











    }
}