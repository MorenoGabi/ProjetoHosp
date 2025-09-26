package com.meuprojeto.model;

public class Profissional extends Person {
    private String matricula;
    private String tipo;
    private String especialidade;

    public Profissional() {
        super();
        this.matricula = matricula;
        this.tipo = tipo;
        this.especialidade = especialidade;
    }

    public String getMatricula() {
        return matricula;
    }

    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getEspecialidade() {
        return especialidade;
    }

    public void setEspecialidade(String especialidade) {
        this.especialidade = especialidade;
    }
}
