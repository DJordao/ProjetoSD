package com.company;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

public class Eleicao implements Serializable{
    private Date data_inicio;
    private Date data_fim;
    private String titulo;
    private String descricao;
    private String tipoEleicao;
    private String departamento;
    private ArrayList<Candidato> listaCandidatos;
    private int resultado;

    public Eleicao(Date data_inicio, Date data_fim, String titulo, String descricao, String tipoEleicao, String departamento, ArrayList<Candidato> listaCandidatos, int resultado) {
        this.data_inicio = data_inicio;
        this.data_fim = data_fim;
        this.titulo = titulo;
        this.descricao = descricao;
        this.tipoEleicao = tipoEleicao;
        this.departamento = departamento;
        this.listaCandidatos = listaCandidatos;
        this.resultado = resultado;
    }

    public Date getData_inicio() {
        return data_inicio;
    }

    public void setData_inicio(Date data_inicio) {
        this.data_inicio = data_inicio;
    }

    public Date getData_fim() {
        return data_fim;
    }

    public void setData_fim(Date data_fim) {
        this.data_fim = data_fim;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getTipoEleicao() {
        return tipoEleicao;
    }

    public void setTipoEleicao(String tipoEleicao) {
        this.tipoEleicao = tipoEleicao;
    }

    public String getDepartamento() {
        return departamento;
    }

    public void setDepartamento(String departamento) {
        this.departamento = departamento;
    }

    public ArrayList<Candidato> getListaCandidatos() {
        return listaCandidatos;
    }

    public void setListaCandidatos(ArrayList<Candidato> listaCandidatos) {
        this.listaCandidatos = listaCandidatos;
    }

    public int getResultado() {
        return resultado;
    }

    public void setResultado(int resultado) {
        this.resultado = resultado;
    }

    @Override
    public String toString() {
        return "Eleicao{" +
                "data_inicio=" + data_inicio +
                ", data_fim=" + data_fim +
                ", titulo='" + titulo + '\'' +
                ", descricao='" + descricao + '\'' +
                ", tipoEleicao='" + tipoEleicao + '\'' +
                ", departamento='" + departamento + '\'' +
                ", listaCandidatos=" + listaCandidatos +
                ", resultado=" + resultado +
                '}';
    }
}