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
import java.sql.Timestamp;
import java.util.Scanner;
import java.util.concurrent.CopyOnWriteArrayList;

public class MulticastServer extends Thread{
    private String MULTICAST_ADDRESS_TERM = "224.3.2.1";
    private int PORT = 4321;

    public static void main(String[] args) throws RemoteException, NotBoundException {
        MulticastServer server = new MulticastServer(args[0]);
        server.start();
    }

    public MulticastServer(String department) {
        super(department);
    }

    public void run() {
        RMInterface h = null;
        try {
            h = (RMInterface) LocateRegistry.getRegistry(7000).lookup("RMIConnect");
            h.print_on_server("olá do multicast");
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (NotBoundException e) {
            e.printStackTrace();
        }


        boolean id = false;
        MulticastSocket socket = null;

        System.out.println(this.getName() + " online...");

        try {
            socket = new MulticastSocket(PORT);  // Socket para comunicar com os terminais
            InetAddress group = InetAddress.getByName(MULTICAST_ADDRESS_TERM);
            socket.joinGroup(group);

            Communication c = new Communication(socket, group);

            LoginHandler lh = new LoginHandler(h); // Thread que trata dos logins
            lh.start();

            VoteReceiver vr = new VoteReceiver(h); // Thread que recebe os votos dos terminais
            vr.start();

            Scanner keyboard_scanner = new Scanner(System.in);
            Scanner keyboard_scanner1 = new Scanner(System.in);

            Pessoa p = null;
            Eleicao e = null;
            int idEleicao = 0;
            while (true) {
                while (!id) { // Enquanto o ulilizador não estiver identificado
                    System.out.println("Indique o seu nª do cc:");
                    String input = keyboard_scanner.nextLine();

                    System.out.println("->" + input + "<-");

                    p = h.findPessoa(input);

                    // Tem que se ir buscar ao RMI
                    //CopyOnWriteArrayList<Pessoa> l = new CopyOnWriteArrayList<>();
                    //l.add(new Pessoa("Diogo Filipe", "1234", "Estudante", "DEI", 856475645, "Leiria", "56475643", "04/2025"));

                    //TODO: alterei este ciclo de forma a que compare com a string que recebe
                    if (p != null){
                        if(p.getNum_cc().equals(input)) {
                            id = true;
                            System.out.println("Identificação bem sucedida.");

                            CopyOnWriteArrayList<Eleicao> listaEleicao = h.getEleicao(getName());
                            if (listaEleicao != null){

                                for (int i = 0; i < listaEleicao.size(); i++){
                                    System.out.println((i+1) + "-> " + listaEleicao.get(i).getTitulo());
                                }
                            }
                            else {
                                System.out.println("Não existem eleições a decorrer para este Departamento.");
                                break;
                            }

                            int i;
                            e = null;
                            while (e == null) {
                                System.out.println("Escolha uma eleição para votar: ");
                                i = keyboard_scanner1.nextInt();

                                e = listaEleicao.get(i-1);
                                idEleicao = i;
                                if(e == null) {
                                    System.out.println("Opção inválida.");;
                                }
                                // TODO Verificar se a pessoa já votou nessa eleição
                            }

                            System.out.println("A procurar um terminal de voto...");

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

                    c.sendOperation("type|term_unlock;term|" + term + ";user|" + p.getNum_cc());

                    //Teste
                    CopyOnWriteArrayList<Candidato> listaCandidatos = h.getListaCandidatos(idEleicao);
                    e.setListaCandidatos(listaCandidatos);
                    CopyOnWriteArrayList<Candidato> l = e.getListaCandidatos();
                    /*
                    //Confirmar que recebe a lista dos candidatos
                    for (int i = 0; i < l.size(); i++){
                        System.out.println("L-> " + l.get(i).getNome());
                    }*/

                    h.recebeLocalVoto(getName(), p.getNum_cc(), e.getTitulo()); // Envia para o RMI o local e a eleição em que x pessoa vai votar

                    String election = "type|send_elec;elec_name|" + e.getTitulo() + ";item_count|" + l.size();

                    for(int i = 0; i < l.size(); i++) {
                        election += ";item_" + i + "|" + l.get(i).getNome();
                    }

                    c.sendOperation(election);

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


class LoginHandler extends Thread{
    private String MULTICAST_ADDRESS_LOGIN = "224.3.2.2";
    private int PORT = 4321;
    private RMInterface h;

    public LoginHandler(RMInterface h) {
        super();
        this.h = h;
    }

    public void run() {
        MulticastSocket socket = null;
        System.out.println("login_handler");
        try {
            socket = new MulticastSocket(PORT);  // Socket para tratar dos logins
            InetAddress group = InetAddress.getByName(MULTICAST_ADDRESS_LOGIN);
            socket.joinGroup(group);
            Communication c = new Communication(socket, group);

            while(true) {
                String[] message = c.receiveOperation().split(";");
                String message_type = c.getMessageType(message[0]);

                if(message_type.equals("login_request")) {
                    String term = message[1].split("\\|")[1];
                    String n_cc = message[2].split("\\|")[1];
                    String password = message[3].split("\\|")[1];

                    Pessoa p = h.findPessoa(n_cc);

                    if(p.getPassword().equals(password)) {
                        c.sendOperation("type|login_accept;term|" + term);
                    }
                    else {
                        c.sendOperation("type|login_deny;term|" + term);
                    }
                }
                else if(message_type.equals("user_voted")) {
                    String elec_name = message[1].split("\\|")[1];
                    String n_cc = message[2].split("\\|")[1];
                    Timestamp cur_date = new Timestamp(System.currentTimeMillis());

                    h.updateVotoPessoaData(cur_date, n_cc, elec_name); // Depois da pessoa votar envia a data
                }
            }

        } catch (IOException | SQLException e) {
            e.printStackTrace();
        } finally {
            socket.close();
        }
    }
}


class VoteReceiver extends Thread{
    private String MULTICAST_ADDRESS_VOTE = "224.3.2.3";
    private int PORT = 4321;
    private RMInterface h;

    public VoteReceiver(RMInterface h) {
        super();
        this.h = h;
    }

    public void run() {
        MulticastSocket socket = null;

        try {
            socket = new MulticastSocket(PORT);  // Socket para receber os votos
            InetAddress group = InetAddress.getByName(MULTICAST_ADDRESS_VOTE);
            socket.joinGroup(group);
            Communication c = new Communication(socket, group);

            while (true) {
                String[] message = c.receiveOperation().split(";");
                String message_type = c.getMessageType(message[0]);
                if(message_type.equals("send_vote")){
                    String elec_name = message[1].split("\\|")[1];
                    String list_name = message[2].split("\\|")[1];
                    h.recebeVoto(list_name, elec_name);
                }

            }
        } catch (IOException | SQLException e) {
            e.printStackTrace();
        } finally {
            socket.close();
        }
    }
}