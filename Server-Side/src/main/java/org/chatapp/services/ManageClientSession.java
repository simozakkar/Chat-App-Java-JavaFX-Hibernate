package org.chatapp.services;

import org.chatapp.entity.SocketClient;
import org.chatapp.entity.User;

import java.nio.channels.SocketChannel;
import java.time.LocalDateTime;
import java.util.Hashtable;
import java.util.List;

import static org.chatapp.data.DataUser.getActifUsers;
import static org.chatapp.data.DataUser.insertUser;

public class ManageClientSession implements Runnable{
    public final Hashtable<String, SocketClient> listClientsSocket;

    public ManageClientSession(Hashtable<String, SocketClient> listClientsSocket) {
        this.listClientsSocket = listClientsSocket;
    }


    @Override
    public void run() {
        while(true){
            List<User> actifUsers = getActifUsers();
            for (User user:actifUsers){
                String key = user.getPhoneNum();
                if (listClientsSocket.containsKey(key)){
                    if (user.getLastTime().compareTo(LocalDateTime.now().minusSeconds(12)) < 0){
                        user.setStatu(false);
                        listClientsSocket.remove(key);
                    }
                }else {
                    user.setStatu(false);
                    insertUser(user);
                }
            }
            try {
                Thread.sleep(30000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
