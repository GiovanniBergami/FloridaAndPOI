package org.example;

import com.mongodb.client.MongoCollection;
import mongoConnectors.mongoConnector;
import neoConnectors.neoConnector;

import java.util.Scanner;
import org.bson.Document;
public class Main {
    public static void main(String[] args) {
        neoConnector.connect();
        CLI.unsignedUser();
        mongoConnector.close();
        CLI.scanner.close();
        neoConnector.close();
    }
}


// cli con jline
//    Terminal terminal = TerminalBuilder.terminal();
//    LineReader lineReader = LineReaderBuilder.builder()
//            .terminal(terminal)
//            .build();
//
//    String line = lineReader.readLine("prompt> ");