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
        setSize(1300, 800);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JLabel titulo = new JLabel("Hospital Management System", SwingConstants.CENTER);
        titulo.setFont(new Font("Arial", Font.BOLD, 28));
        titulo.setOpaque(true);
        titulo.setBackground(new Color(30, 144, 255));
        titulo.setForeground(Color.WHITE);
        titulo.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));
        add(titulo, BorderLayout.NORTH);

        // Botões laterais
        JPanel painelLateral = new JPanel(new GridLayout(0, 1, 10, 10));
        painelLateral.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));

        tabelaCores = new LinkedHashMap<>();
        tabelaCores.put("person", new Color(72, 209, 204));
        tabelaCores.put("patient", new Color(255, 99, 71));
        tabelaCores.put("profissionais", new Color(255, 165, 0));

        for (String tabela : tabelaCores.keySet()) {
            JButton btn = new JButton(tabela.substring(0, 1).toUpperCase() + tabela.substring(1));
            btn.setBackground(tabelaCores.get(tabela));
            btn.setForeground(Color.WHITE);
            btn.setFont(new Font("Arial", Font.BOLD, 14));
            btn.setFocusPainted(false);
            painelLateral.add(btn);
            btn.addActionListener(e -> cardLayout.show(painelCentral, tabela));
        }

        add(painelLateral, BorderLayout.WEST);

        // Painel central
        cardLayout = new CardLayout();
        painelCentral = new JPanel(cardLayout);

        for (String tabela : tabelaCores.keySet()) {
            painelCentral.add(new GenericTablePanel(tabela), tabela);
        }

        add(painelCentral, BorderLayout.CENTER);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new TelaPrincipalHosp().setVisible(true));
    }
}
