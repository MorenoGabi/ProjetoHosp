package com.meuprojeto.model;
import java.time.LocalDate;

public class Person {
    protected int id;
    protected String nome;
    protected  String cpf;
    protected String sexo;
    protected LocalDate dataNasc;
    protected String contato;
    protected String endereco;

    public Person() {
    }

    public Person(int id, String nome, String cpf, LocalDate dataNascimento, String sexo, String contato, String endereco) {
        this.id = this.id;
        this.nome = this.nome;
        this.cpf = this.cpf;
        this.sexo = this.sexo;
        this.dataNasc = dataNasc;
        this.contato = this.contato;
        this.endereco = this.endereco;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getSexo() {
        return sexo;
    }

    public void setSexo(String sexo) {
        this.sexo = sexo;
    }

    public LocalDate getDataNasc() {
        return dataNasc;
    }

    public void setDataNasc(LocalDate dataNasc) {
        this.dataNasc = dataNasc;
    }

    public String getContato() {
        return contato;
    }

    public void setContato(String contato) {
        this.contato = contato;
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }
}
