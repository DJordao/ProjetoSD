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
    private String MULTICAST_ADDRESS_LOGIN = "224.3.2.2";
    private String MULTICAST_ADDRESS_VOTE = "224.3.2.3";
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
        //LoginThread lt;

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
                        MulticastSocket login_socket = null;
                        //VotingThread vt;

                        try {
                            //vt = new VotingThread(getName());

                            login_socket = new MulticastSocket(PORT);  // Socket para fazer login
                            InetAddress login_group = InetAddress.getByName(MULTICAST_ADDRESS_LOGIN);
                            login_socket.joinGroup(login_group);
                            Communication login_c = new Communication(login_socket, login_group);

                            Scanner keyboard_scanner = new Scanner(System.in);
                            String input = "";
                            while (!input.equals(n_cc)) {
                                System.out.println("Introduza o seu nº do cc: ");
                                input = keyboard_scanner.nextLine();
                            }
                            System.out.println("Introduza a sua password: ");
                            String password = keyboard_scanner.nextLine();

                            login_c.sendOperation("type|login_request;term|" + getName() + ";n_cc|" + n_cc + ";passowrd|" + password);

                            while (true) {
                                message = login_c.receiveOperation().split(";");
                                message_type = login_c.getMessageType(message[0]);

                                if (message_type.equals("login_accept")) {
                                    term = message[1].split("\\|")[1];

                                    if (term.equals(getName())) {
                                        System.out.println("Autenticação bem sucedida.");

                                        // Vote
                                        System.out.println("Terminal de voto " + getName());

                                        MulticastSocket vote_socket = null;

                                        try {
                                            vote_socket = new MulticastSocket();  // Socket para enviar os votos
                                            InetAddress vote_group = InetAddress.getByName(MULTICAST_ADDRESS_VOTE);
                                            Communication vote_c = new Communication(vote_socket, vote_group);

                                            keyboard_scanner = new Scanner(System.in);
                                            String readKeyboard = keyboard_scanner.nextLine();
                                            vote_c.sendOperation(readKeyboard);
                                            System.out.println("Voto enviado.");

                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        } finally {
                                            vote_socket.close();
                                        }

                                        break;
                                    }
                                }

                                else if (message_type.equals("login_deny")) {
                                    term = message[1].split("\\|")[1];

                                    if (term.equals(getName())) {
                                        System.out.println("Dados incorretos.");

                                        keyboard_scanner = new Scanner(System.in);
                                        System.out.println("Introduza o seu username: ");
                                        n_cc = keyboard_scanner.nextLine();
                                        System.out.println("Introduza a sua password: ");
                                        password = keyboard_scanner.nextLine();

                                        login_c.sendOperation("type|login_request;term|" + getName() + ";n_cc|" + n_cc + ";passowrd|" + password);

                                    }
                                }
                            }

                        } catch (IOException e) {
                            e.printStackTrace();
                        } finally {
                            login_socket.close();
                        }

                        ready = !ready;
                    }
                }
            }

        } catch(IOException e){
                e.printStackTrace();
        } finally{
                socket.close();
        }
    }
}


/*class LoginThread extends Thread {
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
}*/


/*class VotingThread extends Thread {
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
}*/
