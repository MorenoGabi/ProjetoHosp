package com.meuprojeto.dao.impl;

import com.meuprojeto.Database;
import com.meuprojeto.dao.ProfissionalDAO;
import com.meuprojeto.model.Profissional;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


public class ProfissionalDAOImpl implements ProfissionalDAO {

    @Override
    public Profissional findById(int id) throws Exception {
        String sql = "SELECT p.id, p.nome, p.cpf, p.data_nasc, p.sexo, p.contato, p.endereco, " +
                "pr.matricula, pr.tipo, pr.especialidade " +
                "FROM person p JOIN profissional pr ON p.id = pr.id WHERE p.id = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Profissional pr = new Profissional();
                    pr.setId(rs.getInt("id"));
                    pr.setNome(rs.getString("nome"));
                    pr.setCpf(rs.getString("cpf"));
                    Date d = rs.getDate("data_nasc");
                    if (d != null) pr.setDataNasc(d.toLocalDate());
                    pr.setSexo(rs.getString("sexo"));
                    pr.setContato(rs.getString("contato"));
                    pr.setEndereco(rs.getString("endereco"));
                    pr.setMatricula(rs.getString("matricula"));
                    pr.setTipo(rs.getString("tipo"));
                    pr.setEspecialidade(rs.getString("especialidade"));
                    return pr;
                }
            }
        }

        return null;
    }

    @Override
    public List<Profissional> findAll() throws Exception {
        String sql = "SELECT p.id, p.nome, pr.matricula, pr.tipo, pr.especialidade " +
                "FROM person p JOIN profissional pr ON p.id = pr.id ORDER BY p.nome";
        List<Profissional> list = new ArrayList<>();
        try (Connection conn = Database.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                Profissional pr = new Profissional();
                pr.setId(rs.getInt("id"));
                pr.setNome(rs.getString("nome"));
                pr.setMatricula(rs.getString("matricula"));
                pr.setTipo(rs.getString("tipo"));
                pr.setEspecialidade(rs.getString("especialidade"));
                list.add(pr);
            }
        }

        return List.of();
    }

    @Override
    public List<Profissional> findByEspecialidade(String especialidade) throws Exception {
        return List.of();
    }

    @Override
    public void insert(Profissional p) throws Exception {
        String sqlPerson = "INSERT INTO person (nome, cpf, data_nasc, sexo, contato, endereco) VALUES (?,?,?,?,?,?)";
        String sqlProf = "INSERT INTO profissional (id, matricula, tipo, especialidade) VALUES (?, ?, ?, ?)";
        try (Connection conn = Database.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement ps1 = conn.prepareStatement(sqlPerson, Statement.RETURN_GENERATED_KEYS)) {
                ps1.setString(1, p.getNome());
                ps1.setString(2, p.getCpf());
                if (p.getDataNasc() != null) ps1.setDate(3, Date.valueOf(p.getDataNasc()));
                else ps1.setNull(3, Types.DATE);
                ps1.setString(4, p.getSexo());
                ps1.setString(5, p.getContato());
                ps1.setString(6, p.getEndereco());
                ps1.executeUpdate();

                try (ResultSet keys = ps1.getGeneratedKeys()) {
                    if (keys.next()) {
                        int newId = keys.getInt(1);
                        try (PreparedStatement ps2 = conn.prepareStatement(sqlProf)) {
                            ps2.setInt(1, newId);
                            ps2.setString(2, p.getMatricula());
                            ps2.setString(3, p.getTipo());
                            ps2.setString(4, p.getEspecialidade());
                            ps2.executeUpdate();
                        }
                        conn.commit();
                        p.setId(newId);
                        return;
                    } else {
                        conn.rollback();
                        throw new SQLException("Não foi possível obter ID gerado (person).");
                    }
                }
            } catch (Exception ex) {
                conn.rollback();
                throw ex;
            } finally {
                conn.setAutoCommit(true);
            }
        }
    }

    @Override
    public void update(Profissional p) throws Exception {
        String sqlPerson = "UPDATE person SET nome=?, cpf=?, data_nasc=?, sexo=?, contato=?, endereco=? WHERE id=?";
        String sqlProf = "UPDATE profissional SET matricula=?, tipo=?, especialidade=? WHERE id=?";
        try (Connection conn = Database.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement ps1 = conn.prepareStatement(sqlPerson);
                 PreparedStatement ps2 = conn.prepareStatement(sqlProf)) {

                ps1.setString(1, p.getNome());
                ps1.setString(2, p.getCpf());
                if (p.getDataNasc() != null) ps1.setDate(3, Date.valueOf(p.getDataNasc()));
                else ps1.setNull(3, Types.DATE);
                ps1.setString(4, p.getSexo());
                ps1.setString(5, p.getContato());
                ps1.setString(6, p.getEndereco());
                ps1.setInt(7, p.getId());
                ps1.executeUpdate();

                ps2.setString(1, p.getMatricula());
                ps2.setString(2, p.getTipo());
                ps2.setString(3, p.getEspecialidade());
                ps2.setInt(4, p.getId());
                ps2.executeUpdate();

                conn.commit();
            } catch (Exception ex) {
                conn.rollback();
                throw ex;
            } finally {
                conn.setAutoCommit(true);
            }
        }


    }

    @Override
    public void delete(int id) throws Exception {
        String sql = "DELETE FROM person WHERE id = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }


    }
}
