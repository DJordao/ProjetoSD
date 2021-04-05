package com.company.RMIFiles;

import com.company.Candidato;
import com.company.Eleicao;
import com.company.Pessoa;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.concurrent.CopyOnWriteArrayList;

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
            //System.out.println("Opened database successfully");

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

    public int InsertElection(Eleicao e) throws SQLClientInfoException {

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
            String sql = "INSERT INTO eleicao (id, data_inicio, data_fim, titulo, descricao, tipo) "
                    + "VALUES (?,?,?,?,?, ?);";

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

            myStmt.executeUpdate();

            myStmt.close();
            stmt.close();
            c.commit();
        } catch (Exception ex) {
            System.err.println( ex.getClass().getName()+": "+ ex.getMessage() );
            System.exit(0);
        }
        System.out.println("Eleição Criada com sucesso");

        return idElection;
    }

    public ResultSet listaEleicoes() throws SQLClientInfoException {

        Connection c = connectDB();
        Statement stmt = null;
        PreparedStatement myStmt;
        ResultSet eleicoes = null;

        try {
            //Retorna todas as eleições a decorrer
            stmt = c.createStatement();
            String sqlIDEleicao = "SELECT eleicao.id, titulo, tipo, data_inicio, departamento " + "FROM eleicao, departamento " +
                    "WHERE eleicao.id = departamento.eleicao_id AND CURRENT_TIMESTAMP BETWEEN data_inicio AND data_fim ORDER BY eleicao.id";
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
        //System.out.println("Eleição Criada com sucesso");
        return eleicoes;
    }

    public ResultSet listaEleicoesNaoComecadas() throws SQLClientInfoException {

        Connection c = connectDB();
        Statement stmt = null;
        PreparedStatement myStmt;
        ResultSet eleicoes = null;

        try {
            //Retorna todas as eleições a decorrer
            stmt = c.createStatement();
            String sqlIDEleicao = "SELECT eleicao.id, titulo, tipo, data_inicio, departamento " + "FROM eleicao, departamento " +
                    "WHERE eleicao.id = departamento.eleicao_id AND CURRENT_TIMESTAMP < data_inicio  ORDER BY eleicao.id";
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
        //System.out.println("Eleição Criada com sucesso");
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
            String sqlCandidatos =  "SELECT lista_candidatos.id, nomecandidato, categoria, eleicao_id, titulo " +
                                    "FROM lista_candidatos, eleicao " +
                                    "WHERE eleicao_id = " + opcaoEleicao + " AND lista_candidatos.eleicao_id = eleicao.id ORDER BY id";


            myStmt = c.prepareStatement(sqlCandidatos);
            //myStmt.setInt(1, opcaoEleicao);

            ResultSet rs = stmt.executeQuery(sqlCandidatos);

            candidatos = rs;

            c.commit();

            System.out.println("BD-entei");

            return rs;
        } catch (Exception ex) {
            System.err.println( ex.getClass().getName()+": "+ ex.getMessage() );
            System.exit(0);
        }
        return candidatos;
    }

    public String[] getTipoEleicaoAndDepartamento(int opcaoEleicao) throws SQLClientInfoException{
        Connection c = connectDB();
        Statement stmt = null;
        String tipoAndDep = "";
        String[] output = new String[4];
        int i = 0;
        try {
            //Buscar tipo de eleicao e o seu departamento
            stmt = c.createStatement();
            String sqlIDEleicao = "SELECT tipo, departamento " +
                    "FROM eleicao, departamento " +
                    "WHERE eleicao.id = " + opcaoEleicao + " AND departamento.eleicao_id = '" + opcaoEleicao + "' ORDER BY eleicao.id";
            ResultSet rs = stmt.executeQuery(sqlIDEleicao);

            while (rs.next()){
                tipoAndDep = rs.getString("tipo");
                tipoAndDep += " ";
                tipoAndDep += rs.getString("departamento");
                //System.out.println("OUTPUT: " + tipoAndDep);
                output[i] = tipoAndDep;
                i++;
            }

            stmt.close();
            c.commit();
        } catch (Exception ex) {
            System.err.println( ex.getClass().getName()+": "+ ex.getMessage() );
            System.exit(0);
        }
        return output;

    }

    public ResultSet listaPessoasParaCandidatura(int opcaoEleicao) throws SQLClientInfoException{
        Connection c = connectDB();
        Statement stmt = null;
        PreparedStatement myStmt;
        ResultSet candidatos = null;

        try {
            //1->Preciso de verificar o tipo de eleicao (Estudante, Docente, Funcionário) e comparar com a funcao do aluno
            //2->Depois verificar em que departamento a eleicao vai ocorrer e comparar com o departamento da pessoa
            //Se verificar estas 2 condições é feito o display da pessoa            stmt = c.createStatement();

            //Obter o Tipo e Departamento da Eleicao
            String[] listaTipoDep =  getTipoEleicaoAndDepartamento(opcaoEleicao);
            for (int i = 0; i < listaTipoDep.length; i++){
                String[] output = listaTipoDep[i].split(" ");
                String tipo = output[0];
                String departamento = output[1];

                stmt = c.createStatement();


                String sqlPessoasEleicao =  "SELECT pessoa.num_cc, pessoa.nome " +
                        "FROM pessoa " +
                        "WHERE pessoa.num_cc NOT IN (SELECT pessoa_lista_candidatos.pessoa_num_cc " +
                                                    "FROM pessoa_lista_candidatos) " +
                        "AND pessoa.departamento = '" + departamento + "' AND pessoa.funcao = '" + tipo + "'";

                myStmt = c.prepareStatement(sqlPessoasEleicao);

                ResultSet rs = stmt.executeQuery(sqlPessoasEleicao);
                boolean val = rs.next();
                if (val == true) return rs;
                candidatos = rs;
            }
            c.commit();

        } catch (Exception ex) {
            System.err.println( ex.getClass().getName()+": "+ ex.getMessage() );
            System.exit(0);
        }
        return candidatos;

    }

    public void InsertPessoasCandidatura(int opcaoEleicao, String num_cc, String partido, int idPartido) throws SQLClientInfoException {

        Connection c = connectDB();
        Statement stmt = null;
        PreparedStatement myStmt;
        try {

            stmt = c.createStatement();
            String sql = "INSERT INTO pessoa_lista_candidatos (pessoa_num_cc, lista_candidatos_id) "
                    + "VALUES (?,?);";

            myStmt = c.prepareStatement(sql);

            myStmt.setString(1, num_cc);
            myStmt.setInt(2, idPartido);

            myStmt.executeUpdate();

            myStmt.close();
            stmt.close();
            c.commit();
        } catch (Exception e) {
            System.err.println( e.getClass().getName()+": "+ e.getMessage() );
            System.exit(0);
        }
    }

    public ResultSet ListaElementosCandidatura(int opcaoEleicao, String candidatura, int idPartido) throws SQLClientInfoException {

        Connection c = connectDB();
        Statement stmt = null;
        PreparedStatement myStmt;
        ResultSet elementosCandidatura = null;

        try {

            String[] listaTipoDep =  getTipoEleicaoAndDepartamento(opcaoEleicao);
            for (int i = 0; i < listaTipoDep.length; i++){
                if (listaTipoDep[i] != null){
                    //Obter para uma certa candidatura todos os seus elementos
                    String[] output = listaTipoDep[i].split(" ");

                    String tipo = output[0];
                    String departamento = output[1];

                    stmt = c.createStatement();


                    String sqlPessoasEleicao =  "SELECT pessoa.num_cc, pessoa.nome, nomecandidato " +
                            "FROM pessoa, lista_candidatos " +
                            "WHERE pessoa.num_cc IN (SELECT pessoa_lista_candidatos.pessoa_num_cc " +
                            "FROM pessoa_lista_candidatos WHERE lista_candidatos_id = '" + idPartido + "') " +
                            "AND pessoa.departamento = '" + departamento + "' AND pessoa.funcao = '" + tipo + "'" +
                            "AND lista_candidatos.nomecandidato =  '" + candidatura + "'";

                    myStmt = c.prepareStatement(sqlPessoasEleicao);

                    ResultSet rs = stmt.executeQuery(sqlPessoasEleicao);
                    boolean val = rs.next();
                    if (val == true) return rs;
                    elementosCandidatura = rs;

                    c.commit();

                }

            }

        } catch (Exception ex) {
            System.err.println( ex.getClass().getName()+": "+ ex.getMessage() );
            System.exit(0);
        }
        return elementosCandidatura;
    }

    public void RemovePessoaCandidatura(String num_cc, String nomeLista) throws SQLClientInfoException {
        Connection c = connectDB();
        Statement stmt = null;
        PreparedStatement myStmt;
        try {

            stmt = c.createStatement();
            String sql = "DELETE FROM pessoa_lista_candidatos " +
                         "WHERE pessoa_num_cc = '" + num_cc + "'";

            myStmt = c.prepareStatement(sql);

            myStmt.executeUpdate();

            myStmt.close();
            stmt.close();
            c.commit();
        } catch (Exception e) {
            System.err.println( e.getClass().getName()+": "+ e.getMessage() );
            System.exit(0);
        }


    }

    public ResultSet listaTudoEleicao(int opcaoEleicao) throws SQLClientInfoException{
        Connection c = connectDB();
        Statement stmt = null;
        PreparedStatement myStmt;
        ResultSet AllelementosCandidatura = null;

        try {
            stmt = c.createStatement();


            String sqlPessoasEleicao =  "SELECT pessoa.num_cc, pessoa.nome, lista_candidatos.id, lista_candidatos.nomecandidato " +
                    "FROM pessoa, lista_candidatos " +
                    "WHERE pessoa.num_cc  IN " +
                    "(SELECT pessoa_lista_candidatos.pessoa_num_cc " + " FROM pessoa_lista_candidatos)" +
                    "AND lista_candidatos.id IN (SELECT pessoa_lista_candidatos.lista_candidatos_id " +
                    "FROM pessoa_lista_candidatos " +
                    "WHERE pessoa_lista_candidatos.lista_candidatos_id = lista_candidatos.id AND pessoa_lista_candidatos.pessoa_num_cc = pessoa.num_cc AND lista_candidatos.eleicao_id = '" + opcaoEleicao + "') ";


            myStmt = c.prepareStatement(sqlPessoasEleicao);

            ResultSet rs = stmt.executeQuery(sqlPessoasEleicao);

            AllelementosCandidatura = rs;

            c.commit();

            return rs;
        } catch (Exception ex) {
            System.err.println( ex.getClass().getName()+": "+ ex.getMessage() );
            System.exit(0);
        }
        return AllelementosCandidatura;
    }

    public ResultSet getDetalhesEleicao(int opcaoEleicao) throws SQLClientInfoException{
        Connection c = connectDB();
        Statement stmt = null;
        PreparedStatement myStmt;
        ResultSet detalhesEleicao = null;

        try {
            stmt = c.createStatement();


            String sqlPessoasEleicao =  "SELECT * " +
                    "FROM eleicao " +
                    "WHERE eleicao.id = '" + opcaoEleicao + "' ORDER BY id";


            myStmt = c.prepareStatement(sqlPessoasEleicao);

            ResultSet rs = stmt.executeQuery(sqlPessoasEleicao);

            detalhesEleicao = rs;

            c.commit();

            return rs;
        } catch (Exception ex) {
            System.err.println( ex.getClass().getName()+": "+ ex.getMessage() );
            System.exit(0);
        }
        return detalhesEleicao;
    }

    public void UpdatePropriedadesEleicao(int opcaoEleicao, String tituloAlteracao, String descricaoAlteracao, Timestamp data_inicio, Timestamp data_fim) throws SQLClientInfoException{
        Connection c = connectDB();
        Statement stmt = null;
        PreparedStatement myStmt;
        try {
            stmt = c.createStatement();
            String sql = "UPDATE eleicao " +
                    "SET titulo = '" + tituloAlteracao + "', descricao = '" + descricaoAlteracao + "', data_inicio = '" + data_inicio + "', data_fim = '" + data_fim + "'" +
                    "WHERE id = '" + opcaoEleicao + "'";

            myStmt = c.prepareStatement(sql);
            myStmt.executeUpdate();

            myStmt.close();
            stmt.close();
            c.commit();
        } catch (Exception e) {
            System.err.println( e.getClass().getName()+": "+ e.getMessage() );
            System.exit(0);
        }
    }

    public ResultSet findPessoa(String num_cc) throws SQLClientInfoException{
        Connection c = connectDB();
        Statement stmt = null;
        PreparedStatement myStmt;
        ResultSet pessoa = null;

        try {
            stmt = c.createStatement();
            String sql = "SELECT * " + "FROM pessoa " + "WHERE num_cc = '" + num_cc + "'";

            ResultSet rs = stmt.executeQuery(sql);
            pessoa = rs;

            //myStmt.close();
            //stmt.close();
            c.commit();

            return rs;
        } catch (Exception e) {
            System.err.println( e.getClass().getName()+": "+ e.getMessage() );
            System.exit(0);
        }
    return pessoa;
    }

    public ResultSet getEleicao(String departamento) throws SQLClientInfoException{
        Connection c = connectDB();
        Statement stmt = null;
        PreparedStatement myStmt;
        ResultSet eleicao = null;
        try {
            stmt = c.createStatement();
            String sql = "SELECT * " + "FROM eleicao " + "WHERE id IN (SELECT eleicao_id  FROM departamento WHERE departamento.departamento = '" + departamento + "') AND CURRENT_TIMESTAMP BETWEEN data_inicio AND data_fim ORDER BY eleicao.id";

            ResultSet rs = stmt.executeQuery(sql);
            eleicao = rs;

            //myStmt.close();
            //stmt.close();
            c.commit();

            return rs;
        } catch (Exception e) {
            System.err.println( e.getClass().getName()+": "+ e.getMessage() );
            System.exit(0);
        }
        return eleicao;
    }

    public ResultSet getListaCandidatos(int eleicaoID) throws SQLClientInfoException{
        Connection c = connectDB();
        Statement stmt = null;
        PreparedStatement myStmt;
        ResultSet listaCandidatos = null;
        try {
            stmt = c.createStatement();
            String sql = "SELECT * " + "FROM lista_candidatos " + "WHERE eleicao_id = '" + eleicaoID + "' ORDER BY id";

            ResultSet rs = stmt.executeQuery(sql);
            listaCandidatos = rs;

            //myStmt.close();
            //stmt.close();
            c.commit();

            return rs;
        } catch (Exception e) {
            System.err.println( e.getClass().getName()+": "+ e.getMessage() );
            System.exit(0);
        }
        return listaCandidatos;

    }

    public int getIdVoto() throws SQLClientInfoException{
        Connection c = connectDB();
        Statement stmt = null;
        int maxIdVoto = 0;

        try {
            //Buscar o valor do último Id que está na tabela
            stmt = c.createStatement();
            String sqlIDEleicao = "SELECT COUNT(id_voto) " + "FROM voto ";
            ResultSet rs = stmt.executeQuery(sqlIDEleicao);

            while (rs.next()){
                maxIdVoto = rs.getInt(1);
            }



            stmt.close();
            c.commit();
        } catch (Exception ex) {
            System.err.println( ex.getClass().getName()+": "+ ex.getMessage() );
            System.exit(0);
        }
        return maxIdVoto;

    }

    public int getMaxCandidato() throws SQLClientInfoException{
        Connection c = connectDB();
        Statement stmt = null;
        int maxCandidatos = 0;

        try {
            //Buscar o valor do último Id que está na tabela
            stmt = c.createStatement();
            String sqlIDEleicao = "SELECT COUNT(id) " + "FROM lista_candidatos ";
            ResultSet rs = stmt.executeQuery(sqlIDEleicao);

            while (rs.next()){
                maxCandidatos = rs.getInt(1);
            }



            stmt.close();
            c.commit();
        } catch (Exception ex) {
            System.err.println( ex.getClass().getName()+": "+ ex.getMessage() );
            System.exit(0);
        }
        return maxCandidatos;

    }

    public ResultSet getlocalVotoEleitores(int eleicaoID) throws SQLClientInfoException {
        Connection c = connectDB();
        Statement stmt = null;
        PreparedStatement myStmt;
        ResultSet listaVotoEleitores = null;
        try {
            stmt = c.createStatement();
            String sql = "SELECT local_voto, hora_voto, nome, num_cc " + "FROM voto, pessoa " + "WHERE eleicao_id = '" + eleicaoID + "' "
                    + "AND pessoa.num_cc IN (SELECT pessoa_num_cc FROM voto) AND pessoa.num_cc = voto.pessoa_num_cc";

            ResultSet rs = stmt.executeQuery(sql);
            listaVotoEleitores = rs;

            //myStmt.close();
            //stmt.close();
            c.commit();

            return rs;
        } catch (Exception e) {
            System.err.println( e.getClass().getName()+": "+ e.getMessage() );
            System.exit(0);
        }
        return listaVotoEleitores;


    }

    public ResultSet getEleicaoByID(int opcaoEleicao) throws SQLClientInfoException{
        Connection c = connectDB();
        Statement stmt = null;
        PreparedStatement myStmt;
        ResultSet eleicao = null;
        try {
            stmt = c.createStatement();
            String sql = "SELECT * " + "FROM eleicao " + "WHERE id = '" + opcaoEleicao + "'";

            ResultSet rs = stmt.executeQuery(sql);
            eleicao = rs;

            //myStmt.close();
            //stmt.close();
            c.commit();

            return rs;
        } catch (Exception e) {
            System.err.println( e.getClass().getName()+": "+ e.getMessage() );
            System.exit(0);
        }
        return eleicao;

    }

    public void criaNovoCandidato(int idCandidato, Candidato cand, int opcaoEleicao) throws SQLClientInfoException {
        Connection c = connectDB();
        Statement stmt = null;
        PreparedStatement myStmt;
        try {

            stmt = c.createStatement();
            String sql = "INSERT INTO lista_candidatos (id, nomecandidato, categoria,eleicao_id) "
                    + "VALUES (?,?,?,?);";

            myStmt = c.prepareStatement(sql);


            //
            myStmt.setInt(1, idCandidato);
            myStmt.setString(2, cand.getNome());
            myStmt.setString(3, cand.getCategoria());
            myStmt.setInt(4, opcaoEleicao);



            myStmt.executeUpdate();

            myStmt.close();
            stmt.close();
            c.commit();
        } catch (Exception e) {
            System.err.println( e.getClass().getName()+": "+ e.getMessage() );
            System.exit(0);
        }
        System.out.println("Candidato Inserido com sucesso");


    }

    public int getIdEleicao(String nomeEleicao) throws SQLClientInfoException{
        Connection c = connectDB();
        Statement stmt = null;
        int idEleicao = 0;

        try {
            //Buscar o valor do último Id que está na tabela
            stmt = c.createStatement();
            String sqlIDEleicao = "SELECT id " + "FROM eleicao " + "WHERE titulo = '" + nomeEleicao + "'";
            ResultSet rs = stmt.executeQuery(sqlIDEleicao);

            while (rs.next()){
                idEleicao = rs.getInt(1);
            }



            stmt.close();
            c.commit();
        } catch (Exception ex) {
            System.err.println( ex.getClass().getName()+": "+ ex.getMessage() );
            System.exit(0);
        }
        return idEleicao;

    }

    public void criaVoto(int idVoto, String local, int idEleicao, String num_cc) throws SQLClientInfoException{
        Connection c = connectDB();
        Statement stmt = null;
        PreparedStatement myStmt;
        try {

            stmt = c.createStatement();
            String sql = "INSERT INTO voto (id_voto, local_voto, eleicao_id, pessoa_num_cc) "
                    + "VALUES (?, ?, ?, ?);";

            myStmt = c.prepareStatement(sql);

            myStmt.setInt(1, idVoto);
            myStmt.setString(2, local);
            myStmt.setInt(3, idEleicao);
            myStmt.setString(4, num_cc);

            myStmt.executeUpdate();

            myStmt.close();
            stmt.close();
            c.commit();
        } catch (Exception e) {
            System.err.println( e.getClass().getName()+": "+ e.getMessage() );
            System.exit(0);
        }
    }

    public int getVotoIDbyNome(String num_cc, int idEleicao) throws SQLClientInfoException{
        Connection c = connectDB();
        Statement stmt = null;
        int votoID = 0;

        try {
            //Buscar o valor do último Id que está na tabela
            stmt = c.createStatement();
            String sqlIDEleicao = "SELECT id_voto " + "FROM voto " + "WHERE eleicao_id = '" + idEleicao + "' AND pessoa_num_cc = '" + num_cc + "'" ;
            ResultSet rs = stmt.executeQuery(sqlIDEleicao);

            while (rs.next()){
                votoID = rs.getInt(1);
            }



            stmt.close();
            c.commit();
        } catch (Exception ex) {
            System.err.println( ex.getClass().getName()+": "+ ex.getMessage() );
            System.exit(0);
        }
        return votoID;
    }

    public void updateVotoPessoaData(Timestamp dataVoto, String num_cc, int idEleicao) throws SQLClientInfoException{
        Connection c = connectDB();
        Statement stmt = null;
        PreparedStatement myStmt;
        int votoID = getVotoIDbyNome(num_cc, idEleicao);
        try {
            stmt = c.createStatement();
            String sql = "UPDATE voto " +
                    "SET hora_voto = '" + dataVoto + "'" +
                    "WHERE id_voto = '" + votoID + "'";

            myStmt = c.prepareStatement(sql);
            myStmt.executeUpdate();

            myStmt.close();
            stmt.close();
            c.commit();
        } catch (Exception e) {
            System.err.println( e.getClass().getName()+": "+ e.getMessage() );
            System.exit(0);
        }

    }

    public void InsertElectionCandidatos(Eleicao e) throws SQLClientInfoException{
        Connection c = connectDB();
        Statement stmt = null;
        PreparedStatement myStmt;
        CopyOnWriteArrayList<Candidato> candidatoNulo = e.getListaCandidatos();
        int idCandidato = getMaxCandidato();
        int idEleicao = getIdEleicao(e.getTitulo());

        try {

            for (int i = 0; i < candidatoNulo.size(); i++){
                stmt = c.createStatement();
                String sql = "INSERT INTO lista_candidatos (id, nomecandidato, categoria,eleicao_id) "
                        + "VALUES (?,?,?,?);";

                myStmt = c.prepareStatement(sql);
                idCandidato++;

                //
                myStmt.setInt(1, idCandidato);
                myStmt.setString(2, candidatoNulo.get(i).getNome());
                myStmt.setString(3, candidatoNulo.get(i).getCategoria());
                myStmt.setInt(4, idEleicao);

                myStmt.executeUpdate();
                myStmt.close();

            }

            stmt.close();
            c.commit();
        } catch (Exception ex) {
            System.err.println( ex.getClass().getName()+": "+ ex.getMessage() );
            System.exit(0);
        }
        System.out.println("Candidato Inserido com sucesso");

    }

    public int getIDMaxDep() throws SQLClientInfoException{
        Connection c = connectDB();
        Statement stmt = null;
        int maxDep = 0;

        try {
            //Buscar o valor do último Id que está na tabela
            stmt = c.createStatement();
            String sqlIDEleicao = "SELECT MAX(id) " + "FROM departamento ";
            ResultSet rs = stmt.executeQuery(sqlIDEleicao);

            while (rs.next()){
                maxDep = rs.getInt(1);
            }



            stmt.close();
            c.commit();
        } catch (Exception ex) {
            System.err.println( ex.getClass().getName()+": "+ ex.getMessage() );
            System.exit(0);
        }
        return maxDep;

    }

    public CopyOnWriteArrayList<String> getDepEleicao(int opcaoEleicao) throws SQLClientInfoException{
        Connection c = connectDB();
        Statement stmt = null;
        CopyOnWriteArrayList<String> listaDep = new CopyOnWriteArrayList<>();
        String dep;

        try {
            stmt = c.createStatement();
            String sqlIDEleicao = "SELECT departamento " + "FROM departamento  WHERE eleicao_id = '" + opcaoEleicao + "'";
            ResultSet rs = stmt.executeQuery(sqlIDEleicao);


            while (rs.next()){
                dep = rs.getString(1);
                listaDep.add(dep);
            }

            stmt.close();
            c.commit();
        } catch (Exception ex) {
            System.err.println( ex.getClass().getName()+": "+ ex.getMessage() );
            System.exit(0);
        }
        return listaDep;
    }

    public void InsertDepartamentoEleicao(int idEleicao, Eleicao e) throws SQLClientInfoException{
        Connection c = connectDB();
        Statement stmt = null;
        PreparedStatement myStmt;
        CopyOnWriteArrayList<String> listaDep = e.getDepartamento();
        int idMaxdDep = getIDMaxDep();

        try {

            for (int i = 0; i < listaDep.size(); i++){
                stmt = c.createStatement();
                String sql = "INSERT INTO departamento (id, departamento,eleicao_id) "
                        + "VALUES (?,?,?);";

                myStmt = c.prepareStatement(sql);
                idMaxdDep++;

                //
                myStmt.setInt(1, idMaxdDep);
                myStmt.setString(2, listaDep.get(i));
                myStmt.setInt(3, idEleicao);

                myStmt.executeUpdate();
                myStmt.close();

            }

            stmt.close();
            c.commit();
        } catch (Exception ex) {
            System.err.println( ex.getClass().getName()+": "+ ex.getMessage() );
            System.exit(0);
        }
        System.out.println("Departamento Inserido com sucesso");


    }

    public int getCandidatoByID(String nomeCandidato, int idEleicao) throws SQLClientInfoException{
        Connection c = connectDB();
        Statement stmt = null;
        int candidatoID = 0;

        try {
            //Buscar o valor do último Id que está na tabela
            stmt = c.createStatement();
            String sqlIDEleicao = "SELECT id " + "FROM lista_candidatos " + "WHERE eleicao_id = '" + idEleicao + "' AND nomecandidato = '" + nomeCandidato + "'" ;
            ResultSet rs = stmt.executeQuery(sqlIDEleicao);

            while (rs.next()){
                candidatoID = rs.getInt(1);
            }

            stmt.close();
            c.commit();
        } catch (Exception ex) {
            System.err.println( ex.getClass().getName()+": "+ ex.getMessage() );
            System.exit(0);
        }
        return candidatoID;
    }

    public void recebeVoto(String nomeCandidato, int idEleicao) throws SQLClientInfoException {
        Connection c = connectDB();
        Statement stmt = null;
        PreparedStatement myStmt;

        //
        int idCandidato = getCandidatoByID(nomeCandidato, idEleicao);


        try {
            stmt = c.createStatement();
            String sql = "UPDATE lista_candidatos " +
                    "SET num_votos = num_votos + 1 " +
                    "WHERE eleicao_id = '" + idEleicao +"' AND id = '" + idCandidato + "'";

            myStmt = c.prepareStatement(sql);
            myStmt.executeUpdate();

            myStmt.close();
            stmt.close();
            c.commit();
        } catch (Exception e) {
            System.err.println( e.getClass().getName()+": "+ e.getMessage() );
            System.exit(0);
        }


    }

    public ResultSet getListaVotos() throws SQLClientInfoException{
        Connection c = connectDB();
        Statement stmt = null;
        PreparedStatement myStmt;
        ResultSet listaVotos = null;
        try {
            stmt = c.createStatement();
            String sql = "SELECT * FROM voto ORDER BY id_voto";

            ResultSet rs = stmt.executeQuery(sql);
            listaVotos = rs;

            //myStmt.close();
            //stmt.close();
            c.commit();

            return rs;
        } catch (Exception e) {
            System.err.println( e.getClass().getName()+": "+ e.getMessage() );
            System.exit(0);
        }
        return listaVotos;
    }

    public ResultSet consultaEleicoesPassadas(int eleicaoID) throws SQLClientInfoException{
        Connection c = connectDB();
        Statement stmt = null;
        PreparedStatement myStmt;
        ResultSet eleicaoPassada = null;
        try {
            stmt = c.createStatement();
            String sql = "SELECT * FROM lista_candidatos WHERE eleicao_id = '" + eleicaoID + "' ORDER BY id";

            ResultSet rs = stmt.executeQuery(sql);
            eleicaoPassada = rs;

            //myStmt.close();
            //stmt.close();
            c.commit();

            return rs;
        } catch (Exception e) {
            System.err.println( e.getClass().getName()+": "+ e.getMessage() );
            System.exit(0);
        }
        return eleicaoPassada;
    }

    public ResultSet gereMesadeVoto(int eleicaoID) throws SQLClientInfoException{
        Connection c = connectDB();
        Statement stmt = null;
        PreparedStatement myStmt;
        ResultSet mesasVoto = null;
        try {
            stmt = c.createStatement();
            String sql = "SELECT eleicao.id, eleicao.titulo, departamento " +
                    "FROM eleicao, departamento " +
                    "WHERE eleicao.id IN (SELECT eleicao_id  FROM departamento) " +
                    "AND eleicao.id = departamento.eleicao_id " +
                    "AND eleicao.id = '" + eleicaoID +
                    "' AND CURRENT_TIMESTAMP < data_inicio ORDER BY eleicao.id";

            ResultSet rs = stmt.executeQuery(sql);
            mesasVoto = rs;

            //myStmt.close();
            //stmt.close();
            c.commit();

            return rs;
        } catch (Exception e) {
            System.err.println( e.getClass().getName()+": "+ e.getMessage() );
            System.exit(0);
        }
        return mesasVoto;



    }

    public void removeListaDep(int opcaoEleicao) throws SQLClientInfoException {
        Connection c = connectDB();
        Statement stmt = null;
        PreparedStatement myStmt;
        try {
            stmt = c.createStatement();
            String sql = "DELETE FROM departamento " +
                    "WHERE eleicao_id = '" + opcaoEleicao + "'";
            myStmt = c.prepareStatement(sql);

            myStmt.executeUpdate();

            myStmt.close();
            stmt.close();
            c.commit();
        } catch (Exception e) {
            System.err.println( e.getClass().getName()+": "+ e.getMessage() );
            System.exit(0);
        }

    }

    public void updateListaDep(int opcaoEleicao, CopyOnWriteArrayList<String> listaDept) throws SQLClientInfoException {
        removeListaDep(opcaoEleicao);

        Connection c = connectDB();
        Statement stmt = null;
        PreparedStatement myStmt = null;

        for (int i = 0; i < listaDept.size(); i++){
            System.out.println(i + "-> " + listaDept.get(i));
        }

        int idDep = getIDMaxDep();
        System.out.println("ID MAX :" + idDep);
        try {
            for (int i = 0; i < listaDept.size(); i++){
                idDep++;
                System.out.println("ID DEP :" + idDep);
                System.out.println("->->" + i);
                stmt = c.createStatement();
                String sql = "INSERT INTO departamento (id, departamento, eleicao_id) " +
                        "VALUES(?, ?, ?)";

                myStmt = c.prepareStatement(sql);

                myStmt.setInt(1, idDep);
                myStmt.setString(2, listaDept.get(i));
                myStmt.setInt(3, opcaoEleicao);

                myStmt.executeUpdate();


            }
            myStmt.close();
            stmt.close();
            c.commit();

        } catch (Exception e) {
            System.err.println( e.getClass().getName()+": "+ e.getMessage() );
            System.exit(0);
        }

    }

    public ResultSet listaEleicoesPassadas() throws SQLClientInfoException{
        Connection c = connectDB();
        Statement stmt = null;
        PreparedStatement myStmt;
        ResultSet eleicoes = null;

        try {
            //Retorna todas as eleições a decorrer
            stmt = c.createStatement();
            String sqlIDEleicao = "SELECT eleicao.id, titulo, tipo, data_inicio, departamento " + "FROM eleicao, departamento " +
                    "WHERE eleicao.id = departamento.eleicao_id AND CURRENT_TIMESTAMP NOT BETWEEN data_inicio AND data_fim ORDER BY eleicao.id";
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

    public ResultSet getEleitoresTempoReal(int idEleicao) throws SQLClientInfoException{
        Connection c = connectDB();
        Statement stmt = null;
        PreparedStatement myStmt;
        ResultSet numVotos = null;
        try {
            stmt = c.createStatement();
            String sql = "SELECT COUNT(eleicao_id), local_voto FROM voto WHERE eleicao_id = '" + idEleicao + "' AND hora_voto IS NOT NULL GROUP BY local_voto";

            ResultSet rs = stmt.executeQuery(sql);
            numVotos = rs;
            //myStmt.close();
            //stmt.close();
            c.commit();

            return rs;
        } catch (Exception e) {
            System.err.println( e.getClass().getName()+": "+ e.getMessage() );
            System.exit(0);
        }
        return numVotos;
    }
}