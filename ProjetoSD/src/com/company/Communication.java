package com.company;

import java.io.*;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketTimeoutException;
import java.util.Arrays;

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

    public void sendObject(Object o) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);

        oos.writeObject(o);
        oos.flush();
        byte[] obj_buffer = baos.toByteArray();

        String id = "object";
        byte[] id_buffer = id.getBytes();

        baos = new ByteArrayOutputStream();
        baos.write(id_buffer);
        baos.write(obj_buffer);

        byte buffer[] = baos.toByteArray();

        socket.send(new DatagramPacket(buffer, buffer.length, group, PORT));
    }

    public Object receiveObject() throws IOException, ClassNotFoundException {
        socket.setSoTimeout(5000);
        byte[] buffer = new byte[100000];
        socket.receive(new DatagramPacket(buffer, buffer.length, group, PORT));

        byte[] id_buffer = Arrays.copyOfRange(buffer, 0, 6);
        String id = new String(id_buffer, 0, id_buffer.length);

        if(id.equals("object")) {
            byte[] obj_buffer = Arrays.copyOfRange(buffer, 6, buffer.length);

            ByteArrayInputStream bais = new ByteArrayInputStream(obj_buffer);
            ObjectInputStream ois = new ObjectInputStream(bais);
            Object read_object = ois.readObject();

            if (read_object instanceof Pessoa) {
                return read_object;
            }
        }

        return null;
    }
}
