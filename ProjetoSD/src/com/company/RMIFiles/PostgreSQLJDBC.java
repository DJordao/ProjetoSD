package com.company.RMIFiles;

import com.company.Eleicao;
import com.company.Pessoa;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class PostgreSQLJDBC {
    public PostgreSQLJDBC() {
    }

    public Connection connectDB() throws SQLClientInfoException {
        Connection c = null;
        try {
            Class.forName("org.postgresql.Driver");
            c = DriverManager
                    .getConnection("jdbc:postgresql://localhost:5432/SD-Project",
                            "postgres", "josemiguel1910");
            c.setAutoCommit(false);
            System.out.println("Opened database successfully");

            //c.close();
        } catch ( Exception e ) {
            System.err.println( e.getClass().getName()+": "+ e.getMessage() );
            System.exit(0);
        }
        //System.out.println("Operation done successfully");
        return c;
    }
    
    public void InsertPessoas(Pessoa p) throws SQLClientInfoException {

        Connection c = connectDB();
        Statement stmt = null;
        PreparedStatement myStmt;
        try {

            stmt = c.createStatement();
            String sql = "INSERT INTO pessoa (num_cc,num_telefone,departamento,funcao,nome, password, morada, data_validade) "
                    + "VALUES (?,?,?,?,?, ?, ?, ?);";

            myStmt = c.prepareStatement(sql);



            SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyy");
            Date parsed = format.parse(p.getData_validade_cc());
            java.sql.Date sqlDataValidade = new java.sql.Date(parsed.getTime());

            //
            myStmt.setString(1, p.getNum_cc());
            myStmt.setInt(2, p.getNum_telefone());
            myStmt.setString(3, p.getDepartamento());
            myStmt.setString(4, p.getFuncao());
            myStmt.setString(5, p.getNome());
            myStmt.setString(6, p.getPassword());
            myStmt.setString(7, p.getMorada());
            myStmt.setDate(8, sqlDataValidade);


            myStmt.executeUpdate();

            myStmt.close();
            stmt.close();
            c.commit();
        } catch (Exception e) {
            System.err.println( e.getClass().getName()+": "+ e.getMessage() );
            System.exit(0);
        }
        System.out.println("Pessoas Inseridas com sucesso");


    }

    public void InsertElection(Eleicao e) throws SQLClientInfoException {

        Connection c = connectDB();
        Statement stmt = null;
        PreparedStatement myStmt;
        int idElection = 0;

        try {
            //Buscar o valor do último Id que está na tabela
            stmt = c.createStatement();
            String sqlIDEleicao = "SELECT COUNT(id)" + "FROM eleicao";
            ResultSet rs = stmt.executeQuery(sqlIDEleicao);

            while (rs.next()){
                idElection = rs.getInt(1);
            }

            stmt = c.createStatement();
            String sql = "INSERT INTO eleicao (id, data_inicio, data_fim, titulo, descricao, tipo, departamento) "
                    + "VALUES (?,?,?,?,?, ?, ?);";

            myStmt = c.prepareStatement(sql);

            //Passar a data inicio para TIMESTAMP para guardar no SQL
            DateTimeFormatter format = DateTimeFormatter.ofPattern("dd-MM-yyy HH:mm");
            LocalDateTime localTime = LocalDateTime.from(format.parse(e.getData_inicio()));

            Timestamp timestampIni = Timestamp.valueOf(localTime);

            //Passar a data fim para TIMESTAMP para guardar no SQL
            localTime = LocalDateTime.from(format.parse(e.getData_fim()));

            Timestamp timestampFim = Timestamp.valueOf(localTime);

            //Colocar os valores na BD
            idElection += 1;
            myStmt.setInt(1,idElection);
            myStmt.setTimestamp(2,timestampIni);
            myStmt.setTimestamp(3,timestampFim);
            myStmt.setString(4,e.getTitulo());
            myStmt.setString(5,e.getDescricao());
            myStmt.setString(6,e.getTipoEleicao());
            myStmt.setString(7,e.getDepartamento());

            myStmt.executeUpdate();

            myStmt.close();
            stmt.close();
            c.commit();
        } catch (Exception ex) {
            System.err.println( ex.getClass().getName()+": "+ ex.getMessage() );
            System.exit(0);
        }
        System.out.println("Eleição Criada com sucesso");


    }

    public ResultSet listaEleicoes() throws SQLClientInfoException {

        Connection c = connectDB();
        Statement stmt = null;
        PreparedStatement myStmt;
        ResultSet eleicoes = null;

        try {
            //Retorna todas as eleições a decorrer
            stmt = c.createStatement();
            String sqlIDEleicao = "SELECT id, titulo, tipo, departamento, data_inicio " + "FROM eleicao";
            ResultSet rs = stmt.executeQuery(sqlIDEleicao);

            eleicoes = rs;

            /*while (rs.next()){
                idElection = rs.getInt(1);
            }*/
            
            //stmt.close();
            c.commit();
            
            return rs;
        } catch (Exception ex) {
            System.err.println( ex.getClass().getName()+": "+ ex.getMessage() );
            System.exit(0);
        }
        System.out.println("Eleição Criada com sucesso");
        return eleicoes;
    }

    public int maxEleicoes() throws SQLClientInfoException{
        Connection c = connectDB();
        Statement stmt = null;
        int maxEleicao = 0;

        try {
            //Buscar o valor do último Id que está na tabela
            stmt = c.createStatement();
            String sqlIDEleicao = "SELECT COUNT(id) " + "FROM eleicao";
            ResultSet rs = stmt.executeQuery(sqlIDEleicao);

            while (rs.next()){
                maxEleicao = rs.getInt(1);
            }



            stmt.close();
            c.commit();
        } catch (Exception ex) {
            System.err.println( ex.getClass().getName()+": "+ ex.getMessage() );
            System.exit(0);
        }
        return maxEleicao;

    }

    public ResultSet listaCandidaturas(int opcaoEleicao) throws SQLClientInfoException {
        Connection c = connectDB();
        Statement stmt = null;
        PreparedStatement myStmt;
        ResultSet candidatos = null;

        try {
            //Retorna todas as candidaturas de uma eleicao a decorrer
            stmt = c.createStatement();
            String sqlCandidatos = "SELECT * " + "FROM lista_candidatos " + "WHERE eleicao_id = " + opcaoEleicao;

            myStmt = c.prepareStatement(sqlCandidatos);
            //myStmt.setInt(1, opcaoEleicao);

            ResultSet rs = stmt.executeQuery(sqlCandidatos);

            candidatos = rs;

            c.commit();

            return rs;
        } catch (Exception ex) {
            System.err.println( ex.getClass().getName()+": "+ ex.getMessage() );
            System.exit(0);
        }
        return candidatos;
    }
}