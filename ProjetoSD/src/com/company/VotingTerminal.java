package com.company;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.io.StreamCorruptedException;
import java.net.MulticastSocket;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.io.IOException;
import java.sql.SQLOutput;
import java.util.Scanner;
import java.util.concurrent.CopyOnWriteArrayList;

public class VotingTerminal extends Thread {
    private String MULTICAST_ADDRESS_TERM = "224.3.2.1";
    private String MULTICAST_ADDRESS_LOGIN = "224.3.2.2";
    private int PORT = 4321;
    private VotingThread vt;
    private boolean ready = true;

    public static void main(String[] args) {
        VotingTerminal term = new VotingTerminal(args[0]);
        term.start();
    }

    public VotingTerminal(String id) {
        super(id);
    }

    public void run() {
        MulticastSocket socket = null;
        MulticastSocket login_socket = null;

        try {
            socket = new MulticastSocket(PORT);  // Socket para comunicar com o servidor
            InetAddress group = InetAddress.getByName(MULTICAST_ADDRESS_TERM);
            socket.joinGroup(group);

            Communication c = new Communication(socket, group);

            login_socket = new MulticastSocket(PORT);  // Socket para fazer login no servidor
            InetAddress login_group = InetAddress.getByName(MULTICAST_ADDRESS_LOGIN);
            login_socket.joinGroup(login_group);

            Communication login_c = new Communication(login_socket, login_group);

            while (true) {
                this.vt = new VotingThread(getName());

                String[] message = c.receiveOperation().split(";");
                String message_type = c.getMessageType(message[0]);

                if (message_type.equals("term_fetch") && ready) {
                    ready = !ready;
                    c.sendOperation("type|term_ready;term|" + getName());
                }

                else if (message_type.equals("term_unlock")) {
                    String term = message[1].split("\\|")[1];

                    if (term.equals(getName())) {
                        System.out.println("Terminal de voto " + getName());

                        Scanner keyboard_scanner = new Scanner(System.in);
                        System.out.println("Introduza o seu username: ");
                        String username = keyboard_scanner.nextLine();
                        System.out.println("Introduza a sua password: ");
                        String password = keyboard_scanner.nextLine();

                        login_c.sendOperation("type|login_request;term|" + getName() + ";username|" + username + ";passowrd|" + password);
                    }
                }

                message = login_c.receiveOperation().split(";");
                message_type = login_c.getMessageType(message[0]);

                if (message_type.equals("login_accept")) {
                    String term = message[1].split("\\|")[1];

                    if (term.equals(getName())) {
                        System.out.println("Autenticação bem sucedida.");
                        this.vt.start();
                        this.vt.join();
                        ready = !ready;
                    }
                }

                else if (message_type.equals("login_deny")) {
                    String term = message[1].split("\\|")[1];

                    if (term.equals(getName())) {
                        System.out.println("Dados incorretos.");

                        Scanner keyboard_scanner = new Scanner(System.in);
                        System.out.println("Introduza o seu username: ");
                        String username = keyboard_scanner.nextLine();
                        System.out.println("Introduza a sua password: ");
                        String password = keyboard_scanner.nextLine();

                        login_c.sendOperation("type|login_request;term|" + getName() + ";username|" + username + ";passowrd|" + password);
                    }

                }
            }

        } catch(IOException | InterruptedException e){
                e.printStackTrace();
        } finally{
                socket.close();
        }
    }
}


class VotingThread extends Thread {
    private String MULTICAST_ADDRESS_VOTE = "224.3.2.3";
    private int PORT = 4321;

    public VotingThread(String id) {
        super(id);
    }

    public void run() {
        System.out.println("Terminal de voto " + getName());

        MulticastSocket socket = null;

        try {
            socket = new MulticastSocket();  // Socket para enviar os votos
            InetAddress group = InetAddress.getByName(MULTICAST_ADDRESS_VOTE);
            Communication c = new Communication(socket, group);

            Scanner keyboard_scanner = new Scanner(System.in);
            String readKeyboard = keyboard_scanner.nextLine();
            c.sendOperation(readKeyboard);
            System.out.println("Voto enviado.");

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            socket.close();
        }
    }
}
