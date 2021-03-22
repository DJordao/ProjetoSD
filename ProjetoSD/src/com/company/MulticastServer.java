package com.company;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.Scanner;
import java.util.concurrent.CopyOnWriteArrayList;

public class MulticastServer extends Thread{
    private String MULTICAST_ADDRESS_TERM = "224.3.2.1";
    private int PORT = 4321;
    private long SLEEP_TIME = 5000;
    private VoteReceiver receiver;

    public static void main(String[] args) {
        MulticastServer server = new MulticastServer(args[0]);
        server.start();
    }

    public MulticastServer(String department) {
        super("Mesa de voto " + department);
    }

    public void run() {
        MulticastSocket socket = null;

        System.out.println(this.getName() + " online...");

        try {
            socket = new MulticastSocket();  // create socket without binding it (only for sending)
            this.receiver = new VoteReceiver(new MulticastSocket(PORT)); // Thread que recebe os votos dos terminais
            Scanner keyboard_scanner = new Scanner(System.in);

            while (true) {
                String input = keyboard_scanner.nextLine();

                // Tem que se ir buscar ao RMI
                CopyOnWriteArrayList<Pessoa> p = new CopyOnWriteArrayList<>();
                p.add(new Pessoa("Diogo Filipe", "1234", "1234", "estudante", "DEI", 1234, "Leiria", "1234", null));

                for(int i = 0; i < p.size(); i++) {
                    if(p.get(i).getNum_cc().equals(input)) {
                        byte[] buffer = input.getBytes();
                        InetAddress group = InetAddress.getByName(MULTICAST_ADDRESS_TERM);
                        DatagramPacket packet = new DatagramPacket(buffer, buffer.length, group, PORT);
                        socket.send(packet);
                        break;
                    }
                }

                /*InetAddress group = InetAddress.getByName(MULTICAST_ADDRESS_TERM);
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length, group, PORT);
                socket.send(packet);

                try { sleep((long) (Math.random() * SLEEP_TIME)); } catch (InterruptedException e) { }*/
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            socket.close();
        }
    }
}

class VoteReceiver extends Thread {
    MulticastSocket socket;
    private boolean available = true;
    private String MULTICAST_ADDRESS_VOTE = "224.3.2.2";

    public VoteReceiver(MulticastSocket socket) {
        this.socket = socket;
        this.start();
    }

    public void run() {
        try {
            InetAddress group = InetAddress.getByName(MULTICAST_ADDRESS_VOTE);
            this.socket.joinGroup(group);
            while (true) {
                byte[] buffer = new byte[256];
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                this.socket.receive(packet);

                System.out.println("Receiving packet from " + packet.getAddress() + ':' + packet.getPort() + " with message: ");
                String message = new String(packet.getData(), 0, packet.getLength());
                System.out.println(message);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}