package com.company;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.CopyOnWriteArrayList;

public class Eleicao implements Serializable{
    private String data_inicio;
    private String data_fim;
    private String titulo;
    private String descricao;
    private String tipoEleicao;
    private String departamento;
    private CopyOnWriteArrayList<Candidato> listaCandidatos;
    private int resultado;

    public Eleicao(String data_inicio, String data_fim, String titulo, String descricao, String tipoEleicao, String departamento, CopyOnWriteArrayList<Candidato> listaCandidatos, int resultado) {
        this.data_inicio = data_inicio;
        this.data_fim = data_fim;
        this.titulo = titulo;
        this.descricao = descricao;
        this.tipoEleicao = tipoEleicao;
        this.departamento = departamento;
        this.listaCandidatos = listaCandidatos;
        this.resultado = resultado;
    }

    public String getData_inicio() {
        return data_inicio;
    }

    public void setData_inicio(String data_inicio) {
        this.data_inicio = data_inicio;
    }

    public String getData_fim() {
        return data_fim;
    }

    public void setData_fim(String data_fim) {
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

    public CopyOnWriteArrayList<Candidato> getListaCandidatos() {
        return listaCandidatos;
    }

    public void setListaCandidatos(CopyOnWriteArrayList<Candidato> listaCandidatos) {
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