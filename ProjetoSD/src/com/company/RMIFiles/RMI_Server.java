package com.company.RMIFiles;

import com.company.Candidato;
import com.company.Eleicao;
import com.company.MulticastServerInterface;
import com.company.Pessoa;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.sql.ResultSet;
import java.sql.SQLClientInfoException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;

public class RMI_Server extends UnicastRemoteObject implements RMInterface {
	static AdminConsoleInterface client;
	static MulticastServerInterface mClient;


	public RMI_Server() throws RemoteException {
		super();
	}

	@Override
	public void subscribe(AdminConsoleInterface c) throws RemoteException {
		client = c;
	}

	@Override
	public void subscribeMulticast(MulticastServerInterface c) throws RemoteException {
		mClient = c;
		mClient.print_on_client("Olá do server");
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
		db.InsertElectionCandidatos(e);

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
	public Pessoa findPessoa(String num_cc) throws RemoteException, SQLException {
		//Procurar a pessoa na DB
		PostgreSQLJDBC db = new PostgreSQLJDBC();
		db.connectDB();
		ResultSet rs = db.findPessoa(num_cc);

		boolean val = rs.next();
		String[] atributosPessoa = new String[8];
		Pessoa p = null;

		if (val == false) return null;
		else{
			while (val){
				atributosPessoa[0] = rs.getString("num_cc");
				atributosPessoa[1] = rs.getString("nome");
				atributosPessoa[2] = rs.getString("password");
				atributosPessoa[3] = rs.getString("funcao");
				atributosPessoa[4] = rs.getString("departamento");
				atributosPessoa[5] = String.valueOf(rs.getInt("num_telefone"));
				atributosPessoa[6] = rs.getString("morada");
				atributosPessoa[7] = String.valueOf(rs.getTimestamp("data_validade"));
				val = rs.next();

				p = new Pessoa(atributosPessoa[1], atributosPessoa[2], atributosPessoa[3], atributosPessoa[4], Integer.parseInt(atributosPessoa[5]), atributosPessoa[6], atributosPessoa[0], atributosPessoa[7]);

			}
		}

		/*for (int i = 0; i < atributosPessoa.length; i++){
			System.out.println("-> " + atributosPessoa[i]);
		}*/

		return p;
	}

	public CopyOnWriteArrayList<Eleicao> getEleicao(String departamento) throws RemoteException, SQLException{
		//Procurar a eleicao na DB
		CopyOnWriteArrayList<Eleicao> listaEleicao = new CopyOnWriteArrayList<>();
		PostgreSQLJDBC db = new PostgreSQLJDBC();
		db.connectDB();
		ResultSet rs = db.getEleicao(departamento);
		boolean val = rs.next();
		String[] atributosEleicao = new String[8];

		if (val == false) return null;
		else{

			while (val){
				atributosEleicao[0] = rs.getString("id");
				atributosEleicao[1] = rs.getString("data_inicio");
				atributosEleicao[2] = rs.getString("data_fim");
				atributosEleicao[3] = rs.getString("titulo");
				atributosEleicao[4] = rs.getString("descricao");
				atributosEleicao[5] = rs.getString("tipo");
				atributosEleicao[6] = rs.getString("departamento");
				atributosEleicao[7] = String.valueOf(rs.getInt("resultado"));


				Eleicao e = new Eleicao(atributosEleicao[1], atributosEleicao[2],atributosEleicao[3], atributosEleicao[4], atributosEleicao[5],atributosEleicao[6],Integer.parseInt(atributosEleicao[7]));
				listaEleicao.add(e);
				val = rs.next();
			}
		}

		for (int i = 0; i < listaEleicao.size(); i++){
			System.out.println("-> " + listaEleicao.get(i).getTitulo());
		}

	return listaEleicao;
	}

	public CopyOnWriteArrayList<Candidato> getListaCandidatos(int eleicaoID) throws RemoteException, SQLException{
		//Retorna os candidatos de uma eleicao
		CopyOnWriteArrayList<Candidato> listaCandidatos = new CopyOnWriteArrayList<>();
		PostgreSQLJDBC db = new PostgreSQLJDBC();
		db.connectDB();

		System.out.println("Eleição Id: " + eleicaoID);
		ResultSet rs = db.getListaCandidatos(eleicaoID);
		boolean val = rs.next();
		String[] atributosListaCandidatos = new String[4];

		System.out.println("VAL: " + val);
		if (val == false) return null;
		else{
			while (val){
				atributosListaCandidatos[0] = rs.getString("id");
				atributosListaCandidatos[1] = rs.getString("nomecandidato");
				atributosListaCandidatos[2] = rs.getString("categoria");
				atributosListaCandidatos[3] = rs.getString("eleicao_id");

				Candidato c = new Candidato(atributosListaCandidatos[1], atributosListaCandidatos[2], null);
				listaCandidatos.add(c);
				val = rs.next();
			}
		}

		for (int i = 0; i < listaCandidatos.size(); i++){
			System.out.println("-> " + listaCandidatos.get(i).getNome());
		}

		return listaCandidatos;
	}

	public int getIdVoto() throws RemoteException, SQLException {
		//Vê o qual o ID de voto que é a PK da tabela de voto
		PostgreSQLJDBC db = new PostgreSQLJDBC();
		db.connectDB();
		int idVoto = db.getIdVoto();

		return idVoto;
	}

	public int getMaxCandidato() throws RemoteException, SQLException {
		//Vê o qual o nºo max de candidatos
		PostgreSQLJDBC db = new PostgreSQLJDBC();
		db.connectDB();
		int maxCandidato = db.getMaxCandidato();

		return maxCandidato;
	}


	public int getIdEleicao(String nomeEleicao) throws RemoteException, SQLException {
		//Vê o qual o ID da eleicao dado um nome
		PostgreSQLJDBC db = new PostgreSQLJDBC();
		db.connectDB();
		int idEleicao = db.getIdEleicao(nomeEleicao);

		return idEleicao;
	}

	@Override
	public void recebeLocalVoto(String local, String num_cc, String nomeEleicao)throws RemoteException, SQLException{
			int idEleicao = getIdEleicao(nomeEleicao);
			int idVoto = getIdVoto();
			PostgreSQLJDBC db = new PostgreSQLJDBC();
			db.connectDB();
			db.criaVoto(idVoto + 1, local, idEleicao, num_cc);
	}

	@Override
	public void updateVotoPessoaData(Timestamp dataVoto, String num_cc, String nomeEleicao) throws RemoteException, SQLException{
		int idEleicao = getIdEleicao(nomeEleicao);
		PostgreSQLJDBC db = new PostgreSQLJDBC();
		db.connectDB();
		db.updateVotoPessoaData(dataVoto, num_cc, idEleicao);
	}


	@Override
	public Eleicao getEleicaoByID(int opcaoEleicao) throws RemoteException, SQLException {
		//Procurar a eleicao na DB
		PostgreSQLJDBC db = new PostgreSQLJDBC();
		db.connectDB();
		ResultSet rs = db.getEleicaoByID(opcaoEleicao);

		boolean val = rs.next();
		String[] atributosEleicao = new String[8];
		Eleicao e = null;

		if (val == false) return null;
		else{

			while (val){
				atributosEleicao[0] = rs.getString("id");
				atributosEleicao[1] = rs.getString("data_inicio");
				atributosEleicao[2] = rs.getString("data_fim");
				atributosEleicao[3] = rs.getString("titulo");
				atributosEleicao[4] = rs.getString("descricao");
				atributosEleicao[5] = rs.getString("tipo");
				atributosEleicao[6] = rs.getString("departamento");
				atributosEleicao[7] = String.valueOf(rs.getInt("resultado"));


				e = new Eleicao(atributosEleicao[1], atributosEleicao[2],atributosEleicao[3], atributosEleicao[4], atributosEleicao[5],atributosEleicao[6] ,Integer.parseInt(atributosEleicao[7]));
				val = rs.next();
			}
		}

		return e;
	}

	@Override
	public void criaNovoCandidato(int idCandidato, Candidato c, int opcaoEleicao) throws RemoteException, SQLException {
		//Inserir na tabela dos candidatos o novo candidato
		PostgreSQLJDBC db = new PostgreSQLJDBC();
		db.connectDB();
		db.criaNovoCandidato(idCandidato, c, opcaoEleicao);
		System.out.println("Candidato criado com sucesso");
	}

	@Override
	public void getlocalVotoEleitores(int opcaoEleicao) throws RemoteException, SQLException {
		//Listar todos os votos de cada eleitor de uma certa eleicao
		PostgreSQLJDBC db = new PostgreSQLJDBC();
		db.connectDB();
		ResultSet rs = db.getlocalVotoEleitores(opcaoEleicao);

		//SELECT local_voto, hora_voto, nome, num_cc

		String num_cc, nome, local_voto, hora_voto;

		while (rs.next()){
			local_voto = rs.getString(1);
			hora_voto = String.valueOf(rs.getTimestamp(2));
			nome = rs.getString(3);
			num_cc = rs.getString(4);

			client.displaylocalVotoEleitores(local_voto, hora_voto, nome, num_cc);
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
			Registry r = LocateRegistry.createRegistry(7000);
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