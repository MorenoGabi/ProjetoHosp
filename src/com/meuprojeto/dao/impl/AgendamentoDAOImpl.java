package com.meuprojeto.dao.impl;

import com.meuprojeto.Database;
import com.meuprojeto.dao.AgendamentoDAO;
import com.meuprojeto.model.Agendamento;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class AgendamentoDAOImpl implements AgendamentoDAO {
    @Override
    public Agendamento findById(int id) throws Exception {
        String sql = "SELECT id, paciente_id, profissional_id, data_hora, motivo, status FROM agendamentos WHERE id = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSet(rs);
                }
            }
        }

        return null;
    }

    @Override
    public List<Agendamento> findAll() throws Exception {
        String sql = "SELECT id, paciente_id, profissional_id, data_hora, motivo, status FROM agendamentos ORDER BY data_hora";
        List<Agendamento> list = new ArrayList<>();
        try (Connection conn = Database.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(mapResultSet(rs));
            }
        }
        return List.of();
    }

    @Override
    public List<Agendamento> findByPaciente(int pacienteId) throws Exception {
        String sql = "SELECT id, paciente_id, profissional_id, data_hora, motivo, status FROM agendamentos WHERE paciente_id = ? ORDER BY data_hora";
        List<Agendamento> list = new ArrayList<>();
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, pacienteId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSet(rs));
                }
            }
        }

        return List.of();
    }

    @Override
    public List<Agendamento> findByProfissional(int profissionalId) throws Exception {
        return List.of();
    }

    @Override
    public List<Agendamento> findByDataRange(LocalDateTime from, LocalDateTime to) throws Exception {
        return List.of();
    }

    @Override
    public void insert(Agendamento a) throws Exception {
        String sql = "INSERT INTO agendamentos (paciente_id, profissional_id, data_hora, motivo, status) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, a.getPacienteId());
            ps.setInt(2, a.getProfissionalId());
            ps.setTimestamp(3, Timestamp.valueOf(a.getDataHora()));
            ps.setString(4, a.getMotivo());
            ps.setString(5, a.getStatus());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) a.setId(keys.getInt(1));
            }
        }



    }

    @Override
    public void update(Agendamento a) throws Exception {
        String sql = "UPDATE agendamentos SET paciente_id=?, profissional_id=?, data_hora=?, motivo=?, status=? WHERE id=?";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, a.getPacienteId());
            ps.setInt(2, a.getProfissionalId());
            ps.setTimestamp(3, Timestamp.valueOf(a.getDataHora()));
            ps.setString(4, a.getMotivo());
            ps.setString(5, a.getStatus());
            ps.setInt(6, a.getId());
            ps.executeUpdate();
        }



    }

    @Override
    public void delete(int id) throws Exception {
        String sql = "DELETE FROM agendamentos WHERE id = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }

    }
    private Agendamento mapResultSet(ResultSet rs) throws SQLException {
        Agendamento a = new Agendamento();
        a.setId(rs.getInt("id"));
        a.setPacienteId(rs.getInt("paciente_id"));
        a.setProfissionalId(rs.getInt("profissional_id"));
        Timestamp ts = rs.getTimestamp("data_hora");
        if (ts != null) a.setDataHora(LocalDateTime.from(LocalDate.from(ts.toLocalDateTime())));
        a.setMotivo(rs.getString("motivo"));
        a.setStatus(rs.getString("status"));
        return a;
    }
}
