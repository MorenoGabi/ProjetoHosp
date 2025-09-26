package com.meuprojeto.model;
public class Quartos {
    private int id;
    private String codigo;
    private String tipo;
    private Integer departamentoId;

    public Quartos(int id, String codigo, String tipo, Integer departamentoId) {
        this.id = id;
        this.codigo = codigo;
        this.tipo = tipo;
        this.departamentoId = departamentoId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getTipo() {
        return tipo;
    }

    public Integer getDepartamentoId() {
        return departamentoId;
    }

    public void setDepartamentoId(Integer departamentoId) {
        this.departamentoId = departamentoId;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }
}
