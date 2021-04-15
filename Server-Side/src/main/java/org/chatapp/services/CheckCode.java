package org.chatapp.services;

import org.apache.commons.lang.RandomStringUtils;
import org.chatapp.entity.CodeValidation;
import org.chatapp.entity.User;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.nio.channels.SocketChannel;

import static org.chatapp.data.DataCodeValidition.checkCodeValidition;
import static org.chatapp.data.DataCodeValidition.deleteCodeValidition;
import static org.chatapp.data.DataUser.getUser;
import static org.chatapp.data.DataUser.insertUser;


public class CheckCode implements Runnable{
    // Receive the Validation Code
    // Check if is correct
    // Send OK or !OK to the Client
    // Delete the Validation Code from the DB if OK

    private final SocketChannel socketChannel;

    public CheckCode(SocketChannel socketChennel) {
        this.socketChannel = socketChennel;
    }

    @Override
    public void run() {
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socketChannel.socket().getInputStream()));
            // Receive
            String codeAndNum = bufferedReader.readLine();
            String code = codeAndNum.substring(0,4);
            String num = codeAndNum.substring(4);
            PrintStream printStream = new PrintStream(socketChannel.socket().getOutputStream());
            // Check
            if (checkCodeValidition(num, code)){
                // Generate a access key for the new connection
                String accessKey = RandomStringUtils.randomAlphanumeric(10);
                // Check if exist
                User user = getUser(num);
                if (user == null){
                    // Insert
                    user = new User(num, accessKey, num, null, false);
                    insertUser(user);
                }else{
                    user.setAccessKey(accessKey);
                    user.setStatu(false);
                    // Insert
                    insertUser(user);
                }
                // Delete
                deleteCodeValidition(new CodeValidation(num, code));
                printStream.println("200");
                printStream.println(user.getPhoneNum()+";"+user.getAccessKey());
            }else{
                printStream.println("400");
            }
            socketChannel.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
