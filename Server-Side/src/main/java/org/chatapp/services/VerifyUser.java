package org.chatapp.services;

import org.chatapp.entity.User;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.nio.channels.SocketChannel;

import static org.chatapp.data.DataUser.checkPermission;

public class VerifyUser implements Runnable{
    private final SocketChannel socketChannel;

    public VerifyUser(SocketChannel socketChannel) {
        this.socketChannel = socketChannel;
    }

    @Override
    public void run() {
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socketChannel.socket().getInputStream()));
            String numAndAccessKey= bufferedReader.readLine();
            PrintStream printStream = new PrintStream(socketChannel.socket().getOutputStream());
            if (numAndAccessKey.contains(";")) {
                String num = numAndAccessKey.substring(0, numAndAccessKey.indexOf(";"));
                String accessKey = numAndAccessKey.substring(numAndAccessKey.indexOf(";") + 1);
                if (checkPermission(num, accessKey)) {
                    printStream.println("200");
                } else {
                    printStream.println("400");
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
