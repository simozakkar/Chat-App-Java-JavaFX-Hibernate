package org.chatapp.services;

import org.chatapp.entity.User;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.nio.channels.SocketChannel;

import static org.chatapp.data.DataUser.getUser;

public class SetupUserProfile implements Runnable{
    private final SocketChannel socketChannel;

    public SetupUserProfile(SocketChannel socketChannel) {
        this.socketChannel = socketChannel;
    }

    @Override
    public void run() {
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socketChannel.socket().getInputStream()));
            String numAndAccessKey= bufferedReader.readLine();
            String num = numAndAccessKey.substring(0, numAndAccessKey.indexOf(";"));
            String accessKey = numAndAccessKey.substring(numAndAccessKey.indexOf(";")+1);
            User user = getUser(num);
            PrintStream printStream = new PrintStream(socketChannel.socket().getOutputStream());
            if (user != null && user.getAccessKey().equals(accessKey)){
                printStream.println("200");
                printStream.println(user.getName());
//                printStream.println(user.getName());
            }else {
                printStream.println("400");
            }
            socketChannel.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
