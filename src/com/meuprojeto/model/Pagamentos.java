package com.meuprojeto.model;
import java.math.BigDecimal;
import java.time.LocalDate;

public class Pagamentos {
    private int id;
    private int pacienteId;
    private java.math.BigDecimal valor;
    private LocalDate dataEmissao;
    private String status;

    public Pagamentos() {
        this.id = id;
        this.pacienteId = pacienteId;
        this.valor = valor;
        this.dataEmissao = dataEmissao;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPacienteId() {
        return pacienteId;
    }

    public void setPacienteId(int pacienteId) {
        this.pacienteId = pacienteId;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public LocalDate getDataEmissao() {
        return dataEmissao;
    }

    public void setDataEmissao(LocalDate dataEmissao) {
        this.dataEmissao = dataEmissao;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
