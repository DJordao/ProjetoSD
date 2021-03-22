package com.company.RMIFiles;

import com.company.Message;
import com.company.Pessoa;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.text.ParseException;
import java.util.Date;
import java.util.InputMismatchException;
import java.util.Scanner;
import java.text.SimpleDateFormat;

public class AdminConsole {

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

	public static Pessoa registaPessoa(){
		String nome;
		String password;
		String funcao;
		String departamento;
		int num_telefone = 0;
		String morada;
		String num_cc;
		Date data_validade_cc;

		Scanner input;

			System.out.printf("Nome: ");
			input = new Scanner(System.in);
			nome = input.nextLine();

			System.out.printf("Password: ");
			input = new Scanner(System.in);
			password = input.nextLine();

			while (true){
				try {
					System.out.printf("Funcao: ");
					//Estudante, Docente, Func
					input = new Scanner(System.in);
					funcao = input.nextLine();

					if(!funcao.matches(".*\\d.*") && funcao.matches("[a-zA-Z]+")){ //O input não tem numeros
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

			System.out.printf("Morada: ");
			input = new Scanner(System.in);
			morada = input.nextLine();

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

			while (true){
				try {
					System.out.printf("Data de Validade do CC (MM/yyyy): ");
					input = new Scanner(System.in);
					Date dataInput = new SimpleDateFormat("MM/yyyy").parse(input.nextLine());
					data_validade_cc = dataInput;
					break;

				}catch (ParseException e){
					System.out.println("Insira uma data no formato MM/yyyy");
				}

			}

			Pessoa p = new Pessoa(nome, password, funcao, departamento, num_telefone, morada, num_cc, data_validade_cc);

		System.out.println("===================");
		System.out.println("Pessoa registada com sucesso!");
		System.out.println("===================");

		return p;
	}



	public static void main(String args[]) {

		//System.getProperties().put("java.security.policy", "policy.all");
		//System.setSecurityManager(new RMISecurityManager());

		try {
			RMInterface h = (RMInterface) Naming.lookup("hello");

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
								// Criar Eleição
								break;
							case "3":
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