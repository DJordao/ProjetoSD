package com.company;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.io.StreamCorruptedException;
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
        VotingTerminal term = new VotingTerminal(args[0]);
        term.start();
    }

    public VotingTerminal(String id) {
        super(id);
        this.vt = new VotingThread(id);
    }

    public void run() {
        MulticastSocket socket = null;

        try {
            socket = new MulticastSocket(PORT);  // Socket para comunicar com o servidor
            InetAddress group = InetAddress.getByName(MULTICAST_ADDRESS_TERM);
            socket.joinGroup(group);
            Communication c = new Communication(socket, group);

            while (true) {
                String message = c.ReceiveOperation();

                if(message.equals("get_term")) {
                    if(!this.vt.isAlive()) {
                        c.SendOperation(getName());

                        System.out.println("Terminal de voto " + getName());

                        Pessoa p = null;
                        while (p == null) {
                            p = (Pessoa) c.ReceiveObject();
                        }

                        while(true) {
                            Scanner keyboard_scanner = new Scanner(System.in);
                            System.out.println("Introduza o seu username: ");
                            String username = keyboard_scanner.nextLine();
                            System.out.println("Introduza a sua password: ");
                            String password = keyboard_scanner.nextLine();

                            if(p.getUsername().equals(username) && p.getPassword().equals(password)) {
                                System.out.println("Autenticação bem sucedida.");
                                this.vt.start();
                                break;
                            }
                        }
                    }
                    else {
                        c.SendOperation("occupied");
                    }


                }
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            socket.close();
        }
    }
}

class VotingThread extends Thread {
    private String MULTICAST_ADDRESS_VOTE = "224.3.2.2";
    private int PORT = 4321;

    public VotingThread(String id) {
        super(id);
    }

    public void run() {
        System.out.println("Terminal de voto " + getName());

        MulticastSocket socket = null;

        try {
            socket = new MulticastSocket();  // create socket without binding it (only for sending)
            Scanner keyboard_scanner = new Scanner(System.in);
            while (true) {
                String readKeyboard = keyboard_scanner.nextLine();
                byte[] buffer = readKeyboard.getBytes();

                InetAddress group = InetAddress.getByName(MULTICAST_ADDRESS_VOTE);
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length, group, PORT);
                socket.send(packet);
                return;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            socket.close();
        }
    }
}
