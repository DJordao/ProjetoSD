package com.company.RMIFiles;

import com.company.Eleicao;
import com.company.Message;
import com.company.Pessoa;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class RMI_Server extends UnicastRemoteObject implements RMInterface {
	static AdminConsoleInterface client;


	public RMI_Server() throws RemoteException {
		super();
	}

	@Override
	public Pessoa registaPessoa(Pessoa p) throws RemoteException {
		client.print_on_client("Server: Registo feito com sucesso");
		return null;
	}

	@Override
	public Eleicao criaEleicao(Eleicao e) throws RemoteException {
		return null;
	}

	@Override
	public void tableAndTerminalState() throws RemoteException {

	}

	@Override
	public void getStatsEleicao(Eleicao e) throws RemoteException {

	}

	@Override
	public void encerraEleicao(Eleicao e) throws RemoteException {

	}

	@Override
	public void listaResultadosAnteriores(Eleicao e) throws RemoteException {

	}


	// =======================================================

	public static void main(String args[]) {

		try {
			RMI_Server h = new RMI_Server();
			Naming.rebind("hello", h);
			System.out.println("Hello Server ready.");
		} catch (RemoteException re) {
			System.out.println("Exception in HelloImpl.main: " + re);
		} catch (MalformedURLException e) {
			System.out.println("MalformedURLException in HelloImpl.main: " + e);
		}

	}


}