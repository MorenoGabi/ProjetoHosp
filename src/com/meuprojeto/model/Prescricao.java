package com.meuprojeto.model;
import java.time.LocalDateTime;

public class Prescricao {
    private int id;
    private Integer agendamentoId;
    private String descricao;
    private LocalDateTime createAt;

    public Prescricao() {
        this.id = id;
        this.agendamentoId = agendamentoId;
        this.descricao = descricao;
        this.createAt = createAt;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Integer getAgendamentoId() {
        return agendamentoId;
    }

    public void setAgendamentoId(Integer agendamentoId) {
        this.agendamentoId = agendamentoId;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public LocalDateTime getCreateAt() {
        return createAt;
    }

    public void setCreateAt(LocalDateTime createAt) {
        this.createAt = createAt;
    }
}
