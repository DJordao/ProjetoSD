package com.company;

import java.net.MulticastSocket;
import java.net.InetAddress;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;


public class VotingTerminal extends Thread {
    private String MULTICAST_ADDRESS_TERM = "224.3.2.1";
    private String MULTICAST_ADDRESS_LOGIN = "224.3.2.2";
    private String MULTICAST_ADDRESS_VOTE = "224.3.2.3";
    private int PORT = 4321;
    private boolean ready = true;
    private TimerThread tt;

    public static void main(String[] args) throws InterruptedException {
        while(true) {
            VotingTerminal term = new VotingTerminal(args[0]);
            term.start();
            term.join();
        }
    }

    public VotingTerminal(String id) {
        super(id);
    }

    private void startTimer() {
        tt = new TimerThread(this);
        tt.start();
    }

    public void run() {
        System.out.println("Terminal de voto " + getName() + " bloqueado.");
        MulticastSocket socket = null;

        try {
            socket = new MulticastSocket(PORT);  // Socket para comunicar com o servidor
            InetAddress group = InetAddress.getByName(MULTICAST_ADDRESS_TERM);
            socket.joinGroup(group);

            Communication c = new Communication(socket, group);

            while (true) {
                String[] message = c.receiveOperation().split(";");
                String message_type = c.getMessageType(message[0]);

                if (message_type.equals("term_fetch") && ready) {
                    System.out.println("entrei");
                    ready = false;

                    c.sendOperation("type|term_ready;term|" + getName());
                }
                else if (message_type.equals("term_unlock")) {
                    String term = message[1].split("\\|")[1];

                    if (term.equals(getName())) {
                        System.out.println("Terminal de voto " + getName() + " desbloqueado");

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

                        try {
                            login_socket = new MulticastSocket(PORT);  // Socket para fazer login
                            InetAddress login_group = InetAddress.getByName(MULTICAST_ADDRESS_LOGIN);
                            login_socket.joinGroup(login_group);
                            Communication login_c = new Communication(login_socket, login_group);

                            Scanner keyboard_scanner = new Scanner(System.in);
                            String input = "";
                            while (!input.equals(n_cc)) {
                                System.out.println("Introduza o seu nº do cc: ");
                                startTimer();
                                input = keyboard_scanner.nextLine();
                                tt.stop();
                            }
                            System.out.println("Introduza a sua password: ");
                            startTimer();
                            String password = keyboard_scanner.nextLine();
                            tt.stop();

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

                                            System.out.println(elec_name);
                                            for(int i = 2; i < n; i++) {
                                                System.out.println(i-1 + "-> " + candidates.get(i));
                                            }
                                            System.out.println("Se não introduzir nenhum caracter o voto é contado como branco.");
                                            System.out.println("Se introduzir um caracter diferente dos números apresentados o voto é contado como nulo.");
                                            System.out.println("Introduza o número correspondente à sua escolha: ");
                                            keyboard_scanner = new Scanner(System.in);
                                            startTimer();
                                            String vote = keyboard_scanner.nextLine();
                                            tt.stop();

                                            if(vote.equals("")) {
                                                vote = "Branco";
                                            }
                                            else {
                                                try {
                                                    int option = Integer.parseInt(vote);

                                                    if(option > 0 && option < n-1) {
                                                        vote = candidates.get(option + 1);
                                                    }
                                                    else {
                                                        vote = "Nulo";
                                                    }
                                                } catch (NumberFormatException e) {
                                                    vote = "Nulo";
                                                }
                                            }

                                            vote_c.sendOperation("type|send_vote;elec_name|" + elec_name + ";vote|" + vote);
                                            System.out.println("Voto enviado.");

                                            login_c.sendOperation("type|user_voted;elec_name|" + elec_name + ";user|" + n_cc);

                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        } finally {
                                            if(vote_socket != null) vote_socket.close();
                                        }

                                        break;
                                    }
                                }

                                else if (message_type.equals("login_deny")) {
                                    term = message[1].split("\\|")[1];

                                    // Nova tentativa de login
                                    if (term.equals(getName())) {
                                        System.out.println("Dados incorretos.");

                                        keyboard_scanner = new Scanner(System.in);
                                        System.out.println("Introduza o seu username: ");
                                        startTimer();
                                        n_cc = keyboard_scanner.nextLine();
                                        tt.stop();
                                        System.out.println("Introduza a sua password: ");
                                        startTimer();
                                        password = keyboard_scanner.nextLine();
                                        tt.stop();

                                        login_c.sendOperation("type|login_request;term|" + getName() + ";n_cc|" + n_cc + ";passowrd|" + password);

                                    }
                                }
                            }

                        } catch (IOException e) {
                            e.printStackTrace();
                        } finally {
                            if(login_socket != null) login_socket.close();
                        }

                        ready = !ready;
                    }
                }
            }

        } catch(IOException e){
                e.printStackTrace();
        } finally{
            if(socket != null) socket.close();
        }
    }
}


class TimerThread extends Thread {
    private long TIMEOUT = 15000;
    private VotingTerminal vt;

    public TimerThread(VotingTerminal vt) {
        super();
        this.vt = vt;
    }

    public void run() {
        // É iniciada antes de uma espera de input do utilizador
        try {
            // Se o input for recebido antes do sleep acabar, a thread principal para esta thread
            Thread.sleep(TIMEOUT);
            System.out.println("Terminal bloqueado por inatividade.");
            System.out.println("Dirija-se outra vez à mesa de voto.");
            vt.stop();
            // Para a thread principal se não receber input durante TIMEOUT segundos
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}