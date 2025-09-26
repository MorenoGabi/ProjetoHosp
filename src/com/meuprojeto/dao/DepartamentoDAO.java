package com.meuprojeto.dao;

import com.meuprojeto.model.Departamento;
import java.util.List;

public interface DepartamentoDAO {
    Departamento findById (int id) throws Exception;
    List<Departamento> findAll  () throws Exception;
    Departamento findBynome (String nome) throws Exception;
    void insert (Departamento d) throws Exception;
    void update (Departamento d) throws Exception;
    void delete (int id) throws Exception;

}
