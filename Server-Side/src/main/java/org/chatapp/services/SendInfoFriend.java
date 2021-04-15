package org.chatapp.services;

import org.chatapp.entity.User;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.nio.channels.SocketChannel;

import static org.chatapp.data.DataUser.getUser;

public class SendInfoFriend implements Runnable{
    private final SocketChannel socketChannel;

    public SendInfoFriend(SocketChannel socketChannel) {
        this.socketChannel = socketChannel;
    }

    @Override
    public void run() {
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socketChannel.socket().getInputStream()));
            String numero = bufferedReader.readLine();
            User user = getUser(numero);
            PrintStream printStream = new PrintStream(socketChannel.socket().getOutputStream());
            if(user == null){
                printStream.println("404");
            }else {
                printStream.println(user.getName());
                printStream.println(user.getStatu());
                if (!user.getStatu()) printStream.println(user.getLastTime().toString());
//                send image ??
            }
            socketChannel.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
