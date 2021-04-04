package com.company;

import com.company.RMIFiles.RMInterface;

import java.io.IOException;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.rmi.ConnectException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.CopyOnWriteArrayList;

public class MulticastServer extends Thread{
    private final String MULTICAST_ADDRESS_TERM = "224.3.2.1";
    private final int PORT = 4321;
    private RMInterface h;
    private LoginHandler lh;
    private VoteReceiver vr;


    public static void main(String[] args) {
        MulticastServer server = new MulticastServer(args[0]);
        server.start();
    }


    public MulticastServer(String department) {
        super(department);
    }


    public int changeRMI() {
        try {
            h = (RMInterface) LocateRegistry.getRegistry(7000).lookup("RMIConnect");
            h.print_on_server("olá do multicast");
            lh.changeRMI(h);
            vr.changeRMI(h);
            //RMIChecker rc = new RMIChecker(this, h);
            //rc.start();
            System.out.println("Liguei-me ao secundário");
            return 1;
        } catch (RemoteException | NotBoundException e) {
            //e.printStackTrace();
            return -1;
        }
    }


    private ArrayList<Eleicao> filterVotedElections(Pessoa p, ArrayList<Eleicao> aux_e) throws RemoteException, SQLException {
        CopyOnWriteArrayList<Voto> v = null;
        while (true) {
            try {
                v = h.getListaVotos();
                break;
            } catch (ConnectException e) {
                changeRMI();
            }
        }
        ArrayList<Eleicao> l = new ArrayList<>();

        if(v != null) {
            ArrayList<Voto> aux_v = new ArrayList<>();

            for (Voto voto : v) {
                if (voto.getNum_cc().equals(p.getNum_cc()) && voto.getHoraVoto() != null) {
                    aux_v.add(voto);
                }
            }

            int check = 0;
            for (Eleicao eleicao : aux_e) {
                for (Voto voto : aux_v) {
                    int e_id = Integer.parseInt(voto.getEleicaoID());

                    Eleicao e = null;
                    while (true) {
                        try {
                            e = h.getEleicaoByID(e_id);
                            break;
                        } catch (ConnectException ce) {
                            changeRMI();
                        }
                    }

                    if (e.getTitulo().equals(eleicao.getTitulo())) {
                        check = 1;
                        break;
                    }
                }

                if (check == 0) {
                    l.add(eleicao);
                }
                check = 0;
            }
        }
        else {
            l = aux_e;
        }

        return l;
    }


    private ArrayList<Eleicao> filterElectionsByRole(Pessoa p) throws RemoteException, SQLException {
        CopyOnWriteArrayList<Eleicao> listaEleicao = null;

        while (true) {
            try {
                listaEleicao = h.getEleicao(getName());
                break;
            } catch (ConnectException ce) {
                changeRMI();
            }
        }

        ArrayList<Eleicao> aux_e = new ArrayList<>();

        for (Eleicao eleicao : listaEleicao) {
            if (eleicao.getTipoEleicao().equals(p.getFuncao())) {
                aux_e.add(eleicao);
            }
        }

        return aux_e;
    }


    public void run() {
        try {
            h = (RMInterface) LocateRegistry.getRegistry(7000).lookup("RMIConnect");
            h.print_on_server("olá do multicast");

            h.saveDep(this.getName(), 0);

            //RMIChecker rc = new RMIChecker(this, h);
            //rc.start();

        } catch (RemoteException | NotBoundException e) {
            e.printStackTrace();
        }
        System.out.println(this.getName() + " online...");

        boolean id = false;
        try (MulticastSocket socket = new MulticastSocket(PORT)) {
            // Socket para comunicar com os terminais
            InetAddress group = InetAddress.getByName(MULTICAST_ADDRESS_TERM);
            socket.joinGroup(group);

            Communication c = new Communication(socket, group);

            lh = new LoginHandler(h, this); // Thread que trata dos logins
            lh.start();

            vr = new VoteReceiver(h, this); // Thread que recebe os votos dos terminais
            vr.start();

            Scanner keyboard_scanner = new Scanner(System.in);
            //Scanner keyboard_scanner1 = new Scanner(System.in);

            Pessoa p = null;
            Eleicao e = null;
            int idEleicao = 0;
            while (true) {
                while (!id) { // Enquanto o ulilizador não estiver identificado
                    System.out.println("Indique o seu nª do cc:");
                    String input = keyboard_scanner.nextLine();

                    while (true) {
                        try {
                            p = h.findPessoa(input);
                            break;
                        } catch (ConnectException ce) {
                            changeRMI();
                        }
                    }

                    if (p != null) {
                        if (p.getNum_cc().equals(input)) {
                            id = true;
                            System.out.println("Identificação bem sucedida.");

                            // Filtra as eleições de outra função
                            ArrayList<Eleicao> aux_e = filterElectionsByRole(p);

                            // Filtra as eleições já votadas
                            ArrayList<Eleicao> l = filterVotedElections(p, aux_e);

                            // Lista as eleições válidas para votar
                            if (l != null) {

                                for (int i = 0; i < l.size(); i++) {
                                    System.out.println((i + 1) + "-> " + l.get(i).getTitulo());
                                }
                            } else {
                                System.out.println("Não existem eleições a decorrer neste departamento.");
                                break;
                            }

                            if (l.size() == 0) {
                                System.out.println("Não existem eleições disponíveis para votar neste departamento.");
                                break;
                            }

                            String option;
                            e = null;

                            while (e == null) {
                                System.out.println("Escolha uma eleição para votar: ");
                                int i;

                                option = keyboard_scanner.nextLine();
                                try {
                                    i = Integer.parseInt(option);

                                    if (i > 0 && i <= l.size()) {
                                        e = l.get(i - 1);
                                        idEleicao = i;
                                    } else {
                                        System.out.println("Opção inválida.");
                                    }

                                } catch (NumberFormatException ne) {
                                    System.out.println("Opção inválida.");
                                }
                            }

                            System.out.println("A procurar um terminal de voto...");
                        }
                    }

                    if (!id) {
                        System.out.println("Identificação falhada.");
                    }
                }

                c.sendOperation("type|term_fetch");

                String[] message = c.receiveOperation().split(";");
                String message_type = c.getMessageType(message[0]);

                if (message_type.equals("term_ready")) {
                    String term = message[1].split("\\|")[1];
                    System.out.println("Pode votar no terminal " + term);

                    c.sendOperation("type|term_unlock;term|" + term + ";user|" + p.getNum_cc());

                    CopyOnWriteArrayList<Candidato> listaCandidatos = null;

                    while (true) {
                        try {
                            listaCandidatos = h.getListaCandidatos(idEleicao);
                            break;
                        } catch (ConnectException ce) {
                            changeRMI();
                        }
                    }

                    e.setListaCandidatos(listaCandidatos);
                    CopyOnWriteArrayList<Candidato> l = e.getListaCandidatos();

                    while (true) {
                        try {
                            h.recebeLocalVoto(getName(), p.getNum_cc(), e.getTitulo()); // Envia para o RMI o local e a eleição em que x pessoa vai votar
                            break;
                        } catch (ConnectException ce) {
                            changeRMI();
                        }
                    }

                    String election = "type|send_elec;elec_name|" + e.getTitulo() + ";item_count|" + l.size();

                    for (int i = 0; i < l.size(); i++) {
                        election += ";item_" + i + "|" + l.get(i).getNome();
                    }

                    c.sendOperation(election);

                    id = false;
                }

            }

        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }
    }
}


class LoginHandler extends Thread{
    private final String MULTICAST_ADDRESS_LOGIN = "224.3.2.2";
    private final int PORT = 4321;
    private RMInterface h;
    private MulticastServer s;

    public LoginHandler(RMInterface h, MulticastServer s) {
        super();
        this.h = h;
        this.s = s;
    }

    public void changeRMI(RMInterface h) {
        this.h = h;
    }

    public void run() {
        try (MulticastSocket socket = new MulticastSocket(PORT)) {
            // Socket para tratar dos logins
            InetAddress group = InetAddress.getByName(MULTICAST_ADDRESS_LOGIN);
            socket.joinGroup(group);
            Communication c = new Communication(socket, group);

            while (true) {
                String[] message = c.receiveOperation().split(";");
                String message_type = c.getMessageType(message[0]);

                if (message_type.equals("login_request")) {
                    String term = message[1].split("\\|")[1];
                    String n_cc = message[2].split("\\|")[1];
                    String password = message[3].split("\\|")[1];

                    Pessoa p = null;
                    while (true) {
                        try {
                            p = h.findPessoa(n_cc);
                            break;
                        } catch (ConnectException ce) {
                            s.changeRMI();
                        }
                    }

                    if (p.getPassword().equals(password)) {
                        c.sendOperation("type|login_accept;term|" + term);
                    } else {
                        c.sendOperation("type|login_deny;term|" + term);
                    }
                } else if (message_type.equals("user_voted")) {
                    String elec_name = message[1].split("\\|")[1];
                    String n_cc = message[2].split("\\|")[1];
                    Timestamp cur_date = new Timestamp(System.currentTimeMillis());

                    while(true) {
                        try {
                            h.updateVotoPessoaData(cur_date, n_cc, elec_name); // Depois da pessoa votar envia a data
                            break;
                        } catch (ConnectException ce) {
                            s.changeRMI();
                        }
                    }
                }
            }

        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }
    }
}


class VoteReceiver extends Thread{
    private final String MULTICAST_ADDRESS_VOTE = "224.3.2.3";
    private final int PORT = 4321;
    private RMInterface h;
    private MulticastServer s;

    public VoteReceiver(RMInterface h, MulticastServer s) {
        super();
        this.h = h;
        this.s = s;
    }

    public void changeRMI(RMInterface h) {
        this.h = h;
    }

    public void run() {
        try (MulticastSocket socket = new MulticastSocket(PORT)) {
            // Socket para receber os votos
            InetAddress group = InetAddress.getByName(MULTICAST_ADDRESS_VOTE);
            socket.joinGroup(group);
            Communication c = new Communication(socket, group);

            while (true) {
                String[] message = c.receiveOperation().split(";");
                String message_type = c.getMessageType(message[0]);
                if (message_type.equals("send_vote")) {
                    String elec_name = message[1].split("\\|")[1];
                    String list_name = message[2].split("\\|")[1];

                    while(true) {
                        try {
                            h.recebeVoto(list_name, elec_name);
                            break;
                        } catch (ConnectException ce) {
                            s.changeRMI();
                        }
                    }
                }

            }
        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }
    }
}

class RMIChecker extends Thread {
    private MulticastServer s;
    private RMInterface h;

    public RMIChecker (MulticastServer s, RMInterface h) {
        super();
        this.s = s;
        this.h = h;
    }

    public void run() {
        while (true) {
            try {
                h.print_on_server("Multicast Server check");
                Thread.sleep(3000);
            } catch (RemoteException | InterruptedException e) {
                if (s.changeRMI() == 1)
                    break;
            }
        }
    }

}