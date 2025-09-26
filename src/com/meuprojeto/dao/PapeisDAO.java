package com.meuprojeto.dao;

import com.meuprojeto.model.Papeis;
import java.util.List;

public interface PapeisDAO {
    Papeis findById (int id) throws Exception;
    List<Papeis> findAll  () throws Exception;
    Papeis findBynome (String nome) throws Exception;
    void insert (Papeis p) throws Exception;
    void update (Papeis p) throws Exception;
    void delete (int id) throws Exception;
}
