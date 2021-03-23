package com.company.RMIFiles;

import com.company.Pessoa;

import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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
        System.out.println("Entrei");
        try {

            stmt = c.createStatement();
            String sql = "INSERT INTO lista_pessoas (num_cc,num_telefone,departamento,funcao,nome, password, morada, data_validade) "
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
}