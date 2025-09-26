package com.meuprojeto.dao;

import com.meuprojeto.model.Pagamentos;

import java.time.LocalDate;
import java.util.List;

public interface PagamentoDAO {
    Pagamentos findById (int id) throws Exception;
    List<Pagamentos> findAll  () throws Exception;
    List <Pagamentos> findByPaciente (int pacienteId) throws Exception;
    List <Pagamentos> findByStatus (String status) throws Exception;
    List <Pagamentos> findByDate (LocalDate from, LocalDate to) throws Exception;
    void insert (Pagamentos p) throws Exception;
    void update (Pagamentos p) throws Exception;
    void delete (int id) throws Exception;
}
