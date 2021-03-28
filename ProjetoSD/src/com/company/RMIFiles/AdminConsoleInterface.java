package com.company.RMIFiles;

import java.rmi.*;

public interface AdminConsoleInterface extends Remote{
    public void print_on_client(String s) throws RemoteException;
    public void displayEleicoes(String id, String titulo, String tipo, String departamento, String data_inicio) throws RemoteException;

    void displayCandidatura(String id, String nomeCandidato, String categoria, String numEleicao, String titulo) throws RemoteException;

    void displayListaPessoasParaCandidatura(String num_cc, String nomeCandidato) throws RemoteException;

    void displayListaElementosCandidatura(String num_cc, String nome, String nomeCandidato) throws RemoteException;

    void displayListaTudoEleicao(String num_cc, String nome, String nomeCandidato) throws RemoteException;
}
