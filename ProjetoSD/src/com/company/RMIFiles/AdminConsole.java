package com.company.RMIFiles;

import com.company.Candidato;
import com.company.Eleicao;
import com.company.Pessoa;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.sql.SQLClientInfoException;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.InputMismatchException;
import java.util.Scanner;

public class AdminConsole extends UnicastRemoteObject implements AdminConsoleInterface{

	AdminConsole() throws RemoteException {
		super();
	}

	public void print_on_client(String s) throws RemoteException {
		System.out.println("> " + s);
	}


	public static void displayFuncionalidade(){
		System.out.println("=========================");
		System.out.println("OPERAÇÕES A REALIZAR:");
		System.out.println("[1] -> Registar Pessoas");
		System.out.println("[2] -> Criar Eleição");
		System.out.println("[3] -> Gerir listas de candidatos a uma eleição");
		System.out.println("[4] -> Gerir mesas de voto");
		System.out.println("[5] -> Alterar propriedades de uma eleição");
		System.out.println("[6] -> Saber em que local votou cada eleitors");
		System.out.println("[7] -> Mostrar estado das mesas de voto");
		System.out.println("[8] -> Mostrar eleitores em tempo real");
		System.out.println("[9] -> Terminar uma eleição (acho que não é aqui mas fica na mm)");
		System.out.println("[10] -> Consultar resultados detalhados de eleições passadas");
		System.out.println("=========================");
	}

	public static Pessoa registaPessoa() {
		String nome;
		String password;
		String funcao;
		String departamento;
		int num_telefone;
		String morada;
		String num_cc;
		String data_validade_cc;

		Scanner input;

		//nome
			while (true){
				System.out.printf("Nome: ");
				input = new Scanner(System.in);
				nome = input.nextLine();
				try {

					if (String.valueOf(nome).length() != 0 && !nome.isBlank()) {
						break;
					} else {
						System.out.println("Insira o seu nome");
					}

				}catch (InputMismatchException e){
					System.out.println("Insira o seu nome");
				}

			}

			//password
			while (true){
				System.out.printf("Password: ");
				input = new Scanner(System.in);
				password = input.nextLine();

				try {
					if (String.valueOf(password).length() != 0 && !password.isBlank()) {
						break;
					} else {
						System.out.println("Insira a sua password");
					}

				}catch (InputMismatchException e){
					System.out.println("Insira a sua password");
				}
			}

			//funcao
			while (true){
				try {
					System.out.printf("Funcao: ");
					//Estudante, Docente, Func
					input = new Scanner(System.in);
					funcao = input.nextLine();

					//if(!funcao.matches(".*\\d.*") && funcao.matches("[a-zA-Z]+")){ //O input não tem numeros
					if(funcao.equalsIgnoreCase("estudante")
							|| funcao.equalsIgnoreCase("docente")
							|| funcao.equalsIgnoreCase("funcionario")){
						break;
					}else{
						System.out.println("A categoria função não pode conter números");
					}

				}catch (NumberFormatException ex){
					System.out.println("A categoria função não pode conter números");
				}catch (InputMismatchException e){
					System.out.println("A categoria função não pode conter números");
				}
			}

			//departamento
			while (true){

				try {
					System.out.printf("Departamento: ");
					input = new Scanner(System.in);
					departamento = input.nextLine();

					if(!departamento.matches(".*\\d.*") && departamento.matches("[a-zA-Z]+")){ //O input não tem numeros
						break;
					}else{
						System.out.println("A categoria Departamento não pode conter números");
					}

				}catch (NumberFormatException ex){
					System.out.println("A categoria Departamento não pode conter números");
				}catch (InputMismatchException e){
					System.out.println("A categoria Departamento não pode conter números");
				}

			}

			//num telefone
			while(true){
				try{
					System.out.printf("Número de Telefone: ");
					input = new Scanner(System.in);
					num_telefone = Integer.parseInt(input.nextLine());
					if (String.valueOf(num_telefone).length() != 9){
						System.out.printf("Insira um número de telefone com 9 digitos: ");
						input = new Scanner(System.in);
						num_cc = input.nextLine();
					}
					break;
				}catch (NumberFormatException ex){
					System.out.println("Insira um número válido");
				}

			}

			//morada
			while (true){
				try {
					System.out.printf("Morada: ");
					input = new Scanner(System.in);
					morada = input.nextLine();

					if (String.valueOf(morada).length() != 0 && !morada.isBlank()) {
						break;
					} else {
						System.out.println("Insira a sua morada");
					}

				}catch (InputMismatchException e){
					System.out.println("Insira a sua morada");
				}

			}

			//num CC
			while (true){
				try {
					System.out.printf("Número do CC: ");
					input = new Scanner(System.in);
					num_cc = input.nextLine();
					int num_cc_error = Integer.parseInt(num_cc);
					if (String.valueOf(num_cc_error).length() != 8){
						System.out.printf("Insira um número de CC com 8 digitos: ");
						input = new Scanner(System.in);
						num_telefone = Integer.parseInt(input.nextLine());
					}
					break;
				}catch (NumberFormatException ex){
					System.out.println("Insira um número de CC válido");
				}
			}

			//data validade
			while (true){
				System.out.printf("Data de Validade do CC (MM/yyyy): ");
				input = new Scanner(System.in);
				String dataInput = "01/" + input.nextLine();

				data_validade_cc = dataInput;
				break;

			}

		Pessoa p = new Pessoa(nome, password, funcao, departamento, num_telefone, morada, num_cc, data_validade_cc);

		return p;
	}


	public static Eleicao criaEleicao(){

		String data_inicio;
		String data_fim;
		String titulo;
		String descricao;
		String tipoEleicao;
		String departamento;

		Scanner input;


		//data inicio
		while (true){
			try{
				System.out.printf("Data de Início da Eleicao (dd-MM-yyyy HH:mm):");
				input = new Scanner(System.in);
				String dataInput = input.nextLine();
				Date dataError = new SimpleDateFormat("dd-MM-yyyy HH:mm").parse(dataInput);
				data_inicio = dataInput;
				break;
			}catch (ParseException e) {
				System.out.println("Insira uma data válida");
			}
		}
		//data fim
		while (true){
			try{
				System.out.printf("Data de Fim da Eleicao (dd-MM-yyyy HH:mm):");
				input = new Scanner(System.in);
				String dataInput = input.nextLine();
				Date dataError = new SimpleDateFormat("dd-MM-yyyy HH:mm").parse(dataInput);
				data_fim = dataInput;
				break;
			}catch (ParseException e) {
				System.out.println("Insira uma data válida");
			}
		}

		//titulo eleicao
		while (true){
			System.out.printf("Titulo: ");
			input = new Scanner(System.in);
			titulo = input.nextLine();
			try {

				if (String.valueOf(titulo).length() != 0 && !titulo.isBlank()) {
					break;
				} else {
					System.out.println("Insira um título para a Eleição");
				}

			}catch (InputMismatchException e){
				System.out.println("Insira um título para a Eleição");
			}
		}

		//descricao eleicao
		while (true){
			System.out.printf("Descrição: ");
			input = new Scanner(System.in);
			descricao = input.nextLine();
			try {

				if (String.valueOf(descricao).length() != 0 && !descricao.isBlank()) {
					break;
				} else {
					System.out.println("Insira uma descrição para a Eleição");
				}

			}catch (InputMismatchException e){
				System.out.println("Insira uma descrição para a Eleição");
			}
		}

		//tipo eleicao
		while (true){
			System.out.printf("Tipo Eleição: ");
			input = new Scanner(System.in);
			tipoEleicao = input.nextLine();
			try {

				if (String.valueOf(descricao).length() != 0 && !descricao.isBlank() &&
						(tipoEleicao.equalsIgnoreCase("estudante")
						|| tipoEleicao.equalsIgnoreCase("docente")
						|| tipoEleicao.equalsIgnoreCase("funcionario"))) {
					break;
				} else {
					System.out.println("Insira um tipo de Eleição (Estudante/Docente/Funcionario)");
				}

			}catch (InputMismatchException e){
				System.out.println("Insira um tipo de Eleição (Estudante/Docente/Funcionario)");
			}
		}

		//local eleicao
		while (true){
			System.out.printf("Departamento: ");
			input = new Scanner(System.in);
			departamento = input.nextLine();
			try {

				if (String.valueOf(departamento).length() != 0 && !departamento.isBlank()) {
					break;
				} else {
					System.out.println("Insira um departamento para a realiação da Eleição");
				}

			}catch (InputMismatchException e){
				System.out.println("Insira um departamento para a realiação da Eleição");
			}
		}


		Eleicao e = new Eleicao(data_inicio, data_fim, titulo, descricao, tipoEleicao, departamento, null, 0);

		return e;
		}

	@Override
	public void displayEleicoes(String id, String titulo, String tipo, String departamento, String data_inicio) throws RemoteException {
		System.out.println("ID ->" + id);
		System.out.println("Título -> " + titulo);
		System.out.println("Tipo -> " + tipo);
		System.out.println("Departamento -> " + departamento);
		System.out.println("Data de Início -> " + data_inicio);
		System.out.println(" - - - - - - - - - - - - - - - - - - - - - - - - - - - - ");
	}

	@Override
	public void displayCandidatura(String id, String nomeCandidato, String categoria, String numEleicao) throws RemoteException{
		System.out.println(" -_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-");
		System.out.println("ID -> " + id);
		System.out.println("Nome Candidato -> " + nomeCandidato);
		System.out.println("Categoria -> " + categoria);
		System.out.println("Número de Eleição -> " + numEleicao);
		System.out.println(" -_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-");
	}

	public static void gereCandidato(RMInterface h) throws RemoteException, SQLException {
		//TODO
		//Listar as eleições a decorrer - DONE
		//1-> Buscar a lista de candidaturas para uma determinada eleição. - DONE
		//2-> Para o tipo de eleicao mostrar a todas as pessoas que estão na tabela das pessoas
		//3-> Adicionar uma certa pessoa a uma determinada candidatura
		//4-> Remover uma pessoa de uma certa lista se ela lá estiver
		System.out.println(" - - - - - - - - - - - - - - - - - - - - - - - - - - - - ");
		System.out.println("\t\t\t\tLISTA DE ELEIÇÕES");
		h.ListaEleicoes();

		int maxEleicoes, opcaoEleicao;
		maxEleicoes = h.maxEleicoes();

		Scanner input;

		//Eleição a ver
		while (true){
			try{
				System.out.printf("Indique a eleição a visualizar: ");
				input = new Scanner(System.in);
				String eleicao = input.nextLine();
				opcaoEleicao = Integer.parseInt(eleicao);
				if(!(opcaoEleicao < 1 || opcaoEleicao > maxEleicoes)) { //Ele selecionou uma eleicao valida
					//Fazer o display das candidaturas dessa eleicao
					System.out.println("\n");
					h.ListaCandidaturas(opcaoEleicao);
					System.out.println("\n\n\nSAI\n\n\n");
					break;
				}else{
					System.out.println("Insira uma Eleição entre 1 e " + maxEleicoes);
				}
			}catch (NumberFormatException ex){
				System.out.println("Insira uma Eleição entre 1 e " + maxEleicoes);
			}
		}






	}

	public static void main(String args[]) {

		//System.getProperties().put("java.security.policy", "policy.all");
		//System.setSecurityManager(new RMISecurityManager());

		try {
			RMInterface h = (RMInterface) LocateRegistry.getRegistry(6000).lookup("RMIConnect");
			AdminConsole admin = new AdminConsole();

			h.subscribe(admin);



			while(true){

				displayFuncionalidade();
				String opcao = "";
				try{
					System.out.printf("OPÇÃO: ");
					Scanner input = new Scanner(System.in);
					opcao = input.nextLine();
					Integer.parseInt(opcao);
					if (Integer.parseInt(opcao) <= 10){
						switch(opcao) {
							case "1":
								// Registar Pessoas
								h.registaPessoa(registaPessoa());
								break;
							case "2":
								h.criaEleicao(criaEleicao());
								// Criar Eleição
								break;
							case "3":
								gereCandidato(h);
								// Gerir listas de candidatos a uma eleição
								break;
							case "4":
								// Gerir mesas de voto
								break;
							case "5":
								// Alterar propriedades de uma eleição
								break;
							case "6":
								// Saber em que local votou cada eleitors
								break;
							case "7":
								// Mostrar estado das mesas de voto
								break;
							case "8":
								// Mostrar eleitores em tempo real
								break;
							case "9":
								// Terminar uma eleição
								break;
							case "10":
								// Consultar resultados detalhados de eleições passadas
								break;

						}
					}else{ //Opção não incluída nas possíveis
						System.out.println("Escolha uma opção válida");
					}

				} catch(NumberFormatException ex){ // O que foi lido no input não foi um número
					System.out.println("Insira um número válido");
				}
			}

		} catch (Exception e) {
			System.out.println("Exception in main: " + e);
		}

	}

}