package com.meuprojeto.dao.impl;

import com.meuprojeto.Database;
import com.meuprojeto.dao.QuartoDAO;
import com.meuprojeto.model.Quartos;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class QuartoDAOImpl implements QuartoDAO {
    @Override
    public Quartos findById(int id) throws Exception {
        String sql = "SELECT id, codigo, tipo, departmento_id FROM quartos WHERE id = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Quartos(
                            rs.getInt("id"),
                            rs.getString("codigo"),
                            rs.getString("tipo"),
                            rs.getObject("departmento_id") == null ? null : rs.getInt("department_id")
                    );
                }
            }
        }


        return null;
    }

    @Override
    public List<Quartos> findAll() throws Exception {
        String sql = "SELECT id, codigo, tipo, departmento_id FROM quartos ORDER BY codigo";
        List<Quartos> list = new ArrayList<>();
        try (Connection conn = Database.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new Quartos(
                        rs.getInt("id"),
                        rs.getString("codigo"),
                        rs.getString("tipo"),
                        rs.getObject("departmento_id") == null ? null : rs.getInt("department_id")
                ));
            }
        }


        return List.of();
    }

    @Override
    public List<Quartos> findByDepartamento(int departamentoId) throws Exception {
        String sql = "SELECT id, codigo, tipo, departmento_id FROM quartos WHERE departmento_id = ? ORDER BY codigo";
        List<Quartos> list = new ArrayList<>();
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, departamentoId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new Quartos(
                            rs.getInt("id"),
                            rs.getString("codigo"),
                            rs.getString("tipo"),
                            rs.getInt("departmento_id")
                    ));
                }
            }
        }


        return List.of();
    }

    @Override
    public List<Quartos> findByTipo(String tipo) throws Exception {
        String sql = "SELECT id, codigo, tipo, departmento_id FROM quartos WHERE tipo = ? ORDER BY codigo";
        List<Quartos> list = new ArrayList<>();
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, tipo);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new Quartos(
                            rs.getInt("id"),
                            rs.getString("codigo"),
                            rs.getString("tipo"),
                            rs.getObject("departmento_id") == null ? null : rs.getInt("department_id")
                    ));
                }
            }
        }

        return List.of();
    }

    @Override
    public void insert(Quartos q) throws Exception {
        String sql = "INSERT INTO quartos (codigo, tipo, departmento_id) VALUES (?, ?, ?)";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, q.getCodigo());
            ps.setString(2, q.getTipo());
            if (q.getDepartamentoId() == null) ps.setNull(3, Types.INTEGER);
            else ps.setInt(3, q.getDepartamentoId());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) q.setId(keys.getInt(1));
            }
        }


    }

    @Override
    public void update(Quartos q) throws Exception {
        String sql = "UPDATE quartos SET codigo = ?, tipo = ?, departmento_id = ? WHERE id = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, q.getCodigo());
            ps.setString(2, q.getTipo());
            if (q.getDepartamentoId() == null) ps.setNull(3, Types.INTEGER);
            else ps.setInt(3, q.getDepartamentoId());
            ps.setInt(4, q.getId());
            ps.executeUpdate();
        }



    }

    @Override
    public void delete(int id) throws Exception {
        String sql = "DELETE FROM quartos WHERE id = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }



    }
}
