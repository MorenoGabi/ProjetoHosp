package com.meuprojeto.dao;

import com.meuprojeto.model.Agendamento;

import java.time.LocalDateTime;
import java.util.List;

public interface AgendamentoDAO {
    Agendamento findById (int id) throws Exception;
    List<Agendamento> findAll  () throws Exception;
    List <Agendamento> findByPaciente (int pacienteId) throws Exception;
    List <Agendamento> findByProfissional (int profissionalId) throws Exception;
    List <Agendamento> findByDataRange (LocalDateTime from, LocalDateTime to) throws Exception;
    void insert (Agendamento a) throws Exception;
    void update (Agendamento a) throws Exception;
    void delete (int id) throws Exception;
}
