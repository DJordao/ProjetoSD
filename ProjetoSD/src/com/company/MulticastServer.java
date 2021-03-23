package com.company;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.Scanner;
import java.util.concurrent.CopyOnWriteArrayList;

public class MulticastServer extends Thread{
    private String MULTICAST_ADDRESS_TERM = "224.3.2.1";
    private int PORT = 4321;
    private VoteReceiver collector;

    public static void main(String[] args) {
        MulticastServer server = new MulticastServer(args[0]);
        server.start();
    }

    public MulticastServer(String department) {
        super("Mesa de voto " + department);
    }

    public void run() {
        boolean id = false;
        MulticastSocket socket = null;
        Pessoa p = null;

        System.out.println(this.getName() + " online...");

        try {
            socket = new MulticastSocket(PORT);  // Socket para comunicar com os terminais
            InetAddress group = InetAddress.getByName(MULTICAST_ADDRESS_TERM);
            socket.joinGroup(group);
            this.collector = new VoteReceiver(new MulticastSocket(PORT)); // Thread que recebe os votos dos terminais
            Scanner keyboard_scanner = new Scanner(System.in);

            while (true) {
                while (!id) {
                    System.out.println("Indique o seu nª do cc:");
                    String input = keyboard_scanner.nextLine();

                    // Tem que se ir buscar ao RMI
                    CopyOnWriteArrayList<Pessoa> l = new CopyOnWriteArrayList<>();
                    l.add(new Pessoa("Diogo Filipe", "1234", "1234", "estudante", "DEI", 1234, "Leiria", "1234", null));

                    for(int i = 0; i < l.size(); i++) {
                        p = l.get(i);
                        if(p.getNum_cc().equals(input)) {
                            id = true;
                            System.out.println("Identificação bem sucedida.");
                            System.out.println("A procurar um terminal de voto...");

                            String get_term = "get_term";
                            byte[] buffer = get_term.getBytes();
                            socket.send(new DatagramPacket(buffer, buffer.length, group, PORT));

                            break;
                        }
                    }

                    if(!id) {
                        System.out.println("Identificação falhada.");
                    }
                }

                while (true) {
                    byte[] buffer = new byte[256];
                    DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                    socket.receive(packet);

                    String message = new String(packet.getData(), 0, packet.getLength());
                    if(message.equals("1")) {
                        System.out.println("Pode votar no terminal " + message);

                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        ObjectOutputStream oos = new ObjectOutputStream(baos);

                        oos.writeObject(p);
                        oos.flush();
                        byte[] obj_buffer = baos.toByteArray();
                        socket.send(new DatagramPacket(obj_buffer, obj_buffer.length, group, PORT));

                        id = false;
                        break;
                    }
                }
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