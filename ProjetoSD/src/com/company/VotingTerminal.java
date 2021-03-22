package com.company;

import java.net.MulticastSocket;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.CopyOnWriteArrayList;

public class VotingTerminal extends Thread {
    private String MULTICAST_ADDRESS_TERM = "224.3.2.1";
    private int PORT = 4321;
    private VotingThread vt;

    public static void main(String[] args) {
        VotingTerminal term = new VotingTerminal();
        term.start();
    }

    public VotingTerminal() {
        super("Terminal de voto");
        this.vt = new VotingThread();
    }

    public VotingThread getVt() {
        return this.vt;
    }

    public void run() {
        MulticastSocket socket = null;

        try {
            socket = new MulticastSocket(PORT);  // create socket and bind it
            InetAddress group = InetAddress.getByName(MULTICAST_ADDRESS_TERM);
            socket.joinGroup(group);
            while (true) {
                byte[] buffer = new byte[256];
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);

                System.out.println("Received packet from " + packet.getAddress().getHostAddress() + ":" + packet.getPort() + " with message:");
                String message = new String(packet.getData(), 0, packet.getLength());
                System.out.println(message);

                if(!getVt().isAlive()) {

                }
                Scanner keyboard_scanner = new Scanner(System.in);
                //String username = keyboard_scanner.nextLine();
                String password = keyboard_scanner.nextLine();

                // Tem que receber do servidor
                Pessoa p = new Pessoa("Diogo Filipe", "df", "1234", "estudante", "DEI", 1234, "Leiria", "1234", null);

                if(/*p.getUsername().equals(username) && */p.getPassword().equals(password)) {
                    System.out.println("Autenticado com sucesso.");

                }
                else {
                    System.out.println("Dado(s) de autenticação errado(s).");
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            socket.close();
        }
    }
}

class VotingThread extends Thread {
    private String MULTICAST_ADDRESS_VOTE = "224.3.2.2";
    private int PORT = 4321;

    public VotingThread() {
        super("Terminal");
    }

    public void run() {
        MulticastSocket socket = null;
        System.out.println(this.getName() + " online...");
        try {
            socket = new MulticastSocket();  // create socket without binding it (only for sending)
            Scanner keyboard_scanner = new Scanner(System.in);
            while (true) {
                String readKeyboard = keyboard_scanner.nextLine();
                byte[] buffer = readKeyboard.getBytes();

                InetAddress group = InetAddress.getByName(MULTICAST_ADDRESS_VOTE);
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length, group, PORT);
                socket.send(packet);
                this.join();
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        } finally {
            socket.close();
        }
    }
}
