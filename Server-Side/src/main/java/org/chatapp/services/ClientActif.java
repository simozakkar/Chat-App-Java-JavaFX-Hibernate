package org.chatapp.services;

import org.chatapp.entity.User;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.nio.channels.SocketChannel;
import java.time.LocalDateTime;

import static org.chatapp.data.DataUser.*;

public class ClientActif implements Runnable{
    private final SocketChannel socketChannel;

    public ClientActif(SocketChannel socketChannel) {
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
                User user = getUser(num);
                user.setStatu(true);
                user.setLastTime(LocalDateTime.now());
                // Insert
                insertUser(user);
                printStream.println("200");
            }else {
                printStream.println("400");
            }
            socketChannel.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
