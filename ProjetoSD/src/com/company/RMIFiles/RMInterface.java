package com.company.RMIFiles;

import com.company.Eleicao;
import com.company.Pessoa;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.sql.SQLClientInfoException;
import java.sql.SQLException;

public interface RMInterface extends Remote {

    Pessoa registaPessoa(Pessoa p) throws RemoteException, SQLClientInfoException;

    Eleicao criaEleicao(Eleicao e) throws RemoteException, SQLClientInfoException;

     void tableAndTerminalState() throws RemoteException;

     void getStatsEleicao(Eleicao e) throws RemoteException;

    void encerraEleicao(Eleicao e) throws RemoteException;

    void listaResultadosAnteriores(Eleicao e) throws RemoteException;

    void print_on_server(String s) throws RemoteException;

    void subscribe(AdminConsoleInterface c) throws RemoteException;

    void ListaEleicoes() throws RemoteException, SQLException;

    int maxEleicoes() throws RemoteException, SQLException;

    void ListaCandidaturas(int opcaoEleicao) throws SQLException, RemoteException;
}
