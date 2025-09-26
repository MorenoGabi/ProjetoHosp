package com.meuprojeto.ui.panels;

import com.meuprojeto.Database;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class GenericTablePanel extends JPanel {
    private final String tableName;
    private final DefaultTableModel model = new DefaultTableModel();
    private final JTable table = new JTable(model);

    // metadata
    private final List<String> columnNames = new ArrayList<>();
    private final List<Integer> columnTypes = new ArrayList<>();
    private final List<Boolean> columnAutoInc = new ArrayList<>();

    public GenericTablePanel(String tableName) {
        this.tableName = tableName.toLowerCase();
        initUI();
        loadMeta();
        loadData();
    }

    private void initUI() {
        setLayout(new BorderLayout());
        setBackground(new Color(245,245,250));

        table.setRowHeight(24);
        table.setSelectionBackground(new Color(70,130,180));
        table.setSelectionForeground(Color.WHITE);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.LEFT,8,8));
        JButton btnAdd = createButton("Adicionar", new Color(72,209,204));
        JButton btnEdit = createButton("Editar", new Color(255,165,0));
        JButton btnDel = createButton("Excluir", new Color(255,99,71));

        btnAdd.setPreferredSize(new Dimension(100,28));
        btnEdit.setPreferredSize(new Dimension(100,28));
        btnDel.setPreferredSize(new Dimension(100,28));

        bottom.add(btnAdd); bottom.add(btnEdit); bottom.add(btnDel);
        add(bottom, BorderLayout.SOUTH);

        btnAdd.addActionListener(e -> {
            if (tableName.equals("patient")) openPatientForm(null);
            else openGenericForm(null);
        });
        btnEdit.addActionListener(e -> {
            int r = table.getSelectedRow();
            if (r < 0) { JOptionPane.showMessageDialog(this, "Selecione uma linha."); return; }
            Object id = model.getValueAt(r, 0);
            if (tableName.equals("patient")) openPatientForm(id);
            else openGenericForm(id);
        });
        btnDel.addActionListener(e -> {
            int r = table.getSelectedRow();
            if (r < 0) { JOptionPane.showMessageDialog(this, "Selecione uma linha."); return; }
            Object id = model.getValueAt(r, 0);
            int c = JOptionPane.showConfirmDialog(this, "Excluir registro ID " + id + "?", "Confirmar", JOptionPane.YES_NO_OPTION);
            if (c == JOptionPane.YES_OPTION) {
                try { deleteById(id); loadData(); } catch (Exception ex) { ex.printStackTrace(); JOptionPane.showMessageDialog(this, "Erro: "+ex.getMessage()); }
            }
        });
    }

    private JButton createButton(String text, Color bg) {
        JButton b = new JButton(text);
        b.setBackground(bg); b.setForeground(Color.WHITE); b.setFocusPainted(false);
        return b;
    }

    private Connection getConn() throws SQLException {
        return Database.getConnection();
    }

    // load column metadata (tries DatabaseMetaData)
    private void loadMeta() {
        columnNames.clear(); columnTypes.clear(); columnAutoInc.clear(); model.setColumnCount(0);
        try (Connection conn = getConn()) {
            DatabaseMetaData dm = conn.getMetaData();

            // special handling for patient: show joined person fields first
            if (tableName.equals("patient")) {
                // desired person columns
                String[] personCols = {"id","nome","cpf","data_nasc","sexo","contato","endereco"};
                for (String c : personCols) {
                    columnNames.add(c);
                    columnTypes.add(Types.VARCHAR); // some generic; types handled on load
                    columnAutoInc.add(c.equalsIgnoreCase("id"));
                    model.addColumn(c);
                }
                // then add patient-specific columns: num_pront, alergias, observacoes
                try (ResultSet rs = dm.getColumns(conn.getCatalog(), null, "patient", "%")) {
                    while (rs.next()) {
                        String col = rs.getString("COLUMN_NAME");
                        if (col.equalsIgnoreCase("id")) continue; // already added
                        columnNames.add(col);
                        columnTypes.add(rs.getInt("DATA_TYPE"));
                        String ai = rs.getString("IS_AUTOINCREMENT");
                        columnAutoInc.add("YES".equalsIgnoreCase(ai));
                        model.addColumn(col);
                    }
                }
                return;
            }

            // generic: get columns from metadata
            try (ResultSet rs = dm.getColumns(conn.getCatalog(), null, tableName, "%")) {
                while (rs.next()) {
                    String col = rs.getString("COLUMN_NAME");
                    int dt = rs.getInt("DATA_TYPE");
                    String ai = rs.getString("IS_AUTOINCREMENT");
                    columnNames.add(col);
                    columnTypes.add(dt);
                    columnAutoInc.add("YES".equalsIgnoreCase(ai));
                    model.addColumn(col);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            // fallback minimal
            if (columnNames.isEmpty()) {
                columnNames.add("id"); columnTypes.add(Types.INTEGER); columnAutoInc.add(true);
                model.addColumn("id");
            }
            JOptionPane.showMessageDialog(this, "Erro ao ler metadata: " + e.getMessage());
        }
    }

    public void loadData() {
        SwingUtilities.invokeLater(() -> {
            model.setRowCount(0);
            String sql;
            if (tableName.equals("patient")) {
                // join person + patient
                sql = "SELECT p.id, p.nome, p.cpf, p.data_nasc, p.sexo, p.contato, p.endereco, pt.num_pront, pt.alergias, pt.observacoes " +
                        "FROM person p LEFT JOIN patient pt ON p.id = pt.id ORDER BY p.id";
            } else {
                sql = "SELECT * FROM " + tableName + " ORDER BY " + (columnNames.isEmpty() ? "1" : columnNames.get(0));
            }

            try (Connection conn = getConn();
                 Statement st = conn.createStatement();
                 ResultSet rs = st.executeQuery(sql)) {

                ResultSetMetaData md = rs.getMetaData();
                while (rs.next()) {
                    Vector<Object> row = new Vector<>();
                    for (int i = 1; i <= md.getColumnCount(); i++) {
                        Object v = rs.getObject(i);
                        row.add(v);
                    }
                    model.addRow(row);
                }
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Erro ao carregar dados: " + e.getMessage());
            }
        });
    }

    private void deleteById(Object idValue) throws SQLException {
        String idCol = columnNames.get(0);
        String sql = "DELETE FROM " + (tableName.equals("patient") ? "person" : tableName) + " WHERE " + idCol + " = ?";
        try (Connection conn = getConn(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setObject(1, idValue);
            ps.executeUpdate();
        }
    }

    // ---------- Generic form for non-patient ----------
    private void openGenericForm(Object idValue) {
        JDialog dlg = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), true);
        dlg.setTitle((idValue == null ? "Novo " : "Editar ") + tableName);
        dlg.setSize(420, 320);
        dlg.setLocationRelativeTo(this);

        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints(); c.insets = new Insets(6,6,6,6); c.anchor = GridBagConstraints.WEST;
        List<JTextField> fields = new ArrayList<>();

        // build fields from columnNames (skip PK auto-inc)
        int row = 0;
        for (int i = 0; i < columnNames.size(); i++) {
            String col = columnNames.get(i);
            if (i==0 && columnAutoInc.get(i)) continue; // skip id auto-inc
            c.gridx = 0; c.gridy = row; form.add(new JLabel(col+":"), c);
            c.gridx = 1; JTextField tf = new JTextField(20); form.add(tf,c);
            fields.add(tf); row++;
        }

        // if edit -> load values
        if (idValue != null) {
            String idCol = columnNames.get(0);
            String sql = "SELECT * FROM " + tableName + " WHERE " + idCol + " = ?";
            try (Connection conn = getConn(); PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setObject(1, idValue);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        int fieldIdx = 0;
                        for (int i = 0; i < columnNames.size(); i++) {
                            if (i==0 && columnAutoInc.get(i)) continue;
                            Object v = rs.getObject(columnNames.get(i));
                            fields.get(fieldIdx++).setText(v==null?"":v.toString());
                        }
                    }
                }
            } catch (Exception ex) { ex.printStackTrace(); JOptionPane.showMessageDialog(this, "Erro: "+ex.getMessage()); }
        }

        JPanel foot = new JPanel();
        JButton btnSave = createButton("Salvar", new Color(70,130,180));
        JButton btnCancel = new JButton("Cancelar");
        foot.add(btnSave); foot.add(btnCancel);

        btnCancel.addActionListener(ev -> dlg.dispose());
        btnSave.addActionListener(ev -> {
            try {
                if (idValue==null) insertGeneric(fields);
                else updateGeneric(idValue, fields);
                dlg.dispose();
                loadData();
            } catch (Exception ex) { ex.printStackTrace(); JOptionPane.showMessageDialog(this, "Erro: "+ex.getMessage()); }
        });

        dlg.getContentPane().setLayout(new BorderLayout());
        dlg.add(new JScrollPane(form), BorderLayout.CENTER);
        dlg.add(foot, BorderLayout.SOUTH);
        dlg.setVisible(true);
    }

    private void insertGeneric(List<JTextField> fields) throws SQLException {
        StringBuilder cols = new StringBuilder();
        StringBuilder vals = new StringBuilder();
        List<Object> params = new ArrayList<>();
        int fieldIdx = 0;
        for (int i = 0; i < columnNames.size(); i++) {
            if (i==0 && columnAutoInc.get(i)) continue; // skip id
            if (cols.length()>0) { cols.append(", "); vals.append(", "); }
            cols.append(columnNames.get(i)); vals.append("?");
            String txt = fields.get(fieldIdx++).getText().trim();
            params.add(txt.isEmpty() ? null : txt);
        }
        String sql = "INSERT INTO " + tableName + " (" + cols + ") VALUES (" + vals + ")";
        try (Connection conn = getConn(); PreparedStatement ps = conn.prepareStatement(sql)) {
            for (int i=0;i<params.size();i++) ps.setObject(i+1, params.get(i));
            ps.executeUpdate();
        }
    }

    private void updateGeneric(Object idValue, List<JTextField> fields) throws SQLException {
        StringBuilder set = new StringBuilder();
        List<Object> params = new ArrayList<>();
        int fieldIdx = 0;
        for (int i=0;i<columnNames.size();i++) {
            if (i==0 && columnAutoInc.get(i)) continue;
            if (set.length()>0) set.append(", ");
            set.append(columnNames.get(i)).append(" = ?");
            params.add(fields.get(fieldIdx++).getText().trim().isEmpty() ? null : fields.get(fieldIdx-1).getText().trim());
        }
        String sql = "UPDATE " + tableName + " SET " + set + " WHERE " + columnNames.get(0) + " = ?";
        try (Connection conn = getConn(); PreparedStatement ps = conn.prepareStatement(sql)) {
            int idx=1;
            for (Object p : params) ps.setObject(idx++, p);
            ps.setObject(idx, idValue);
            ps.executeUpdate();
        }
    }

    // ---------- patient forms & persistence ----------
    private void openPatientForm(Object idValue) {
        JDialog dlg = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), true);
        dlg.setTitle((idValue==null?"Novo Paciente":"Editar Paciente"));
        dlg.setSize(440, 420);
        dlg.setLocationRelativeTo(this);

        JPanel p = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints(); c.insets = new Insets(6,6,6,6); c.anchor = GridBagConstraints.WEST;

        JTextField tfNome = new JTextField(20);
        JTextField tfCpf = new JTextField(14);
        JTextField tfDataNasc = new JTextField(10); // yyyy-MM-dd
        JTextField tfSexo = new JTextField(6);
        JTextField tfContato = new JTextField(14);
        JTextField tfEndereco = new JTextField(20);
        JTextField tfNumPront = new JTextField(12);
        JTextField tfAlergias = new JTextField(20);
        JTextField tfObserv = new JTextField(20);

        int row = 0;
        c.gridx=0; c.gridy=row; p.add(new JLabel("Nome:"), c); c.gridx=1; p.add(tfNome,c); row++;
        c.gridx=0; c.gridy=row; p.add(new JLabel("CPF:"), c); c.gridx=1; p.add(tfCpf,c); row++;
        c.gridx=0; c.gridy=row; p.add(new JLabel("Data Nasc (yyyy-MM-dd):"), c); c.gridx=1; p.add(tfDataNasc,c); row++;
        c.gridx=0; c.gridy=row; p.add(new JLabel("Sexo:"), c); c.gridx=1; p.add(tfSexo,c); row++;
        c.gridx=0; c.gridy=row; p.add(new JLabel("Contato:"), c); c.gridx=1; p.add(tfContato,c); row++;
        c.gridx=0; c.gridy=row; p.add(new JLabel("Endereço:"), c); c.gridx=1; p.add(tfEndereco,c); row++;

        c.gridx=0; c.gridy=row; p.add(new JLabel("Nº Prontuário:"), c); c.gridx=1; p.add(tfNumPront,c); row++;
        c.gridx=0; c.gridy=row; p.add(new JLabel("Alergias:"), c); c.gridx=1; p.add(tfAlergias,c); row++;
        c.gridx=0; c.gridy=row; p.add(new JLabel("Observações:"), c); c.gridx=1; p.add(tfObserv,c); row++;

        // load when editing
        if (idValue != null) {
            String sql = "SELECT p.*, pt.num_pront, pt.alergias, pt.observacoes FROM person p LEFT JOIN patient pt ON p.id=pt.id WHERE p.id = ?";
            try (Connection conn = getConn(); PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setObject(1, idValue);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        tfNome.setText(rs.getString("nome"));
                        tfCpf.setText(rs.getString("cpf"));
                        Date d = rs.getDate("data_nasc");
                        tfDataNasc.setText(d==null?"":d.toString());
                        tfSexo.setText(rs.getString("sexo"));
                        tfContato.setText(rs.getString("contato"));
                        tfEndereco.setText(rs.getString("endereco"));
                        tfNumPront.setText(rs.getString("num_pront"));
                        tfAlergias.setText(rs.getString("alergias"));
                        tfObserv.setText(rs.getString("observacoes"));
                    }
                }
            } catch (Exception ex) { ex.printStackTrace(); JOptionPane.showMessageDialog(this,"Erro: "+ex.getMessage()); }
        }

        JPanel foot = new JPanel();
        JButton btnSave = createButton("Salvar", new Color(70,130,180));
        JButton btnCancel = new JButton("Cancelar");
        foot.add(btnSave); foot.add(btnCancel);

        btnCancel.addActionListener(ev -> dlg.dispose());
        btnSave.addActionListener(ev -> {
            try {
                if (idValue==null) insertPatient(tfNome, tfCpf, tfDataNasc, tfSexo, tfContato, tfEndereco, tfNumPront, tfAlergias, tfObserv);
                else updatePatient(idValue, tfNome, tfCpf, tfDataNasc, tfSexo, tfContato, tfEndereco, tfNumPront, tfAlergias, tfObserv);
                dlg.dispose();
                loadData();
            } catch (Exception ex) { ex.printStackTrace(); JOptionPane.showMessageDialog(dlg, "Erro ao salvar paciente: "+ex.getMessage()); }
        });

        dlg.getContentPane().setLayout(new BorderLayout());
        dlg.add(new JScrollPane(p), BorderLayout.CENTER);
        dlg.add(foot, BorderLayout.SOUTH);
        dlg.setVisible(true);
    }

    private void insertPatient(JTextField tfNome, JTextField tfCpf, JTextField tfDataNasc,
                               JTextField tfSexo, JTextField tfContato, JTextField tfEndereco,
                               JTextField tfNumPront, JTextField tfAlergias, JTextField tfObserv) throws SQLException {

        try (Connection conn = getConn()) {
            conn.setAutoCommit(false);
            try {
                String sqlPerson = "INSERT INTO person (nome, cpf, data_nasc, sexo, contato, endereco) VALUES (?,?,?,?,?,?)";
                try (PreparedStatement ps = conn.prepareStatement(sqlPerson, Statement.RETURN_GENERATED_KEYS)) {
                    ps.setString(1, tfNome.getText().trim());
                    ps.setString(2, tfCpf.getText().trim());
                    String d = tfDataNasc.getText().trim();
                    if (!d.isEmpty()) ps.setDate(3, Date.valueOf(LocalDate.parse(d)));
                    else ps.setNull(3, Types.DATE);
                    ps.setString(4, tfSexo.getText().trim());
                    ps.setString(5, tfContato.getText().trim());
                    ps.setString(6, tfEndereco.getText().trim());
                    ps.executeUpdate();

                    try (ResultSet keys = ps.getGeneratedKeys()) {
                        if (!keys.next()) throw new SQLException("Não obteve id de person.");
                        int personId = keys.getInt(1);

                        String sqlPatient = "INSERT INTO patient (id, num_pront, alergias, observacoes) VALUES (?, ?, ?, ?)";
                        try (PreparedStatement ps2 = conn.prepareStatement(sqlPatient)) {
                            ps2.setInt(1, personId);
                            ps2.setString(2, tfNumPront.getText().trim());
                            ps2.setString(3, tfAlergias.getText().trim());
                            ps2.setString(4, tfObserv.getText().trim());
                            ps2.executeUpdate();
                        }
                    }
                }
                conn.commit();
            } catch (Exception ex) {
                conn.rollback();
                throw ex;
            } finally {
                conn.setAutoCommit(true);
            }
        }
    }

    private void updatePatient(Object idValue,
                               JTextField tfNome, JTextField tfCpf, JTextField tfDataNasc,
                               JTextField tfSexo, JTextField tfContato, JTextField tfEndereco,
                               JTextField tfNumPront, JTextField tfAlergias, JTextField tfObserv) throws SQLException {

        try (Connection conn = getConn()) {
            conn.setAutoCommit(false);
            try {
                String sqlPerson = "UPDATE person SET nome=?, cpf=?, data_nasc=?, sexo=?, contato=?, endereco=? WHERE id=?";
                try (PreparedStatement ps = conn.prepareStatement(sqlPerson)) {
                    ps.setString(1, tfNome.getText().trim());
                    ps.setString(2, tfCpf.getText().trim());
                    String d = tfDataNasc.getText().trim();
                    if (!d.isEmpty()) ps.setDate(3, Date.valueOf(LocalDate.parse(d)));
                    else ps.setNull(3, Types.DATE);
                    ps.setString(4, tfSexo.getText().trim());
                    ps.setString(5, tfContato.getText().trim());
                    ps.setString(6, tfEndereco.getText().trim());
                    ps.setObject(7, idValue);
                    ps.executeUpdate();
                }

                // update or insert patient row
                String check = "SELECT id FROM patient WHERE id = ?";
                try (PreparedStatement ps = conn.prepareStatement(check)) {
                    ps.setObject(1, idValue);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) {
                            String sqlP = "UPDATE patient SET num_pront=?, alergias=?, observacoes=? WHERE id=?";
                            try (PreparedStatement ps2 = conn.prepareStatement(sqlP)) {
                                ps2.setString(1, tfNumPront.getText().trim());
                                ps2.setString(2, tfAlergias.getText().trim());
                                ps2.setString(3, tfObserv.getText().trim());
                                ps2.setObject(4, idValue);
                                ps2.executeUpdate();
                            }
                        } else {
                            String sqlP = "INSERT INTO patient (id, num_pront, alergias, observacoes) VALUES (?, ?, ?, ?)";
                            try (PreparedStatement ps2 = conn.prepareStatement(sqlP)) {
                                ps2.setObject(1, idValue);
                                ps2.setString(2, tfNumPront.getText().trim());
                                ps2.setString(3, tfAlergias.getText().trim());
                                ps2.setString(4, tfObserv.getText().trim());
                                ps2.executeUpdate();
                            }
                        }
                    }
                }

                conn.commit();
            } catch (Exception ex) {
                conn.rollback();
                throw ex;
            } finally {
                conn.setAutoCommit(true);
            }
        }
    }
}
