package com.meuprojeto.model;
import java.time.LocalDateTime;

public class Internacoes {
    private  int id;
    private int pacienteId;
    private LocalDateTime entrada;
    private LocalDateTime saida;
    private Integer quartoId;
    private String motivo;

    public Internacoes() {
        this.id = id;
        this.pacienteId = pacienteId;
        this.entrada = entrada;
        this.saida = saida;
        this.quartoId = quartoId;
        this.motivo = motivo;

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

    public LocalDateTime getEntrada() {
        return entrada;
    }

    public void setEntrada(LocalDateTime entrada) {
        this.entrada = entrada;
    }

    public LocalDateTime getSaida() {
        return saida;
    }

    public void setSaida(LocalDateTime saida) {
        this.saida = saida;
    }

    public Integer getQuartoId() {
        return quartoId;
    }

    public void setQuartoId(Integer quartoId) {
        this.quartoId = quartoId;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }
}
