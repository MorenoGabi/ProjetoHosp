package com.meuprojeto.dao;

import com.meuprojeto.model.Profissional;
import java.util.List;

public interface ProfissionalDAO {
    Profissional findById (int id) throws Exception;
    List <Profissional> findAll  () throws Exception;
    List <Profissional> findByEspecialidade (String especialidade) throws Exception;
    void insert (Profissional p) throws Exception;
    void update (Profissional p) throws Exception;
    void delete (int id) throws Exception;
}
