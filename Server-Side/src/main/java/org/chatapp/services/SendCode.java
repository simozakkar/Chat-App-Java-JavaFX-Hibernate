package org.chatapp.services;

import org.chatapp.SMSSender;
import org.chatapp.entity.CodeValidation;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.nio.channels.SocketChannel;

import static org.chatapp.data.DataCodeValidition.insertCodeValidition;

public class SendCode implements Runnable{
    // Generate a Validation code with 4 numbers
    // Send a SMS with validation code
    // Insert the phone number and the validation code in the DB (codeValidation table)

    private final SocketChannel socketChannel;

    public SendCode(SocketChannel socketChannel) {
        this.socketChannel = socketChannel;
    }

    @Override
    public void run() {
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socketChannel.socket().getInputStream()));
            String numero = bufferedReader.readLine();
            // Default Validation code
            String  valCode = "1111";

            // Generate Validation code
//            String  valCode = getRandomNumber(1000, 9999).toString();
            // Send a validation code
//            SMSSender smsSender = new SMSSender();
//            try {
//                // Go to https://www.smsmode.com/en/ create a new account
//                // Create a access key ....
//                smsSender.sendSmsPost("AccessToken", "The validation code is : "+valCode, numero.substring(1), "Emetteur", "0");
//            } catch (IOException e) {
//                e.printStackTrace();
//            }

            // Insert
            insertCodeValidition(new CodeValidation(numero, valCode));
            PrintStream printStream = new PrintStream(socketChannel.socket().getOutputStream());
            printStream.println("200");
            socketChannel.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public Integer getRandomNumber(int min, int max) {
        return (int) ((Math.random() * (max - min)) + min);
    }
}
