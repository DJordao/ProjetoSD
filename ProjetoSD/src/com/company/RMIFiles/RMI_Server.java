package com.company.RMIFiles;

import com.company.Eleicao;
import com.company.Pessoa;

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
	public String ListaCandidaturas(int opcaoEleicao) throws SQLException, RemoteException {
		PostgreSQLJDBC db = new PostgreSQLJDBC();
		db.connectDB();
		String listaCandidatosArray = "";

		ResultSet rs = db.listaCandidaturas(opcaoEleicao); //Retorna a lista de candidaturas
		int id, i = 0;
		String nomeCandidato, categoria, numEleicao, titulo;

		while (rs.next()){
			id = rs.getInt(1);
			nomeCandidato = rs.getString("nomecandidato");
			categoria = rs.getString("categoria");
			numEleicao = rs.getString("eleicao_id");
			titulo = rs.getString("titulo");

			//1=Partido Chega-2=PS
			listaCandidatosArray += id;
			listaCandidatosArray += "=";
			listaCandidatosArray += nomeCandidato;
			listaCandidatosArray += "-";

			client.displayCandidatura(String.valueOf(id), nomeCandidato, categoria, numEleicao, titulo);

		}
		return listaCandidatosArray;
	}

	@Override
	public String ListaPessoasParaCandidatura(int opcaoEleicao) throws SQLException, RemoteException {
		PostgreSQLJDBC db = new PostgreSQLJDBC();
		db.connectDB();
		String numCC_pessoas = "";

		ResultSet rs = db.listaPessoasParaCandidatura(opcaoEleicao); //Retorna a lista de pessoas que podem ser adicionadas a uma certa candidatura

		String nomeCandidato, num_cc;

		while (rs.next()){
			num_cc = rs.getString(1);
			nomeCandidato = rs.getString("nome");
			numCC_pessoas += num_cc;
			numCC_pessoas += "-";

			client.displayListaPessoasParaCandidatura(num_cc, nomeCandidato);
		}
		return numCC_pessoas;
	}


	@Override
	public void AdicionaPessoaCandidatura(int opcaoEleicao, String num_cc, String partido, String idPartido) throws SQLException, RemoteException {
		//Inserir na tabela das lista pessoas candidatos a pessoa recebida
		PostgreSQLJDBC db = new PostgreSQLJDBC();
		db.connectDB();
		db.InsertPessoasCandidatura(opcaoEleicao, num_cc, partido,Integer.parseInt(idPartido));
	}

	@Override
	public String ListaElementosCandidatura(int opcaoEleicao, String candidatura, String idPartido) throws RemoteException, SQLException {
		//Listar todas as pessoas de uma determinada candidatura
		PostgreSQLJDBC db = new PostgreSQLJDBC();
		db.connectDB();
		ResultSet rs = db.ListaElementosCandidatura(opcaoEleicao, candidatura, Integer.parseInt(idPartido));

		String dadosElementosCandidatura = "";

		String num_cc, nome, nomeCandidato;
		while (rs.next()){
			num_cc = rs.getString(1);
			nome = rs.getString(2);
			nomeCandidato = rs.getString(3);

			dadosElementosCandidatura+= num_cc;
			dadosElementosCandidatura += " ";

			client.displayListaElementosCandidatura(num_cc, nome, nomeCandidato);
		}
		return dadosElementosCandidatura;
	}

	@Override
	public void RemovePessoaCandidatura(String num_cc, String nomeLista) throws RemoteException, SQLException {
		//Remove na tabela das lista dos candidatos a pessoa recebida
		PostgreSQLJDBC db = new PostgreSQLJDBC();
		db.connectDB();
		db.RemovePessoaCandidatura(num_cc, nomeLista);
		client.print_on_client("Pessoa removida com sucesso");
	}

	@Override
	public void ListaTudoEleicao(int opcaoEleicao) throws RemoteException, SQLException {
		//Listar todas as Candidaturas de uma determinada eleicao
		PostgreSQLJDBC db = new PostgreSQLJDBC();
		db.connectDB();
		ResultSet rs = db.listaTudoEleicao(opcaoEleicao);

		String num_cc, nome, nomeCandidato;
		while (rs.next()){
			num_cc = rs.getString(1);
			nome = rs.getString(2);
			nomeCandidato = rs.getString(4);

			client.displayListaTudoEleicao(num_cc, nome, nomeCandidato);
		}
	}

	@Override
	public String getDetalhesEleicao(int opcaoEleicao) throws RemoteException, SQLException{
		//Obter propriedades de uma eleicao para as poder alterar
		PostgreSQLJDBC db = new PostgreSQLJDBC();
		db.connectDB();
		String detalhes = "";

		ResultSet rs = db.getDetalhesEleicao(opcaoEleicao);

		String titulo, descricao;
		Timestamp data_inicio, data_fim;
		while (rs.next()){
			titulo = rs.getString("titulo");
			descricao = rs.getString("descricao");
			data_inicio = rs.getTimestamp("data_inicio");
			data_fim = rs.getTimestamp("data_fim");

			detalhes = titulo + "#" + descricao + "#" + data_inicio + "#" + data_fim;
			client.displayDetalhesEleicao(titulo, descricao, data_inicio, data_fim);
		}
		return detalhes;
	}

	@Override
	public void UpdatePropriedadesEleicao(int opcaoEleicao, String tituloAlteracao, String descricaoAlteracao, Timestamp data_inicio, Timestamp data_fim) throws RemoteException, SQLException {
		//DAr update às propriedades de uma eleicao
		PostgreSQLJDBC db = new PostgreSQLJDBC();
		db.connectDB();
		db.UpdatePropriedadesEleicao(opcaoEleicao, tituloAlteracao, descricaoAlteracao,data_inicio, data_fim);
		client.print_on_client("Update com sucesso");
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