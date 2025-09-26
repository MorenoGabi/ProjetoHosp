package com.meuprojeto.dao.impl;

import com.meuprojeto.Database;
import com.meuprojeto.dao.DepartamentoDAO;
import com.meuprojeto.model.Departamento;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DepartamentoDAOImpl implements DepartamentoDAO{
    @Override
    public Departamento findById(int id) throws Exception {
        String sql = "SELECT id, nome FROM departmento WHERE id = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Departamento(rs.getInt("id"), rs.getString("nome"));
                }
            }
        }


        return null;
    }

    @Override
    public List<Departamento> findAll() throws Exception {
        String sql = "SELECT id, nome FROM departmento ORDER BY nome";
        List<Departamento> list = new ArrayList<>();
        try (Connection conn = Database.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new Departamento(rs.getInt("id"), rs.getString("nome")));
            }
        }


        return List.of();
    }

    @Override
    public Departamento findBynome(String nome) throws Exception {
        String sql = "SELECT id, nome FROM departmento WHERE nome = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nome);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return new Departamento(rs.getInt("id"), rs.getString("nome"));
            }
        }

        return null;
    }

    @Override
    public void insert(Departamento d) throws Exception {
        String sql = "INSERT INTO departmento (nome) VALUES (?)";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, d.getNome());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) d.setId(keys.getInt(1));
            }
        }



    }

    @Override
    public void update(Departamento d) throws Exception {
        String sql = "UPDATE departmento SET nome = ? WHERE id = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, d.getNome());
            ps.setInt(2, d.getId());
            ps.executeUpdate();
        }


    }

    @Override
    public void delete(int id) throws Exception {
        String sql = "DELETE FROM departmento WHERE id = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

}

