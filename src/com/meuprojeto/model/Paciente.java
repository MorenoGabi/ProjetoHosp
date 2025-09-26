package com.meuprojeto.model;

public class Paciente extends Person {
    private String numeroPront;
    private String alergias;
    private String observacao;




    public Paciente() {
        super();
        this.numeroPront = numeroPront;
        this.alergias = alergias;
        this.observacao = observacao;
    }

    public String getNumeroPront() {
        return numeroPront;
    }

    public void setNumeroPront(String numeroPront) {
        this.numeroPront = numeroPront;
    }

    public String getAlergias() {
        return alergias;
    }

    public void setAlergias(String alergias) {
        this.alergias = alergias;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }
}


