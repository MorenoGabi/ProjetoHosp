package com.meuprojeto.dao.impl;

import com.meuprojeto.Database;
import com.meuprojeto.dao.PrescricaoDAO;
import com.meuprojeto.model.Prescricao;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class PrescricaoDAOImpl implements PrescricaoDAO {
    @Override
    public Prescricao findById(int id) throws Exception {
        String sql = "SELECT id, agendamento_id, descricao, created_at FROM prescricao WHERE id = ?";
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
    public List<Prescricao> findAll() throws Exception {
        String sql = "SELECT id, agendamento_id, descricao, created_at FROM prescricao ORDER BY created_at DESC";
        List<Prescricao> list = new ArrayList<>();
        try (Connection conn = Database.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(map(rs));
        }

        return List.of();
    }

    @Override
    public List<Prescricao> findByAgendamento(int agendamentoId) throws Exception {
        String sql = "SELECT id, agendamento_id, descricao, created_at FROM prescricao WHERE agendamento_id = ? ORDER BY created_at DESC";
        List<Prescricao> list = new ArrayList<>();
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, agendamentoId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(map(rs));
            }
        }

        return List.of();
    }

    @Override
    public List<Prescricao> findByPaciente(int pacienteId) throws Exception {
        String sql = "SELECT pr.id, pr.agendamento_id, pr.descricao, pr.created_at " +
                "FROM prescricao pr " +
                "JOIN agendamentos ap ON pr.agendamento_id = ap.id " +
                "WHERE ap.paciente_id = ? ORDER BY pr.created_at DESC";
        List<Prescricao> list = new ArrayList<>();
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
    public void insert(Prescricao p) throws Exception {
        String sql = "INSERT INTO prescricao (agendamento_id, descricao, created_at) VALUES (?, ?, ?)";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            if (p.getAgendamentoId() == null) ps.setNull(1, Types.INTEGER);
            else ps.setInt(1, p.getAgendamentoId());
            ps.setString(2, p.getDescricao());
            if (p.getCreateAt()== null) ps.setTimestamp(3, new Timestamp(System.currentTimeMillis()));
            else ps.setTimestamp(3, Timestamp.valueOf(p.getCreateAt()));
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) p.setId(keys.getInt(1));
            }
        }


    }

    @Override
    public void update(Prescricao p) throws Exception {
        String sql = "UPDATE prescricao SET agendamento_id = ?, descricao = ?, created_at = ? WHERE id = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            if (p.getAgendamentoId() == null) ps.setNull(1, Types.INTEGER);
            else ps.setInt(1, p.getAgendamentoId());
            ps.setString(2, p.getDescricao());
            if (p.getCreateAt() == null) ps.setTimestamp(3, new Timestamp(System.currentTimeMillis()));
            else ps.setTimestamp(3, Timestamp.valueOf(p.getCreateAt()));
            ps.setInt(4, p.getId());
            ps.executeUpdate();
        }


    }

    @Override
    public void delete(int id) throws Exception {
        String sql = "DELETE FROM prescricao WHERE id = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    private Prescricao map(ResultSet rs) throws SQLException {
        Prescricao p = new Prescricao();
        p.setId(rs.getInt("id"));
        p.setAgendamentoId(rs.getObject("agendamento_id") == null ? null : rs.getInt("agendamento_id"));
        p.setDescricao(rs.getString("descricao"));
        Timestamp ts = rs.getTimestamp("created_at");
        if (ts != null) p.setCreateAt(ts.toLocalDateTime());
        return p;
    }
}
