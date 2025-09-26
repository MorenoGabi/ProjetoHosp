package com.meuprojeto.dao.impl;

import com.meuprojeto.Database;
import com.meuprojeto.dao.UserDAO;
import com.meuprojeto.model.User;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class UserDAOImpl implements UserDAO {
    @Override
    public User findById(int id) throws Exception {
        String sql = "SELECT id, username, password_hash, role_id, created_at FROM users WHERE id = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return map(rs);
            }
        }

        return null;
    }

    @Override
    public List<User> findAll() throws Exception {
        String sql = "SELECT id, username, password_hash, role_id, created_at FROM users ORDER BY username";
        List<User> list = new ArrayList<>();
        try (Connection conn = Database.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(map(rs));
        }

        return List.of();
    }

    @Override
    public User findByUsername(String username) throws Exception {
        String sql = "SELECT id, username, password_hash, role_id, created_at FROM users WHERE username = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return map(rs);
            }
        }

        return null;
    }

    @Override
    public List<User> findByPapeisId(int papeisId) throws Exception {
        String sql = "SELECT id, username, password_hash, role_id, created_at FROM users WHERE role_id = ? ORDER BY username";
        List<User> list = new ArrayList<>();
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, papeisId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(map(rs));
            }
        }

        return List.of();
    }

    @Override
    public void insert(User u) throws Exception {
        String sql = "INSERT INTO users (username, password_hash, role_id, created_at) VALUES (?, ?, ?, ?)";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, u.getUsername());
            ps.setString(2, u.getPasswordHash());
            if (u.getPapeisId() == null) ps.setNull(3, Types.INTEGER);
            else ps.setInt(3, u.getPapeisId());
            if (u.getCreatedAt() == null) ps.setTimestamp(4, Timestamp.valueOf(LocalDateTime.now()));
            else ps.setTimestamp(4, Timestamp.valueOf(u.getCreatedAt()));
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) u.setId(keys.getInt(1));
            }
        }

    }

    @Override
    public void update(User u) throws Exception {
        String sql = "UPDATE users SET username = ?, password_hash = ?, role_id = ? WHERE id = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, u.getUsername());
            ps.setString(2, u.getPasswordHash());
            if (u.getPapeisId() == null) ps.setNull(3, Types.INTEGER);
            else ps.setInt(3, u.getPapeisId());
            ps.setInt(4, u.getId());
            ps.executeUpdate();
        }

    }

    @Override
    public void delete(int id) throws Exception {
        String sql = "DELETE FROM users WHERE id = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }

    }

    private User map(ResultSet rs) throws SQLException {
        User u = new User();
        u.setId(rs.getInt("id"));
        u.setUsername(rs.getString("username"));
        u.setPasswordHash(rs.getString("password_hash"));
        u.setPapeisId(rs.getObject("role_id") == null ? null : rs.getInt("role_id"));
        Timestamp ts = rs.getTimestamp("created_at");
        if (ts != null) u.setCreatedAt(ts.toLocalDateTime());
        return u;
    }

    public User authenticate(String username, String password) {
        return null;
    }
}


