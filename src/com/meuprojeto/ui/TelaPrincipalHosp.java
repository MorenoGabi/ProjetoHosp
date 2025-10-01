package com.meuprojeto.ui;

import com.meuprojeto.ui.panels.GenericTablePanel;

import javax.swing.*;
import java.awt.*;
import java.util.LinkedHashMap;
import java.util.Map;

public class TelaPrincipalHosp extends JFrame {
    private JPanel painelCentral;
    private CardLayout cardLayout;
    private Map<String, Color> tabelaCores;

    public TelaPrincipalHosp() {
        setTitle("Sistema Hospitalar");
        setSize(1200, 720);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // topo
        JLabel titulo = new JLabel("Gestão Hospitalar", SwingConstants.CENTER);
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 26));
        titulo.setOpaque(true);
        titulo.setBackground(new Color(30, 144, 255));
        titulo.setForeground(Color.WHITE);
        titulo.setBorder(BorderFactory.createEmptyBorder(12, 0, 12, 0));
        add(titulo, BorderLayout.NORTH);

        // lateral
        JPanel painelLateral = new JPanel(new GridLayout(0, 1, 8, 8));
        painelLateral.setBorder(BorderFactory.createEmptyBorder(12, 10, 12, 10));
        tabelaCores = createTabelaCores();

        for (String tabela : tabelaCores.keySet()) {
            JButton btn = new JButton(capitalize(tabela));
            btn.setBackground(tabelaCores.get(tabela));
            btn.setForeground(Color.WHITE);
            btn.setFocusPainted(false);
            btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
            btn.setPreferredSize(new Dimension(140, 34));
            painelLateral.add(btn);

            btn.addActionListener(e -> {
                cardLayout.show(painelCentral, tabela);
            });
        }
        add(painelLateral, BorderLayout.WEST);

        // central
        cardLayout = new CardLayout();
        painelCentral = new JPanel(cardLayout);

        for (String tabela : tabelaCores.keySet()) {
            painelCentral.add(new GenericTablePanel(tabela), tabela);
        }
        add(painelCentral, BorderLayout.CENTER);
    }

    private Map<String, Color> createTabelaCores() {
        Map<String, Color> cores = new LinkedHashMap<>();
        cores.put("person", new Color(72, 209, 204));
        cores.put("patient", new Color(255, 99, 71));
        cores.put("profissionais", new Color(255, 165, 0));
        cores.put("agendamentos", new Color(138, 43, 226));
        cores.put("departamento", new Color(60, 179, 113));
        cores.put("quartos", new Color(255, 215, 0));
        cores.put("faturamento", new Color(220, 20, 60));
        cores.put("internacoes", new Color(70, 130, 180));
        cores.put("users", new Color(123, 104, 238));
        cores.put("prescricao", new Color(244, 164, 96));
        return cores;
    }

    private String capitalize(String s) {
        if (s == null || s.isEmpty()) return s;
        return s.substring(0,1).toUpperCase() + s.substring(1);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            TelaPrincipalHosp tela = new TelaPrincipalHosp();
            tela.setVisible(true);
        });
    }
}
