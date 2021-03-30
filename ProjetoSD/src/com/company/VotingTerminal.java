package com.company;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.io.StreamCorruptedException;
import java.net.MulticastSocket;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.io.IOException;
import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.CopyOnWriteArrayList;

public class VotingTerminal extends Thread {
    private String MULTICAST_ADDRESS_TERM = "224.3.2.1";
    private int PORT = 4321;
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
        LoginThread lt;

        try {
            socket = new MulticastSocket(PORT);  // Socket para comunicar com o servidor
            InetAddress group = InetAddress.getByName(MULTICAST_ADDRESS_TERM);
            socket.joinGroup(group);

            Communication c = new Communication(socket, group);

            while (true) {
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

                        String n_cc = message[2].split("\\|")[1];

                        while (!message_type.equals("send_elec")) {
                            message = c.receiveOperation().split(";");
                            message_type = c.getMessageType(message[0]);
                        }

                        String elec_name = message[1].split("\\|")[1];
                        int n = Integer.parseInt(message[2].split("\\|")[1]);
                        ArrayList<String> candidates = new ArrayList<>();

                        for (int i = 3; i < n + 3; i++) {
                            candidates.add(message[i].split("\\|")[1]);
                        }

                        // Efetuar login
                        lt = new LoginThread(getName(), n_cc, elec_name, candidates);
                        lt.start();
                        lt.join();

                        ready = !ready;
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


class LoginThread extends Thread {
    private String MULTICAST_ADDRESS_LOGIN = "224.3.2.2";
    private int PORT = 4321;
    private String n_cc;
    private String elec_name;


    public LoginThread(String id, String n_cc, String elec_name, ArrayList<String> candidates) {
        super(id);
        this.n_cc = n_cc;
    }

    public void run() {
        MulticastSocket socket = null;
        VotingThread vt;

        try {
            vt = new VotingThread(getName());

            socket = new MulticastSocket(PORT);  // Socket para fazer login
            InetAddress group = InetAddress.getByName(MULTICAST_ADDRESS_LOGIN);
            socket.joinGroup(group);
            Communication c = new Communication(socket, group);

            Scanner keyboard_scanner = new Scanner(System.in);
            String n_cc = "";
            while (!n_cc.equals(this.n_cc)) {
                System.out.println("Introduza o seu nº do cc: ");
                n_cc = keyboard_scanner.nextLine();
            }
            System.out.println("Introduza a sua password: ");
            String password = keyboard_scanner.nextLine();

            c.sendOperation("type|login_request;term|" + getName() + ";n_cc|" + n_cc + ";passowrd|" + password);

            while (true) {
                String[] message = c.receiveOperation().split(";");
                String message_type = c.getMessageType(message[0]);

                if (message_type.equals("login_accept")) {
                    String term = message[1].split("\\|")[1];

                    if (term.equals(getName())) {
                        System.out.println("Autenticação bem sucedida.");

                        // Vote
                        vt.start();
                        vt.join();

                        break;
                    }
                }

                else if (message_type.equals("login_deny")) {
                    String term = message[1].split("\\|")[1];

                    if (term.equals(getName())) {
                        System.out.println("Dados incorretos.");

                        keyboard_scanner = new Scanner(System.in);
                        System.out.println("Introduza o seu username: ");
                        n_cc = keyboard_scanner.nextLine();
                        System.out.println("Introduza a sua password: ");
                        password = keyboard_scanner.nextLine();

                        c.sendOperation("type|login_request;term|" + getName() + ";n_cc|" + n_cc + ";passowrd|" + password);

                    }
                }
            }

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        } finally {
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
