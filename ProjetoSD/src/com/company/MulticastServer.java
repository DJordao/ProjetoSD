package com.company;

import java.io.IOException;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.Scanner;
import java.util.concurrent.CopyOnWriteArrayList;

public class MulticastServer extends Thread{
    private String MULTICAST_ADDRESS_TERM = "224.3.2.1";
    private int PORT = 4321;

    public static void main(String[] args) {
        MulticastServer server = new MulticastServer(args[0]);
        server.start();
    }

    public MulticastServer(String department) {
        super("Mesa de voto " + department);
    }

    public void run() {
        boolean id = false;
        Pessoa p = null;
        MulticastSocket socket = null;

        System.out.println(this.getName() + " online...");

        try {
            socket = new MulticastSocket(PORT);  // Socket para comunicar com os terminais
            InetAddress group = InetAddress.getByName(MULTICAST_ADDRESS_TERM);
            socket.joinGroup(group);

            Communication c = new Communication(socket, group);

            LoginHandler lt = new LoginHandler(); // Thread que recebe os pedidos de login dos terminais
            lt.start();

            VoteReceiver vr = new VoteReceiver(); // Thread que recebe os votos dos terminais
            vr.start();

            Scanner keyboard_scanner = new Scanner(System.in);

            while (true) {
                while (!id) { // Enquanto o ulilizador não estiver identificado
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

                            break;
                        }
                    }

                    if(!id) {
                        System.out.println("Identificação falhada.");
                    }
                }

                System.out.println("A procurar um terminal de voto...");
                while (true) {
                    c.sendOperation("type|term_fetch");

                    String[] message = c.receiveOperation().split(";");
                    String message_type = c.getMessageType(message[0]);

                    if(message_type.equals("term_ready")) {
                        String term = message[1].split("\\|")[1];
                        System.out.println("Pode votar no terminal " + term);

                        c.sendOperation("type|term_unlock;term|" + term);
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

class LoginHandler extends Thread {
    private String MULTICAST_ADDRESS_LOGIN = "224.3.2.2";
    private int PORT = 4321;

    public LoginHandler() {
        super();
    }

    public void run() {
        MulticastSocket socket = null;

        try {
            socket = new MulticastSocket(PORT);  // Socket para receber os votos
            InetAddress group = InetAddress.getByName(MULTICAST_ADDRESS_LOGIN);
            socket.joinGroup(group);
            Communication c = new Communication(socket, group);

            // Tem que se ir buscar ao RMI
            CopyOnWriteArrayList<Pessoa> l = new CopyOnWriteArrayList<>();
            Pessoa p = new Pessoa("Diogo Filipe", "1234", "1234", "estudante", "DEI", 1234, "Leiria", "1234", null);
            l.add(p);

            while (true) {
                String[] message = c.receiveOperation().split(";");
                String message_type = c.getMessageType(message[0]);

                if(message_type.equals("login_request")) {
                    String term = message[1].split("\\|")[1];
                    String username = message[2].split("\\|")[1];
                    String password = message[3].split("\\|")[1];
                    System.out.println(username + " " + p.getUsername() + " " + password + " " + p.getPassword());
                    if(p.getUsername().equals(username) && p.getPassword().equals(password)) {
                        c.sendOperation("type|login_accept;term|" + term);
                    }
                    else {
                        c.sendOperation("type|login_deny;term|" + term);
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
    private String MULTICAST_ADDRESS_VOTE = "224.3.2.3";
    private int PORT = 4321;

    public VoteReceiver() {
        super();
    }

    public void run() {
        MulticastSocket socket = null;

        try {
            socket = new MulticastSocket(PORT);  // Socket para receber os votos
            InetAddress group = InetAddress.getByName(MULTICAST_ADDRESS_VOTE);
            socket.joinGroup(group);
            Communication c = new Communication(socket, group);

            while (true) {
                String op = c.receiveOperation();

                //System.out.println(op);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            socket.close();
        }
    }
}