package org.chatapp.ClientConnections;

import java.io.*;
import java.net.Socket;
import java.time.LocalDateTime;

import static java.lang.System.exit;

public class SendMSG implements Runnable{
    private final String permession;
    private String destination;
    private String content;


    public SendMSG(String permession) {
        this.permession = permession;
    }

    public SendMSG(String permession, String destination, String content) {
        this.permession = permession;
        this.destination = destination;
        this.content = content;
    }

    @Override
    public void run() {
        try {
            Socket socket = new Socket("localhost", 1240);
            PrintStream printStream = new PrintStream(socket.getOutputStream());
            printStream.println(permession);
            BufferedReader flux = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String resp = flux.readLine();

            if (resp.equals("200")) {
                printStream.println(destination);
                printStream.println(content);
                // Write the content in file
                try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(destination+".txt", true))) {
                    bufferedWriter.append("right;").append(content)
                            .append(";").append(LocalDateTime.now().toString());
                    bufferedWriter.newLine();
                } catch (IOException e) {
                    // Exception handling
                }
            }

        }catch (Exception e){

        }
    }
}
