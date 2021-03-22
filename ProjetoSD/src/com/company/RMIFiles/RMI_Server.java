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


	// RMI FICHA 3
	public String sayHello() throws RemoteException {
		System.out.println("Printing on server...");
		return "ACK";
	}

	public void remote_print(String s) throws RemoteException {
		System.out.println("Server:" + s);
	}

	public void remote_print(Message m) throws RemoteException {
		System.out.println("Server:" + m);
	}

	public void publish(Package p) throws RemoteException{
		System.out.println(p.toString());

	}
	public Message ping_pong(Message m) throws RemoteException {
		Message m1 = new Message("");
		m1.text = m.text + "....";
		return m1;
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