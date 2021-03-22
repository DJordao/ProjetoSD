package com.company.RMIFiles;
import java.sql.*;

public class PostgreSQLJDBC {
    public static void main(String args[]) throws SQLException {
        Connection c = null;
        Statement st = null;
        try {
            Class.forName("org.postgresql.Driver");
            c = DriverManager
                    .getConnection("jdbc:postgresql://localhost:5432/SD-Project",
                            "postgres", "josemiguel1910");



            System.out.println("Opened database successfully");
            String query = "SELECT * FROM lista_pessoas";
            st = c.createStatement();
            ResultSet rs = st.executeQuery(query);

            while ( rs.next() ) {
                int num_cc = rs.getInt("num_cc");
                String  name = rs.getString("nome");
                int telefone  = rs.getInt("num_telefone");
                String  address = rs.getString("morada");

                System.out.println( "CC NUM = " + num_cc );
                System.out.println( "NAME = " + name );
                System.out.println( "TELEFONE = " + telefone );
                System.out.println( "ADDRESS = " + address );
                System.out.println();
            }
            rs.close();
            st.close();
            c.close();


        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e.getClass().getName()+": "+e.getMessage());
            System.exit(0);
        }


    }
}