package org.example;

import mongoConnectors.mongoConnector;
import neoConnectors.neoConnector;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {

//        neoConnector.connect();
        CLI.start();
        mongoConnector.close();
        CLI.scanner.close();
    }
}


// cli con jline
//    Terminal terminal = TerminalBuilder.terminal();
//    LineReader lineReader = LineReaderBuilder.builder()
//            .terminal(terminal)
//            .build();
//
//    String line = lineReader.readLine("prompt> ");