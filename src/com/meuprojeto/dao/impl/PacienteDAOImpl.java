package com.meuprojeto.dao.impl;

import com.meuprojeto.Database;
import com.meuprojeto.dao.PacienteDAO;
import com.meuprojeto.model.Paciente;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PacienteDAOImpl implements PacienteDAO {

    @Override
    public Paciente findById(int id) throws Exception {
        String sql = "SELECT p.id, p.nome, p.cpf, p.data_nasc, p.sexo, p.contato, p.endereco, " +
                "pt.num_pront, pt.alergias, pt.observacoes " +
                "FROM person p JOIN patient pt ON p.id = pt.id WHERE p.id = ?";

        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToPaciente(rs);
                }
            }
        }
        return null;
    }

    @Override
    public List<Paciente> findAll() throws Exception {
        String sql = "SELECT p.id, p.nome, p.cpf, p.data_nasc, p.sexo, p.contato, p.endereco, " +
                "pt.num_pront, pt.alergias, pt.observacoes " +
                "FROM person p JOIN patient pt ON p.id = pt.id ORDER BY p.nome";

        List<Paciente> list = new ArrayList<>();
        try (Connection conn = Database.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                Paciente p = mapResultSetToPaciente(rs);
                list.add(p);
            }
        }
        return list;
    }

    @Override
    public List<Paciente> findBynome(String nomePattern) throws Exception {
        return List.of();
    }

    @Override
    public void insert(Paciente p) throws Exception {
        String sqlPerson = "INSERT INTO person (nome, cpf, data_nasc, sexo, contato, endereco) VALUES (?, ?, ?, ?, ?, ?)";
        String sqlPatient = "INSERT INTO patient (id, num_pront, alergias, observacoes) VALUES (?, ?, ?, ?)";

        try (Connection conn = Database.getConnection()) {
            conn.setAutoCommit(false); // inicia transação
            try (PreparedStatement psPerson = conn.prepareStatement(sqlPerson, Statement.RETURN_GENERATED_KEYS)) {

                // Configura campos do person
                psPerson.setString(1, p.getNome() != null ? p.getNome() : "");
                psPerson.setString(2, p.getCpf() != null ? p.getCpf() : "");

                if (p.getDataNasc() != null) {
                    psPerson.setDate(3, Date.valueOf(p.getDataNasc()));
                } else {
                    psPerson.setNull(3, Types.DATE);
                }

                psPerson.setString(4, p.getSexo() != null ? p.getSexo() : "");
                psPerson.setString(5, p.getContato() != null ? p.getContato() : "");
                psPerson.setString(6, p.getEndereco() != null ? p.getEndereco() : "");

                psPerson.executeUpdate();

                try (ResultSet keys = psPerson.getGeneratedKeys()) {
                    if (keys.next()) {
                        int newId = keys.getInt(1); // ID gerado do person
                        p.setId(newId); // seta no objeto paciente

                        try (PreparedStatement psPatient = conn.prepareStatement(sqlPatient)) {
                            psPatient.setInt(1, newId);
                            psPatient.setString(2, p.getNumeroPront() != null ? p.getNumeroPront() : "");
                            psPatient.setString(3, p.getAlergias() != null ? p.getAlergias() : "");
                            psPatient.setString(4, p.getObservacao() != null ? p.getObservacao() : "");
                            psPatient.executeUpdate();
                        }

                        conn.commit(); // confirma transação
                        System.out.println("Paciente inserido com ID: " + newId);

                    } else {
                        conn.rollback();
                        throw new SQLException("Falha ao obter ID gerado para person.");
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
    public void update(Paciente p) throws Exception {
        String sqlPerson = "UPDATE person SET nome=?, cpf=?, data_nasc=?, sexo=?, contato=?, endereco=? WHERE id=?";
        String sqlPatient = "UPDATE patient SET num_pront=?, alergias=?, observacoes=? WHERE id=?";

        try (Connection conn = Database.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement psPerson = conn.prepareStatement(sqlPerson);
                 PreparedStatement psPatient = conn.prepareStatement(sqlPatient)) {

                psPerson.setString(1, p.getNome() != null ? p.getNome() : "");
                psPerson.setString(2, p.getCpf() != null ? p.getCpf() : "");
                if (p.getDataNasc() != null) psPerson.setDate(3, Date.valueOf(p.getDataNasc()));
                else psPerson.setNull(3, Types.DATE);
                psPerson.setString(4, p.getSexo() != null ? p.getSexo() : "");
                psPerson.setString(5, p.getContato() != null ? p.getContato() : "");
                psPerson.setString(6, p.getEndereco() != null ? p.getEndereco() : "");
                psPerson.setInt(7, p.getId());
                psPerson.executeUpdate();

                psPatient.setString(1, p.getNumeroPront() != null ? p.getNumeroPront() : "");
                psPatient.setString(2, p.getAlergias() != null ? p.getAlergias() : "");
                psPatient.setString(3, p.getObservacao() != null ? p.getObservacao() : "");
                psPatient.setInt(4, p.getId());
                psPatient.executeUpdate();

                conn.commit();
                System.out.println("Paciente atualizado com ID: " + p.getId());

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
        String sqlPatient = "DELETE FROM patient WHERE id = ?";
        String sqlPerson = "DELETE FROM person WHERE id = ?";

        try (Connection conn = Database.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement psPatient = conn.prepareStatement(sqlPatient);
                 PreparedStatement psPerson = conn.prepareStatement(sqlPerson)) {

                psPatient.setInt(1, id);
                psPatient.executeUpdate();

                psPerson.setInt(1, id);
                psPerson.executeUpdate();

                conn.commit();
                System.out.println("Paciente deletado com ID: " + id);

            } catch (Exception ex) {
                conn.rollback();
                throw ex;
            } finally {
                conn.setAutoCommit(true);
            }
        }
    }

    // Helper: converte ResultSet para Paciente
    private Paciente mapResultSetToPaciente(ResultSet rs) throws SQLException {
        Paciente p = new Paciente();
        p.setId(rs.getInt("id"));
        p.setNome(rs.getString("nome"));
        p.setCpf(rs.getString("cpf"));
        Date d = rs.getDate("data_nasc");
        if (d != null) p.setDataNasc(d.toLocalDate());
        p.setSexo(rs.getString("sexo"));
        p.setContato(rs.getString("contato"));
        p.setEndereco(rs.getString("endereco"));
        p.setNumeroPront(rs.getString("num_pront"));
        p.setAlergias(rs.getString("alergias"));
        p.setObservacao(rs.getString("observacoes"));
        return p;
    }
}
