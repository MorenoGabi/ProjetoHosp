package com.meuprojeto.dao;
import com.meuprojeto.model.Paciente;
import java.util.List;
import java.util.prefs.PreferenceChangeEvent;

public interface PacienteDAO {
    Paciente findById (int id) throws Exception;
    List <Paciente> findAll  () throws Exception;
    List <Paciente> findBynome (String nomePattern) throws Exception;
    void insert (Paciente p) throws Exception;
    void update (Paciente p) throws Exception;
    void delete (int id) throws Exception;



}
