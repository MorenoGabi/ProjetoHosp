package com.meuprojeto.dao.impl;

import com.meuprojeto.Database;
import com.meuprojeto.dao.PapeisDAO;
import com.meuprojeto.model.Papeis;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class PapeisDAOImpl implements PapeisDAO {
    @Override
    public Papeis findById(int id) throws Exception {
        String sql = "SELECT id, name FROM roles WHERE id = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return new Papeis(rs.getInt("id"), rs.getString("name"));
            }
        }

        return null;
    }

    @Override
    public List<Papeis> findAll() throws Exception {
        String sql = "SELECT id, name FROM roles ORDER BY name";
        List<Papeis> list = new ArrayList<>();
        try (Connection conn = Database.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(new Papeis(rs.getInt("id"), rs.getString("name")));
        }

        return List.of();
    }

    @Override
    public Papeis findBynome(String nome) throws Exception {
        String sql = "SELECT id, name FROM roles WHERE name = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nome);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return new Papeis(rs.getInt("id"), rs.getString("name"));
            }
        }

        return null;
    }

    @Override
    public void insert(Papeis p) throws Exception {
        String sql = "INSERT INTO roles (name) VALUES (?)";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, p.getNome());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) p.setId(keys.getInt(1));
            }
        }


    }

    @Override
    public void update(Papeis p) throws Exception {
        String sql = "UPDATE roles SET name = ? WHERE id = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, p.getNome());
            ps.setInt(2, p.getId());
            ps.executeUpdate();
        }

    }

    @Override
    public void delete(int id) throws Exception {
        String sql = "DELETE FROM roles WHERE id = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }

    }
}
