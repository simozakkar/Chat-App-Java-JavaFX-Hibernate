package org.chatapp.entity;

import java.nio.channels.SocketChannel;

public class SocketClient {
    private SocketChannel socketChannel;
    private boolean disp;

    public SocketClient(SocketChannel socketChannel, boolean disp) {
        this.socketChannel = socketChannel;
        this.disp = disp;
    }

    public SocketChannel getSocketChannel() {
        return socketChannel;
    }

    public void setSocketChannel(SocketChannel socketChannel) {
        this.socketChannel = socketChannel;
    }

    public boolean isDisp() {
        return disp;
    }

    public void setDisp(boolean disp) {
        this.disp = disp;
    }
}