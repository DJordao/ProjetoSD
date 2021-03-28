package com.company;

import java.io.*;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketTimeoutException;

public class Communication {
    private MulticastSocket socket;
    private InetAddress group;
    private int PORT = 4321;
    private int TIMEOUT = 5000;

    public Communication(MulticastSocket socket, InetAddress group) {
        this.socket = socket;
        this.group = group;
    }

    public void sendOperation(String op) throws IOException {
        byte[] buffer = op.getBytes();
        socket.send(new DatagramPacket(buffer, buffer.length, group, PORT));
    }

    public String receiveOperation() throws IOException {
        try {
            socket.setSoTimeout(TIMEOUT);
            byte[] buffer = new byte[256];
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
            socket.receive(packet);

            return new String(packet.getData(), 0, packet.getLength());
        } catch (SocketTimeoutException s) {
            return "";
        }
    }

    public String getMessageType(String message) {
        if(message.length() > 0) {
            return message.split("\\|")[1];
        }

        return "";
    }
}
