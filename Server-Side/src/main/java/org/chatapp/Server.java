package org.chatapp;


import org.chatapp.entity.SocketClient;
import org.chatapp.services.*;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Hashtable;
import java.util.Iterator;

public class Server {
    public final Hashtable<String, SocketClient> listClientsSocket = new Hashtable<>();

    public void startListining() throws IOException {
        // create a server socket channel for a server with multiplport
        ServerSocketChannel server;
        Selector selector = Selector.open();
        int ports[] = new int[] { 1234, 1235, 1236, 1237, 1238, 1239, 1240, 1241};
        System.out.println("Listening multiplPort :");
        // loop through each port in our list and bind it to a ServerSocketChannel
        for (int port : ports) {
            server = ServerSocketChannel.open();
            server.configureBlocking(false);
            server.socket().bind(new InetSocketAddress(port));
            server.register(selector, SelectionKey.OP_ACCEPT);
            System.out.print(" "+port);
        }
        System.out.println();
        // Start Thread Manage the clients session every n seconds
        new Thread(new ManageClientSession(listClientsSocket)).start();

        while (true) {
            selector.select();
            Iterator<SelectionKey> selectedKeys = selector.selectedKeys().iterator();
            while (selectedKeys.hasNext()) {
                SelectionKey selectedKey = selectedKeys.next();

                if (selectedKey.isAcceptable()) {
                    SocketChannel socketChannel = ((ServerSocketChannel) selectedKey.channel()).accept();
                    if (socketChannel != null){
//                    socketChannel.configureBlocking(false);
                        // Switch with the local port of the socket connection, ... multiservices...
                        switch (socketChannel.socket().getLocalPort()){ // dosnt work with gePort method
                            case 1234:
                                // Receive a phone number and send to it a SMS with a validation code
                                new Thread(new SendCode(socketChannel)).start();
                                break;
                            case 1235:
                                // Receive a phone number and the validation code
                                // Send response Ok (the validation code is correct) or !OK
                                new Thread(new CheckCode(socketChannel)).start();
                                break;
                            case 1236:
                                //      Receive phone number and the access key, verify if we have in the
                                // DB a user with the same PhNum and the access key, send the response to the App (OK or !OK)
                                new Thread(new VerifyUser(socketChannel)).start();
                                break;
                            case 1237:
                                new Thread(new SetupUserProfile(socketChannel)).start();
                                break;
                            case 1238:
                                // To send information aboute users (statu, photo, name ...)
                                new Thread(new SendInfoFriend(socketChannel)).start();
                                break;
                            case 1239:
                                System.out.println("Hello 1239");
                                new Thread(new AddClientSocket(listClientsSocket, socketChannel)).start();
                                break;
                            case 1240:
                                System.out.println("Hello 1240");
                                new Thread(new RedirectMsg(listClientsSocket, socketChannel)).start();
                                break;
                            case 1241:
                                System.out.println("Hello 1241");
                                new Thread(new ClientActif(socketChannel)).start();
                                break;
                        }
//                        socketChannel.close();
                    }
                } else if (selectedKey.isReadable()) {
                    System.out.println("Hello read");
                } else if (selectedKey.isWritable()) {
                    System.out.println("Hello write");
                }
//                selectedKey.channel().close();
            }
        }
    }
}
