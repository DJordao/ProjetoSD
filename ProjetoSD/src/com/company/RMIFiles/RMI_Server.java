package com.company.RMIFiles;

import com.company.Eleicao;
import com.company.Message;
import com.company.Pessoa;

import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.Naming;
import java.rmi.RMISecurityManager;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.sql.ResultSet;
import java.sql.SQLClientInfoException;
import java.sql.SQLException;
import java.sql.Timestamp;

public class RMI_Server extends UnicastRemoteObject implements RMInterface {
	static AdminConsoleInterface client;


	public RMI_Server() throws RemoteException {
		super();
	}

	@Override
	public void subscribe(AdminConsoleInterface c) throws RemoteException {
		client = c;
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
	public Eleicao criaEleicao(Eleicao e) throws RemoteException, SQLClientInfoException {
		//Inserir na tabela das eleições a eleiçao recebida
		PostgreSQLJDBC db = new PostgreSQLJDBC();
		db.connectDB();
		db.InsertElection(e);

		client.print_on_client("Server: Eleição criada com sucesso");
		return e;
	}

	@Override
	public void ListaEleicoes() throws RemoteException, SQLException {
		//Lista todas as eleicoes a decorrer
		PostgreSQLJDBC db = new PostgreSQLJDBC();
		db.connectDB();
		ResultSet rs = db.listaEleicoes(); //Retorna a lista de eleições
		int id;
		String titulo, tipo, departamento;
		Timestamp data_inicio;

		while (rs.next()){
                id = rs.getInt(1);
                titulo = rs.getString("titulo");
                tipo = rs.getString("tipo");
                departamento = rs.getString("departamento");
                data_inicio = rs.getTimestamp("data_inicio");

                client.displayEleicoes(String.valueOf(id), titulo, tipo, departamento, data_inicio.toString());
		}



	}

	@Override
	public int maxEleicoes() throws RemoteException, SQLException {
		//Vê o número max de eleicoes que existe
		PostgreSQLJDBC db = new PostgreSQLJDBC();
		db.connectDB();
		int max = db.maxEleicoes();

		return max;
	}

	@Override
	public void ListaCandidaturas(int opcaoEleicao) throws RemoteException,SQLException {
		PostgreSQLJDBC db = new PostgreSQLJDBC();
		db.connectDB();

		ResultSet rs = db.listaCandidaturas(opcaoEleicao); //Retorna a lista de candidaturas
		int id;
		String nomeCandidato, categoria, numEleicao;

		while (rs.next()){
			id = rs.getInt(1);
			nomeCandidato = rs.getString("nomecandidato");
			categoria = rs.getString("categoria");
			numEleicao = rs.getString("eleicao_id");

			client.displayCandidatura(String.valueOf(id), nomeCandidato, categoria, numEleicao);
		}
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

			RMI_Server h = new RMI_Server();
			Registry r = LocateRegistry.createRegistry(6000);
			r.rebind("RMIConnect", h);

			System.out.println("Hello Server ready.");


			PostgreSQLJDBC db = new PostgreSQLJDBC();
			db.connectDB();



		} catch (RemoteException re) {
			System.out.println("Exception in HelloImpl.main: " + re);
		} catch (SQLClientInfoException throwables) {
			throwables.printStackTrace();
		}

	}


}