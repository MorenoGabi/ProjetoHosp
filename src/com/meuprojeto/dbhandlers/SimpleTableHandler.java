package com.meuprojeto.dbhandlers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SimpleTableHandler implements TableHandler {
    private final String tableName;
    private final String pkName;

    public SimpleTableHandler(String tableName, String pkName) {
        this.tableName = tableName;
        this.pkName = pkName;
    }

    @Override
    public Integer insert(Connection conn, List<String> columnOrder, Map<String, String> values) throws Exception {
        StringBuilder cols = new StringBuilder();
        StringBuilder ph = new StringBuilder();
        List<Object> params = new ArrayList<>();

        for (String col : columnOrder) {
            if (col.equalsIgnoreCase(pkName)) continue;
            if (cols.length()>0) { cols.append(", "); ph.append(", "); }
            cols.append(col);
            ph.append("?");
            params.add(values.getOrDefault(col.toLowerCase(), null));
        }

        String sql = "INSERT INTO " + tableName + " (" + cols + ") VALUES (" + ph + ")";
        try (PreparedStatement ps = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            for (int i=0;i<params.size();i++) ps.setObject(i+1, params.get(i));
            ps.executeUpdate();
            var rs = ps.getGeneratedKeys();
            if (rs.next()) return rs.getInt(1);
            return null;
        }
    }

    @Override
    public void update(Connection conn, Object idValue, List<String> columnOrder, Map<String, String> values) throws Exception {
        StringBuilder set = new StringBuilder();
        List<Object> params = new ArrayList<>();
        for (String col : columnOrder) {
            if (col.equalsIgnoreCase(pkName)) continue;
            if (set.length()>0) set.append(", ");
            set.append(col).append(" = ?");
            params.add(values.getOrDefault(col.toLowerCase(), null));
        }
        String sql = "UPDATE " + tableName + " SET " + set + " WHERE " + pkName + " = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            int idx = 1;
            for (Object p : params) ps.setObject(idx++, p);
            ps.setObject(idx, idValue);
            ps.executeUpdate();
        }
    }

    @Override
    public void delete(Connection conn, Object idValue) throws Exception {
        try (PreparedStatement ps = conn.prepareStatement("DELETE FROM " + tableName + " WHERE " + pkName + " = ?")) {
            ps.setObject(1, idValue);
            ps.executeUpdate();
        }
    }
}

