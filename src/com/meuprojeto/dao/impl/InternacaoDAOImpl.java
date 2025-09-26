package com.meuprojeto.dao.impl;

import com.meuprojeto.Database;
import com.meuprojeto.dao.InternacaoDAO;
import com.meuprojeto.model.Internacoes;

import java.sql.*;
import java.util.ArrayList;
import java.time.LocalDateTime;
import java.util.List;

public class InternacaoDAOImpl implements InternacaoDAO {
    @Override
    public Internacoes findById(int id) throws Exception {
        String sql = "SELECT id, paciente_id, entrada, saida, quarto_id, motivo FROM internacoes WHERE id = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return map (rs);
            }
        }

        return null;
    }

    @Override
    public List<Internacoes> findAll() throws Exception {
        String sql = "SELECT id, paciente_id, entrada, saida, quarto_id, motivo FROM internacoes ORDER BY entrada DESC";
        List<Internacoes> list = new ArrayList<>();
        try (Connection conn = Database.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(map(rs));
        }


        return List.of();
    }

    @Override
    public List<Internacoes> findByPaciente(int pacienteId) throws Exception {
        String sql = "SELECT id, paciente_id, entrada, saida, quarto_id, motivo FROM internacoes WHERE paciente_id = ? ORDER BY entrada DESC";
        List<Internacoes> list = new ArrayList<>();
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
    public List<Internacoes> findByActiveInternacao() throws Exception {
        String sql = "SELECT id, paciente_id, entrada, saida, quarto_id, motivo FROM internacoes WHERE saida IS NULL ORDER BY entrada";
        List<Internacoes> list = new ArrayList<>();
        try (Connection conn = Database.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(map(rs));
        }


        return List.of();
    }

    @Override
    public List<Internacoes> findByDateRange(LocalDateTime from, LocalDateTime to) throws Exception {
        String sql = "SELECT id, paciente_id, entrada, saida, quarto_id, motivo FROM internacoes WHERE entrada BETWEEN ? AND ? ORDER BY entrada";
        List<Internacoes> list = new ArrayList<>();
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setTimestamp(1, Timestamp.valueOf(from));
            ps.setTimestamp(2, Timestamp.valueOf(to));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(map(rs));
            }
        }


        return List.of();
    }

    @Override
    public void insert(Internacoes i) throws Exception {
        String sql = "INSERT INTO internacoes (paciente_id, entrada, saida, quarto_id, motivo) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, i.getPacienteId());
            ps.setTimestamp(2, Timestamp.valueOf(i.getEntrada()));
            if (i.getSaida() == null) ps.setNull(3, Types.TIMESTAMP);
            else ps.setTimestamp(3, Timestamp.valueOf(i.getSaida()));
            if (i.getQuartoId() == null) ps.setNull(4, Types.INTEGER);
            else ps.setInt(4, i.getQuartoId());
            ps.setString(5, i.getMotivo());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) i.setId(keys.getInt(1));
            }
        }



    }

    @Override
    public void update(Internacoes i) throws Exception {
        String sql = "UPDATE internacoes SET paciente_id=?, entrada=?, saida=?, quarto_id=?, motivo=? WHERE id=?";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, i.getPacienteId());
            ps.setTimestamp(2, Timestamp.valueOf(i.getEntrada()));
            if (i.getSaida() == null) ps.setNull(3, Types.TIMESTAMP);
            else ps.setTimestamp(3, Timestamp.valueOf(i.getSaida()));
            if (i.getQuartoId() == null) ps.setNull(4, Types.INTEGER);
            else ps.setInt(4, i.getQuartoId());
            ps.setString(5, i.getMotivo());
            ps.setInt(6, i.getId());
            ps.executeUpdate();
        }



    }

    @Override
    public void delete(int id) throws Exception {
        String sql = "DELETE FROM internacoes WHERE id = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }

    }
    private Internacoes map(ResultSet rs) throws SQLException {
        Internacoes i = new Internacoes();
        i.setId(rs.getInt("id"));
        i.setPacienteId(rs.getInt("paciente_id"));
        Timestamp tEntrada = rs.getTimestamp("entrada");
        if (tEntrada != null) i.setEntrada(tEntrada.toLocalDateTime());
        Timestamp tSaida = rs.getTimestamp("saida");
        if (tSaida != null) i.setSaida(tSaida.toLocalDateTime());
        i.setQuartoId(rs.getObject("room_id") == null ? null : rs.getInt("room_id"));
        i.setMotivo(rs.getString("motivo"));
        return i;
    }
}
