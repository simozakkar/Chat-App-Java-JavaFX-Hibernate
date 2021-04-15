package org.chatapp.ClientConnections;


import javafx.application.Platform;
import org.chatapp.MainController;
import org.chatapp.MsgBoxController;

import java.io.*;
import java.net.Socket;

import static java.lang.System.exit;

// Make connection with the server in 1239 port and wait for receive messages
public class ReceiveMSGConnection implements Runnable{

    private final String permession;

    public ReceiveMSGConnection(String phoneNumAndCode) {
        this.permession = phoneNumAndCode;
    }


    @Override
    public void run() {
        try {
            Socket socket = new Socket("localhost", 1239);
            PrintStream printStream = new PrintStream(socket.getOutputStream());
            printStream.println(permession);
            BufferedReader flux = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String resp = flux.readLine();
            if (resp.equals("200")) {
                while(true){
                    String emitter = flux.readLine();
                    String dateTime = flux.readLine();
                    String content = flux.readLine();
                    System.out.println(emitter+dateTime+content);

                    // Display the msg.
                    Platform.runLater(()->{
                        // Write the content in file
                        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(emitter+".txt", true))) {
                            bufferedWriter.append("left;").append(content)
                                    .append(";").append(dateTime);
                            bufferedWriter.newLine();
                        } catch (IOException e) {
                            // Exception handling
                        }

                        if ( MainController.msgBoxController != null && emitter.equals(MainController.msgBoxController.getFriend().getPhoneNum())){
                            MainController.msgBoxController.setMsgLeft(content, dateTime);
                        }
                    });
                }
            }else {
                System.out.println("No user in the DB with the number or the access key in the File");
                exit(-1);
            }

        }catch (Exception e){

        }
    }
}
