package com.company.RMIFiles;

import com.company.Eleicao;
import com.company.Message;
import com.company.Pessoa;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RMInterface extends Remote {

    Pessoa registaPessoa(Pessoa p) throws RemoteException;

    Eleicao criaEleicao(Eleicao e) throws RemoteException;

     void tableAndTerminalState() throws RemoteException;

     void getStatsEleicao(Eleicao e) throws RemoteException;

    void encerraEleicao(Eleicao e) throws RemoteException;

    void listaResultadosAnteriores(Eleicao e) throws RemoteException;

}
