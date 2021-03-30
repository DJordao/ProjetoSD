package com.company.RMIFiles;

import com.company.Candidato;
import com.company.Eleicao;
import com.company.MulticastServerInterface;
import com.company.Pessoa;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.sql.SQLClientInfoException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;

public interface RMInterface extends Remote {

    Pessoa registaPessoa(Pessoa p) throws RemoteException, SQLClientInfoException;

    Eleicao criaEleicao(Eleicao e) throws RemoteException, SQLClientInfoException;

     void tableAndTerminalState() throws RemoteException;

     void getStatsEleicao(Eleicao e) throws RemoteException;

    void encerraEleicao(Eleicao e) throws RemoteException;

    void listaResultadosAnteriores(Eleicao e) throws RemoteException;

    void print_on_server(String s) throws RemoteException;

    void subscribe(AdminConsoleInterface c) throws RemoteException;

    void subscribeMulticast(MulticastServerInterface c) throws RemoteException;

    void ListaEleicoes() throws RemoteException, SQLException;

    int maxEleicoes() throws RemoteException, SQLException;

    String ListaCandidaturas(int opcaoEleicao) throws RemoteException, SQLException;

    String ListaPessoasParaCandidatura(int opcaoEleicao) throws RemoteException, SQLException;

    void AdicionaPessoaCandidatura(int opcaoEleicao, String num_cc, String partido, String idPartido) throws RemoteException, SQLException;

    String ListaElementosCandidatura(int opcaoEleicao, String candidatura, String idPartido) throws RemoteException, SQLException;

    void RemovePessoaCandidatura(String num_cc, String nomeLista) throws RemoteException, SQLException;

    void ListaTudoEleicao(int opcaoEleicao) throws RemoteException, SQLException;

    String getDetalhesEleicao(int opcaoEleicao) throws RemoteException, SQLException;

    void UpdatePropriedadesEleicao(int opcaoEleicao, String tituloAlteracao, String descricaoAlteracao, Timestamp data_inicio, Timestamp data_fim) throws RemoteException, SQLException;

    Pessoa findPessoa(String num_cc)  throws RemoteException, SQLException;

    CopyOnWriteArrayList<Eleicao> getEleicao(String departamento) throws RemoteException, SQLException;

    CopyOnWriteArrayList<Candidato> getListaCandidatos(int eleicaoID) throws RemoteException, SQLException;
}
