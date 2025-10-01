package com.meuprojeto.ui.panels;

import com.meuprojeto.Database;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Painel genérico que monta a tabela + formulário CRUD simples.
 * Uso:
 *   new GenericTablePanel("patient")  // usa "id" como PK padrão
 *   new GenericTablePanel("my_table", "my_pk")
 */
public class GenericTablePanel extends JPanel {
    private final String tableName;
    private final String pkColumn;
    private final DefaultTableModel model = new DefaultTableModel();
    private final JTable table = new JTable(model);
    private final List<String> columnNames = new ArrayList<>();
    private final List<Integer> columnTypes = new ArrayList<>();

    public GenericTablePanel(String tableName, String pkColumn) {
        this.tableName = tableName;
        this.pkColumn = (pkColumn == null || pkColumn.isBlank()) ? "id" : pkColumn;
        initUI();
        loadMetaAndData();
    }

    /** Construtor curto — usa "id" como PK */
    public GenericTablePanel(String tableName) {
        this(tableName, "id");
    }

    private void initUI() {
        setLayout(new BorderLayout());
        setBackground(new Color(245, 245, 250));

        table.setFillsViewportHeight(true);
        table.setRowHeight(24);
        table.setSelectionBackground(new Color(70, 130, 180));
        table.setSelectionForeground(Color.WHITE);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        JButton btnRefresh = new JButton("Atualizar");
        JButton btnNovo = new JButton("Novo");
        JButton btnEdit = new JButton("Editar");
        JButton btnDelete = new JButton("Excluir");

        // tornar botões mais compactos
        Dimension btnSize = new Dimension(90, 28);
        btnRefresh.setPreferredSize(btnSize);
        btnNovo.setPreferredSize(btnSize);
        btnEdit.setPreferredSize(btnSize);
        btnDelete.setPreferredSize(btnSize);

        bottom.add(btnRefresh);
        bottom.add(btnNovo);
        bottom.add(btnEdit);
        bottom.add(btnDelete);
        add(bottom, BorderLayout.SOUTH);

        btnRefresh.addActionListener(e -> loadData());
        btnNovo.addActionListener(e -> openForm(null));
        btnEdit.addActionListener(e -> {
            int r = table.getSelectedRow();
            if (r < 0) { JOptionPane.showMessageDialog(this, "Selecione uma linha."); return; }
            Object id = model.getValueAt(r, 0);
            openForm(id);
        });
        btnDelete.addActionListener(e -> {
            int r = table.getSelectedRow();
            if (r < 0) { JOptionPane.showMessageDialog(this, "Selecione uma linha."); return; }
            Object id = model.getValueAt(r, 0);
            int c = JOptionPane.showConfirmDialog(this, "Excluir registro ID " + id + "?", "Confirmar", JOptionPane.YES_NO_OPTION);
            if (c == JOptionPane.YES_OPTION) {
                try { deleteById(id); loadData(); } catch (Exception ex) { ex.printStackTrace(); JOptionPane.showMessageDialog(this, "Erro: " + ex.getMessage()); }
            }
        });
    }

    private void loadMetaAndData() {
        columnNames.clear();
        columnTypes.clear();
        model.setColumnCount(0);

        // tenta SELECT LIMIT 1 para pegar metadata (simples)
        try (Connection conn = Database.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM " + tableName + " LIMIT 1")) {

            ResultSetMetaData md = rs.getMetaData();
            for (int i = 1; i <= md.getColumnCount(); i++) {
                String name = md.getColumnName(i);
                int type = md.getColumnType(i);
                columnNames.add(name);
                columnTypes.add(type);
                model.addColumn(name);
            }
            loadData();
        } catch (SQLException e) {
            // fallback para DatabaseMetaData (se tabela vazia)
            try {
                loadMetaFromDatabaseMetaData();
                loadData();
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Erro ao ler metadata: " + ex.getMessage());
            }
        }
    }

    private void loadMetaFromDatabaseMetaData() throws SQLException {
        try (Connection conn = Database.getConnection()) {
            DatabaseMetaData dm = conn.getMetaData();
            try (ResultSet rs = dm.getColumns(conn.getCatalog(), null, tableName, null)) {
                while (rs.next()) {
                    String name = rs.getString("COLUMN_NAME");
                    int type = rs.getInt("DATA_TYPE");
                    columnNames.add(name);
                    columnTypes.add(type);
                    model.addColumn(name);
                }
            }
        }
    }

    public void loadData() {
        SwingUtilities.invokeLater(() -> {
            model.setRowCount(0);
            String sql = "SELECT * FROM " + tableName + " ORDER BY " + pkColumn;
            try (Connection conn = Database.getConnection();
                 Statement st = conn.createStatement();
                 ResultSet rs = st.executeQuery(sql)) {

                while (rs.next()) {
                    Object[] row = new Object[columnNames.size()];
                    for (int i = 0; i < columnNames.size(); i++) {
                        row[i] = rs.getObject(columnNames.get(i));
                    }
                    model.addRow(row);
                }
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Erro ao carregar dados: " + e.getMessage());
            }
        });
    }

    private void openForm(Object idValue) {
        // Formulário compacto e organizado
        JDialog dlg = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), true);
        dlg.setTitle((idValue == null ? "Novo " : "Editar ") + tableName);
        dlg.setSize(420, Math.min(420, 90 + columnNames.size() * 36));
        dlg.setLocationRelativeTo(this);

        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6,6,6,6);
        c.anchor = GridBagConstraints.WEST;

        java.util.List<JTextField> fields = new ArrayList<>();
        for (int i = 0; i < columnNames.size(); i++) {
            String col = columnNames.get(i);
            c.gridx = 0; c.gridy = i;
            form.add(new JLabel(col + ":"), c);
            c.gridx = 1;
            JTextField tf = new JTextField(22);
            if (col.equalsIgnoreCase(pkColumn)) tf.setEnabled(false);
            form.add(tf, c);
            fields.add(tf);
        }

        if (idValue != null) {
            String sql = "SELECT * FROM " + tableName + " WHERE " + pkColumn + " = ?";
            try (Connection conn = Database.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setObject(1, idValue);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        for (int i = 0; i < columnNames.size(); i++) {
                            Object val = rs.getObject(columnNames.get(i));
                            fields.get(i).setText(val == null ? "" : val.toString());
                        }
                    }
                }
            } catch (Exception e) { e.printStackTrace(); JOptionPane.showMessageDialog(this, "Erro: "+e.getMessage()); }
        }

        JPanel foot = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnSave = new JButton("Salvar");
        JButton btnCancel = new JButton("Cancelar");
        btnSave.setPreferredSize(new Dimension(90, 28));
        btnCancel.setPreferredSize(new Dimension(90, 28));
        foot.add(btnSave); foot.add(btnCancel);

        btnCancel.addActionListener(e -> dlg.dispose());
        btnSave.addActionListener(e -> {
            try {
                if (idValue == null) insertFromFields(fields);
                else updateFromFields(idValue, fields);
                dlg.dispose();
                loadData();
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(dlg, "Erro ao salvar: " + ex.getMessage());
            }
        });

        dlg.getContentPane().setLayout(new BorderLayout());
        dlg.add(new JScrollPane(form), BorderLayout.CENTER);
        dlg.add(foot, BorderLayout.SOUTH);
        dlg.setVisible(true);
    }

    private void insertFromFields(List<JTextField> fields) throws SQLException {
        StringBuilder cols = new StringBuilder();
        StringBuilder vals = new StringBuilder();
        List<Object> params = new ArrayList<>();

        for (int i = 0; i < columnNames.size(); i++) {
            String col = columnNames.get(i);
            String text = fields.get(i).getText().trim();
            if (col.equalsIgnoreCase(pkColumn) && (text.isEmpty() || text.equals("0"))) continue; // pular PK auto
            if (cols.length() > 0) { cols.append(", "); vals.append(", "); }
            cols.append(col); vals.append("?");
            params.add(text.isEmpty() ? null : text);
        }

        String sql = "INSERT INTO " + tableName + " (" + cols + ") VALUES (" + vals + ")";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            for (int i = 0; i < params.size(); i++) ps.setObject(i + 1, params.get(i));
            ps.executeUpdate();
        }
    }

    private void updateFromFields(Object idValue, List<JTextField> fields) throws SQLException {
        StringBuilder set = new StringBuilder();
        List<Object> params = new ArrayList<>();
        for (int i = 0; i < columnNames.size(); i++) {
            String col = columnNames.get(i);
            if (col.equalsIgnoreCase(pkColumn)) continue;
            if (set.length() > 0) set.append(", ");
            set.append(col).append(" = ?");
            String text = fields.get(i).getText().trim();
            params.add(text.isEmpty() ? null : text);
        }
        String sql = "UPDATE " + tableName + " SET " + set + " WHERE " + pkColumn + " = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            int idx = 1;
            for (Object p : params) ps.setObject(idx++, p);
            ps.setObject(idx, idValue);
            ps.executeUpdate();
        }
    }

    private void deleteById(Object idValue) throws SQLException {
        String sql = "DELETE FROM " + tableName + " WHERE " + pkColumn + " = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setObject(1, idValue);
            ps.executeUpdate();
        }
    }

    public JTable getTable() { return table; }
}
