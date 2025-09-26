package com.meuprojeto;

import com.meuprojeto.ui.panels.GenericTablePanel;

import javax.swing.*;
import java.awt.*;
import java.util.LinkedHashMap;
import java.util.Map;

public class TelaPrincipalHosp extends JFrame {

    private JPanel painelCentral;
    private CardLayout cardLayout;
    private JButton btnAtivo = null;
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

        JPanel painelLateral = new JPanel(new GridLayout(0, 1, 10, 10));
        painelLateral.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));

        tabelaCores = createTabelaCores();

        for (String tabela : tabelaCores.keySet()) {
            JButton btn = new JButton(tabela.substring(0,1).toUpperCase() + tabela.substring(1));
            btn.setBackground(tabelaCores.get(tabela));
            btn.setForeground(Color.WHITE);
            btn.setFocusPainted(false);
            btn.setFont(new Font("Arial", Font.BOLD, 14));
            painelLateral.add(btn);

            addHoverEffect(btn, tabela);
            btn.addActionListener(e -> {
                animarTransicao(tabela);
                destacarBotao(btn);
            });
        }

        add(painelLateral, BorderLayout.WEST);

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
        cores.put("Agendamentos", new Color(138, 43, 226));
        cores.put("departamento", new Color(60, 179, 113));
        cores.put("quartos", new Color(255, 215, 0));
        cores.put("Faturamento", new Color(220, 20, 60));
        cores.put("internacoes", new Color(70, 130, 180));
        cores.put("users", new Color(123, 104, 238));
        cores.put("prescricao", new Color(244, 164, 96));
        return cores;
    }

    private void addHoverEffect(JButton btn, String tabela) {
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                if (btnAtivo != btn) btn.setBackground(tabelaCores.get(tabela).darker());
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                if (btnAtivo != btn) btn.setBackground(tabelaCores.get(tabela));
            }
        });
    }

    private void destacarBotao(JButton btn) {
        if (btnAtivo != null) btnAtivo.setBackground(tabelaCores.get(getTabelaNome(btnAtivo)));
        btnAtivo = btn;
        btnAtivo.setBackground(tabelaCores.get(getTabelaNome(btnAtivo)).darker());
    }

    private String getTabelaNome(JButton btn) {
        String text = btn.getText();
        for (String key : tabelaCores.keySet()) {
            if (key.equalsIgnoreCase(text)) return key;
        }
        return text.toLowerCase();
    }

    private void animarTransicao(String tabela) {
        painelCentral.setVisible(false);
        cardLayout.show(painelCentral, tabela);
        painelCentral.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            TelaPrincipalHosp tela = new TelaPrincipalHosp();
            tela.setVisible(true);
        });
    }
}
