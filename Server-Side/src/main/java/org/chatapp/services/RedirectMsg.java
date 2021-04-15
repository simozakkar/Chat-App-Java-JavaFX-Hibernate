package org.chatapp.services;


import org.chatapp.entity.MSG;
import org.chatapp.entity.SocketClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.nio.channels.SocketChannel;
import java.time.LocalDateTime;
import java.util.Hashtable;

import static org.chatapp.data.DataUser.checkPermission;
import static org.chatapp.data.DateMSG.insertMSG;

public class RedirectMsg implements Runnable {
    private final SocketChannel socketChannel;
    public final Hashtable<String, SocketClient> listClientsSocket;

    public RedirectMsg(Hashtable<String, SocketClient> listClientsSocket, SocketChannel socketChannel) {
        this.socketChannel = socketChannel;
        this.listClientsSocket = listClientsSocket;
    }

    @Override
    public void run() {
        try{
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socketChannel.socket().getInputStream()));
            String permission = bufferedReader.readLine();
            String num = permission.substring(0, permission.indexOf(";"));
            String accessKey = permission.substring(permission.indexOf(";")+1);
            PrintStream printStream = new PrintStream(socketChannel.socket().getOutputStream());
            if (checkPermission(num, accessKey)) {
                printStream.println("200");
                String destination = bufferedReader.readLine();
                String content = bufferedReader.readLine();
                SocketClient socketClient = listClientsSocket.get(destination);
                if (socketClient != null){

                    while(!socketClient.isDisp());// wait to be the socket channel free
                    socketClient.setDisp(false);
                    PrintStream printStreamDestination = new PrintStream(socketClient.getSocketChannel().socket().getOutputStream());
                    printStreamDestination.println(num);
                    printStreamDestination.println(LocalDateTime.now().toString());
                    printStreamDestination.println(content);
                    socketClient.setDisp(true);
                }else {
                    insertMSG(new MSG(num, destination, LocalDateTime.now(), content));
                }
            }else {
                printStream.println("400");
            }
            socketChannel.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
