package com.meuprojeto.ui;

import com.meuprojeto.Database;
import com.meuprojeto.ui.panels.GenericTablePanel;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DynamicTelaPrincipal {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(DynamicTelaPrincipal::createAndShowGUI);
    }

    private static void createAndShowGUI() {
        JFrame frame = new JFrame("Sistema Hospitalar - Interface Dinâmica");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1200, 700);
        frame.setLocationRelativeTo(null);

        JTabbedPane tabbed = new JTabbedPane();
        List<String> tables = new ArrayList<>();

        try (Connection conn = Database.getConnection()) {
            DatabaseMetaData dm = conn.getMetaData();
            String catalog = conn.getCatalog();
            try (ResultSet rs = dm.getTables(catalog, null, "%", new String[]{"TABLE"})) {
                while (rs.next()) {
                    String table = rs.getString("TABLE_NAME");
                    if (table != null && !table.toLowerCase().startsWith("sys") && !table.equalsIgnoreCase("mysql")) {
                        tables.add(table);
                    }
                }
            }

            if (tables.isEmpty()) {
                JPanel p = new JPanel(new BorderLayout());
                p.add(new JLabel("Nenhuma tabela encontrada.", SwingConstants.CENTER), BorderLayout.CENTER);
                tabbed.addTab("Info", p);
            } else {
                for (String tableName : tables) {
                    String pk = guessPrimaryKey(conn, tableName);
                    GenericTablePanel panel = new GenericTablePanel(tableName);
                    tabbed.addTab(tableName + (pk != null ? " (" + pk + ")" : ""), panel);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Erro ao acessar o banco de dados:\n" + e.getMessage(),
                    "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        frame.add(tabbed, BorderLayout.CENTER);
        frame.setVisible(true);
    }

    private static String guessPrimaryKey(Connection conn, String tableName) {
        try {
            DatabaseMetaData dm = conn.getMetaData();
            try (ResultSet rs = dm.getPrimaryKeys(conn.getCatalog(), null, tableName)) {
                if (rs.next()) {
                    return rs.getString("COLUMN_NAME");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        // fallback para "id" se não achar PK
        return "id";
    }
}
