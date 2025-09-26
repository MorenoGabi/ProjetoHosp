package com.meuprojeto.dbhandlers;

import java.sql.Connection;
import java.sql.Date; // java.sql.Date
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.DatabaseMetaData;
import java.sql.Types;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Handler que insere/atualiza/exclui registros que são "filhos" de person.
 * Fluxo insert: insere em person -> obtém person.id -> insere na tabela filha (childTable).
 */
public class PersonChildHandler implements TableHandler {
    private final String childTable;             // "patient" ou "profissionais"
    private final String pkName = "id";

    public PersonChildHandler(String childTable) {
        this.childTable = childTable;
    }

    @Override
    public Integer insert(Connection conn, List<String> columnOrder, Map<String, String> values) throws Exception {
        String sqlPerson = "INSERT INTO person (nome, cpf, data_nasc, sexo, contato, endereco) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sqlPerson, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, values.getOrDefault("nome", null));
            ps.setString(2, values.getOrDefault("cpf", null));

            Date d = parseDateNullable(values.get("data_nasc")); // java.sql.Date
            if (d == null) ps.setNull(3, Types.DATE);
            else ps.setDate(3, d);

            ps.setString(4, values.getOrDefault("sexo", null));
            ps.setString(5, values.getOrDefault("contato", null));
            ps.setString(6, values.getOrDefault("endereco", null));
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (!rs.next()) throw new SQLException("Não obteve id gerado para person.");
                int personId = rs.getInt(1);

                // montar INSERT para tabela filha (child), usando colunas descobertas dinamicamente
                List<String> childCols = discoverChildColumns(conn);
                StringBuilder cols = new StringBuilder("id");
                StringBuilder ph = new StringBuilder("?");
                List<Object> params = new ArrayList<>();
                params.add(personId);

                for (String c : childCols) {
                    cols.append(", ").append(c);
                    ph.append(", ?");
                    params.add(values.getOrDefault(c.toLowerCase(), null));
                }

                String sqlChild = "INSERT INTO " + childTable + " (" + cols + ") VALUES (" + ph + ")";
                try (PreparedStatement psChild = conn.prepareStatement(sqlChild)) {
                    for (int i = 0; i < params.size(); i++) psChild.setObject(i + 1, params.get(i));
                    psChild.executeUpdate();
                }

                return personId;
            }
        }
    }

    @Override
    public void update(Connection conn, Object idValue, List<String> columnOrder, Map<String, String> values) throws Exception {
        // update person
        String sqlP = "UPDATE person SET nome=?, cpf=?, data_nasc=?, sexo=?, contato=?, endereco=? WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sqlP)) {
            ps.setString(1, values.getOrDefault("nome", null));
            ps.setString(2, values.getOrDefault("cpf", null));
            Date d = parseDateNullable(values.get("data_nasc"));
            if (d == null) ps.setNull(3, Types.DATE);
            else ps.setDate(3, d);
            ps.setString(4, values.getOrDefault("sexo", null));
            ps.setString(5, values.getOrDefault("contato", null));
            ps.setString(6, values.getOrDefault("endereco", null));
            ps.setObject(7, idValue);
            ps.executeUpdate();
        }

        // update child: monta SET com as colunas da child
        List<String> childCols = discoverChildColumns(conn);
        if (!childCols.isEmpty()) {
            StringBuilder set = new StringBuilder();
            for (String c : childCols) {
                if (set.length() > 0) set.append(", ");
                set.append(c).append(" = ?");
            }
            String sqlC = "UPDATE " + childTable + " SET " + set + " WHERE id = ?";
            try (PreparedStatement ps = conn.prepareStatement(sqlC)) {
                int idx = 1;
                for (String c : childCols) ps.setObject(idx++, values.getOrDefault(c.toLowerCase(), null));
                ps.setObject(idx, idValue);
                ps.executeUpdate();
            }
        }
    }

    @Override
    public void delete(Connection conn, Object idValue) throws Exception {
        // remove child then person
        try (PreparedStatement ps = conn.prepareStatement("DELETE FROM " + childTable + " WHERE id = ?")) {
            ps.setObject(1, idValue);
            ps.executeUpdate();
        }
        try (PreparedStatement ps = conn.prepareStatement("DELETE FROM person WHERE id = ?")) {
            ps.setObject(1, idValue);
            ps.executeUpdate();
        }
    }

    /**
     * Descobre colunas da tabela filha (childTable), exceto 'id'.
     */
    private List<String> discoverChildColumns(Connection conn) throws SQLException {
        List<String> cols = new ArrayList<>();
        DatabaseMetaData dm = conn.getMetaData();
        try (ResultSet rs = dm.getColumns(null, null, childTable, null)) {
            while (rs.next()) {
                String name = rs.getString("COLUMN_NAME");
                if (!name.equalsIgnoreCase("id")) cols.add(name);
            }
        }
        return cols;
    }

    /**
     * Parse flexível de data: aceita "dd/MM/yyyy" ou "yyyy-MM-dd".
     * Retorna java.sql.Date ou null.
     */
    private Date parseDateNullable(String s) {
        if (s == null) return null;
        s = s.trim();
        if (s.isEmpty()) return null;
        try {
            if (s.contains("/")) {
                DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                LocalDate ld = LocalDate.parse(s, fmt);
                return Date.valueOf(ld);
            } else {
                // assume yyyy-MM-dd or yyyy-MM-ddTHH:mm... -> take date part
                String part = s.split(" ")[0];
                return Date.valueOf(part);
            }
        } catch (Exception e) {
            try {
                // última tentativa: pegar parte antes do espaço
                String part = s.split(" ")[0];
                if (part.contains("/")) {
                    DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                    LocalDate ld = LocalDate.parse(part, fmt);
                    return Date.valueOf(ld);
                } else return Date.valueOf(part);
            } catch (Exception ex) {
                return null;
            }
        }
    }
}
