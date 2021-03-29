package com.company;

import com.company.RMIFiles.RMInterface;

import java.io.IOException;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.CopyOnWriteArrayList;

public class MulticastServer extends Thread implements MulticastServerInterface{
    private String MULTICAST_ADDRESS_TERM = "224.3.2.1";
    private int PORT = 4321;

    public static class MulticastServerRMI extends UnicastRemoteObject implements MulticastServerInterface{

        protected MulticastServerRMI() throws RemoteException {
            super();
        }

        @Override
        public void print_on_client(String s) throws RemoteException {
            System.out.println(">Server: " + s);
        }
    }

    public void print_on_client(String s) throws RemoteException {
        System.out.println("> " + s);
    }

    public static void main(String[] args) throws RemoteException, NotBoundException {
        MulticastServer server = new MulticastServer(args[0]);
        server.start();

        //RMInterface h = (RMInterface) LocateRegistry.getRegistry(6000).lookup("RMIConnect");
        //MulticastServerRMI admin = new MulticastServerRMI();
        //h.subscribeMulticast(admin);
        //h.print_on_server("olá do multicast");
    }

    public MulticastServer(String department) {
        super("Mesa de voto " + department);
    }

    public void run() {
        RMInterface h = null;
        try {
             h = (RMInterface) LocateRegistry.getRegistry(6000).lookup("RMIConnect");
            MulticastServerRMI admin = new MulticastServerRMI();
            h.subscribeMulticast(admin);
            h.print_on_server("olá do multicast");
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (NotBoundException e) {
            e.printStackTrace();
        }


        boolean id = false;
        Pessoa p = null;
        MulticastSocket socket = null;

        System.out.println(this.getName() + " online...");

        try {
            socket = new MulticastSocket(PORT);  // Socket para comunicar com os terminais
            InetAddress group = InetAddress.getByName(MULTICAST_ADDRESS_TERM);
            socket.joinGroup(group);

            Communication c = new Communication(socket, group);

            LoginHandler lh = new LoginHandler();
            lh.start();

            VoteReceiver vr = new VoteReceiver(); // Thread que recebe os votos dos terminais
            vr.start();

            Scanner keyboard_scanner = new Scanner(System.in);

            while (true) {
                while (!id) { // Enquanto o ulilizador não estiver identificado
                    System.out.println("Indique o seu nª do cc:");
                    String input = keyboard_scanner.nextLine();

                    Pessoa pessoa = null;
                    String[] propriedadesPessoa = null;
                    CopyOnWriteArrayList<Eleicao>listaEleicao = new CopyOnWriteArrayList<>();

                    //TODO funcao de encontrar pressoa retorna um String[] com todas as propriedades da pessoa
                    pessoa = h.findPessoa(input);

                    //TODO retorna um array List de todas as eleições de um dado Departamento.
                    listaEleicao = h.getEleicao("FLUC");

                    //Só para ver se recebia bem a lista das eleições
                    if (listaEleicao != null){
                        for (int i = 0; i < listaEleicao.size(); i++){
                            System.out.println("-> " + listaEleicao.get(i).getTitulo());
                        }
                    }else System.out.println("Nã há eleições para esse Departamento");

                    /* Só para verificar se recebia as propriedades bem
                    System.out.println("\n\n\n");
                    for (int i = 0; i < propriedadesPessoa.length; i++){
                        System.out.println("-> " + propriedadesPessoa[i]);
                    }*/

                    // Tem que se ir buscar ao RMI
                    CopyOnWriteArrayList<Pessoa> l = new CopyOnWriteArrayList<>();
                    l.add(new Pessoa("Diogo Filipe", "1234", "Estudante", "DEI", 856475645, "Leiria", "56475643", "04/2025"));

                    //TODO: alterei este ciclo de forma a que compare com a string que recebe
                    for(int i = 0; i < l.size(); i++) {
                        p = l.get(i);
                        if (pessoa != null){
                            if(pessoa.getNum_cc().equals(input)) {
                                id = true;
                                System.out.println("Identificação bem sucedida.");
                                System.out.println("A procurar um terminal de voto...");
                                break;
                            }
                        }
                    }

                    if(!id) {
                        System.out.println("Identificação falhada.");
                    }
                }

                c.sendOperation("type|term_fetch");

                String[] message = c.receiveOperation().split(";");
                String message_type = c.getMessageType(message[0]);

                if(message_type.equals("term_ready")) {
                    String term = message[1].split("\\|")[1];
                    System.out.println("Pode votar no terminal " + term);

                    c.sendOperation("type|term_unlock;term|" + term);
                    id = false;
                }

            }

        } catch (IOException | SQLException e) {
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
        System.out.println("login_handler");
        try {
            socket = new MulticastSocket(PORT);  // Socket para receber os votos
            InetAddress group = InetAddress.getByName(MULTICAST_ADDRESS_LOGIN);
            socket.joinGroup(group);
            Communication c = new Communication(socket, group);

            while(true) {
                String[] message = c.receiveOperation().split(";");
                String message_type = c.getMessageType(message[0]);

                if(message_type.equals("login_request")) {
                    String term = message[1].split("\\|")[1];
                    String username = message[2].split("\\|")[1];
                    String password = message[3].split("\\|")[1];

                    // Tem que se ir buscar ao RMI

                    Pessoa p = new Pessoa("Diogo Filipe", "1234", "Estudante", "DEI", 856475645, "Leiria", "56475643", "04/2025");

                    if(p.getPassword().equals(username) && p.getPassword().equals(password)) {
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
            socket = new MulticastSocket(PORT);  // Socket para fazer logins
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