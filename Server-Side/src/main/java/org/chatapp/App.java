package org.chatapp;

import java.io.IOException;
public class App {

    public static void main(String[] args) {
        Server server = new Server();
        try {
            server.startListining();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
