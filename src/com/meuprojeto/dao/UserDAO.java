package com.meuprojeto.dao;

import com.meuprojeto.model.User;
import java.util.List;

public interface UserDAO {
    User findById (int id) throws Exception;
    List<User> findAll  () throws Exception;
    User findByUsername (String username) throws Exception;
    List <User> findByPapeisId (int papeisId) throws Exception;
    void insert (User u) throws Exception;
    void update (User u) throws Exception;
    void delete (int id) throws Exception;
}
