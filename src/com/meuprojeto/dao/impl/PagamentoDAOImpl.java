package com.meuprojeto.dao.impl;

import com.meuprojeto.Database;
import com.meuprojeto.dao.PagamentoDAO;
import com.meuprojeto.model.Pagamentos;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PagamentoDAOImpl implements PagamentoDAO{
    @Override
    public Pagamentos findById(int id) throws Exception {
        String sql = "SELECT id, paciente_id, valor, data_emissao, status FROM faturamento WHERE id = ?";
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
    public List<Pagamentos> findAll() throws Exception {
        String sql = "SELECT id, paciente_id, valor, data_emissao, status FROM faturamento ORDER BY data_emissao DESC";
        List<Pagamentos> list = new ArrayList<>();
        try (Connection conn = Database.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(map(rs));
        }
        return List.of();
    }

    @Override
    public List<Pagamentos> findByPaciente(int pacienteId) throws Exception {
        String sql = "SELECT id, paciente_id, valor, data_emissao, status FROM faturamento WHERE paciente_id = ? ORDER BY data_emissao DESC";
        List<Pagamentos> list = new ArrayList<>();
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, pacienteId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(map(rs));
            }
        }

        return List.of();
    }

    @Override
    public List<Pagamentos> findByStatus(String status) throws Exception {
        String sql = "SELECT id, paciente_id, valor, data_emissao, status FROM faturamento WHERE status = ? ORDER BY data_emissao DESC";
        List<Pagamentos> list = new ArrayList<>();
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(map(rs));
            }
        }

        return List.of();
    }

    @Override
    public List<Pagamentos> findByDate(LocalDate from, LocalDate to) throws Exception {
        String sql = "SELECT id, paciente_id, valor, data_emissao, status FROM faturamento WHERE data_emissao BETWEEN ? AND ? ORDER BY data_emissao DESC";
        List<Pagamentos> list = new ArrayList<>();
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(from));
            ps.setDate(2, Date.valueOf(to));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(map(rs));
            }
        }

        return List.of();
    }

    @Override
    public void insert(Pagamentos p) throws Exception {
        String sql = "INSERT INTO faturamento (paciente_id, valor, data_emissao, status) VALUES (?, ?, ?, ?)";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, p.getPacienteId());
            ps.setBigDecimal(2, p.getValor());
            ps.setDate(3, Date.valueOf(p.getDataEmissao()));
            ps.setString(4, p.getStatus());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) p.setId(keys.getInt(1));
            }
        }
    }

    @Override
    public void update(Pagamentos p) throws Exception {
        String sql = "UPDATE faturamento SET paciente_id=?, valor=?, data_emissao=?, status=? WHERE id=?";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, p.getPacienteId());
            ps.setBigDecimal(2, p.getValor());
            ps.setDate(3, Date.valueOf(p.getDataEmissao()));
            ps.setString(4, p.getStatus());
            ps.setInt(5, p.getId());
            ps.executeUpdate();
        }


    }

    @Override
    public void delete(int id) throws Exception {
        String sql = "DELETE FROM faturamento WHERE id = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    private Pagamentos map(ResultSet rs) throws SQLException {
        Pagamentos p = new Pagamentos();
        p.setId(rs.getInt("id"));
        p.setPacienteId(rs.getInt("paciente_id"));
        p.setValor(rs.getBigDecimal("valor"));
        Date d = rs.getDate("data_emissao");
        if (d != null) p.setDataEmissao(d.toLocalDate());
        p.setStatus(rs.getString("status"));
        return p;
    }
}
