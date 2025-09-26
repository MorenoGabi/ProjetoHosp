package com.meuprojeto.dao;


import com.meuprojeto.model.Quartos;
import java.util.List;

public interface QuartoDAO {
    Quartos findById (int id) throws Exception;
    List<Quartos> findAll  () throws Exception;
    List <Quartos> findByDepartamento (int departamentoId) throws Exception;
    List <Quartos> findByTipo (String tipo) throws Exception;
    void insert (Quartos q) throws Exception;
    void update (Quartos q) throws Exception;
    void delete (int id) throws Exception;
}
