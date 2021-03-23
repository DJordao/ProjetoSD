package com.company.RMIFiles;

import com.company.Eleicao;
import com.company.Message;
import com.company.Pessoa;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.SQLClientInfoException;

public class RMI_Server extends UnicastRemoteObject implements RMInterface {
	static AdminConsoleInterface client;


	public RMI_Server() throws RemoteException {
		super();
	}

	@Override
	public void subscribe(AdminConsole c) throws RemoteException {
		client = c;
		System.out.println("Entrei");
	}

	@Override
	public Pessoa registaPessoa(Pessoa p) throws RemoteException, SQLClientInfoException {
		//Inserir na tabela das pessoas a pessoa recebida
		PostgreSQLJDBC db = new PostgreSQLJDBC();
		db.connectDB();
		db.InsertPessoas(p);

		client.print_on_client("Server: Registo feito com sucesso");
		return p;
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

	@Override
	public void print_on_server(String s) throws RemoteException {
		System.out.println("> " + s);
	}


	// =======================================================

	public static void main(String args[]) {

		try {
			System.out.println("Hello Server ready.");

			RMI_Server h = new RMI_Server();
			Naming.rebind("RMIConnect", h);



			PostgreSQLJDBC db = new PostgreSQLJDBC();
			db.connectDB();



		} catch (RemoteException re) {
			System.out.println("Exception in HelloImpl.main: " + re);
		} catch (MalformedURLException e) {
			System.out.println("MalformedURLException in HelloImpl.main: " + e);
		} catch (SQLClientInfoException throwables) {
			throwables.printStackTrace();
		}

	}


}