package org.service;

import mongoConnectors.mongoConnector;
import neoConnectors.neoConnector;

public class Main {
    public static void main(String[] args) {
        neoConnector.connect();
        CLI.unsignedUser();
        exit();
    }
    public static void exit(){
        mongoConnector.close();
        CLI.scanner.close();
        neoConnector.close();
        System.exit(0);
    }
}


// cli con jline
//    Terminal terminal = TerminalBuilder.terminal();
//    LineReader lineReader = LineReaderBuilder.builder()
//            .terminal(terminal)
//            .build();
//
//    String line = lineReader.readLine("prompt> ");