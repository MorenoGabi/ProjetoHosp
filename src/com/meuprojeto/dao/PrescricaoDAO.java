package com.meuprojeto.dao;

import com.meuprojeto.model.Prescricao;
import java.util.List;

public interface PrescricaoDAO {
    Prescricao findById (int id) throws Exception;
    List<Prescricao> findAll  () throws Exception;
    List <Prescricao> findByAgendamento (int agendamentoId) throws Exception;
    List <Prescricao> findByPaciente (int pacienteId) throws Exception;
    void insert (Prescricao p) throws Exception;
    void update (Prescricao p) throws Exception;
    void delete (int id) throws Exception;
}