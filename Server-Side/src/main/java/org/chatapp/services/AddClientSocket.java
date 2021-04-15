package org.chatapp.services;

import org.chatapp.entity.MSG;
import org.chatapp.entity.SocketClient;
import org.chatapp.entity.User;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.nio.channels.SocketChannel;
import java.time.LocalDateTime;
import java.util.Hashtable;
import java.util.List;

import static org.chatapp.data.DataUser.*;
import static org.chatapp.data.DateMSG.deleteMSGs;
import static org.chatapp.data.DateMSG.getMSGs;

public class AddClientSocket implements Runnable {
    public final Hashtable<String, SocketClient> listClientsSocket;
    private final SocketChannel socketChannel;

    public AddClientSocket(Hashtable<String, SocketClient> listClientsSocket, SocketChannel socketChannel) {
        this.listClientsSocket = listClientsSocket;
        this.socketChannel = socketChannel;
    }

    @Override
    public void run() {
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socketChannel.socket().getInputStream()));
            String numAndAccessKey= bufferedReader.readLine();
            String num = numAndAccessKey.substring(0, numAndAccessKey.indexOf(";"));
            String accessKey = numAndAccessKey.substring(numAndAccessKey.indexOf(";")+1);
            PrintStream printStream = new PrintStream(socketChannel.socket().getOutputStream());
            if (checkPermission(num, accessKey)){
                printStream.println("200");
                // get msgs from DB
                List<MSG> msgs = getMSGs(num);
                if (msgs != null){
                    for (MSG msg:msgs){
                        // send msg
                        printStream.println(msg.getEmitter());
                        printStream.println(msg.getDateTime());
                        printStream.println(msg.getContent());
                    }
                }
                // delete msgs from the DB
                deleteMSGs(msgs);
                // update user
                User user = getUser(num);
                user.setStatu(true);
                user.setLastTime(LocalDateTime.now());
                insertUser(user);

                // remove Object with key == num if exist
                listClientsSocket.remove(num);
                // put a new Object<num><socketChannel> in the map list
                listClientsSocket.put(num, new SocketClient(socketChannel,true));


            }else {
                printStream.println("400");
            }
//            socketChannel.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
