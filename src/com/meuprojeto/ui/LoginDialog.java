package com.meuprojeto.ui;

import javax.swing.*;
import java.awt.*;

public class LoginDialog {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(LoginDialog::showLogin);
    }

    private static void showLogin() {
        JFrame frame = new JFrame("Login - Gestão Hospitalar");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(350, 200);
        frame.setLocationRelativeTo(null);
        frame.setLayout(new GridBagLayout());
        frame.getContentPane().setBackground(new Color(245, 245, 250));

        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(10, 10, 10, 10);
        c.fill = GridBagConstraints.HORIZONTAL;

        // --- Usuário ---
        JLabel lblUser = new JLabel("Usuário:");
        c.gridx = 0; c.gridy = 0; c.gridwidth = 1; c.anchor = GridBagConstraints.LINE_END;
        frame.add(lblUser, c);

        JTextField tfUser = new JTextField();
        tfUser.setColumns(15);
        c.gridx = 1; c.gridy = 0; c.gridwidth = 2; c.anchor = GridBagConstraints.LINE_START;
        frame.add(tfUser, c);

        // --- Senha ---
        JLabel lblPass = new JLabel("Senha:");
        c.gridx = 0; c.gridy = 1; c.gridwidth = 1; c.anchor = GridBagConstraints.LINE_END;
        frame.add(lblPass, c);

        JPasswordField pfPass = new JPasswordField();
        pfPass.setColumns(15);
        c.gridx = 1; c.gridy = 1; c.gridwidth = 2; c.anchor = GridBagConstraints.LINE_START;
        frame.add(pfPass, c);

        // --- Botão Login ---
        JButton btnLogin = new JButton("Entrar");
        btnLogin.setBackground(new Color(70, 130, 180));
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setPreferredSize(new Dimension(100, 30));
        c.gridx = 1; c.gridy = 2; c.gridwidth = 1; c.anchor = GridBagConstraints.CENTER;
        frame.add(btnLogin, c);

        // --- Ação do botão ---
        btnLogin.addActionListener(e -> {
            String username = tfUser.getText().trim();
            String password = new String(pfPass.getPassword());

            // Exemplo simples: usuário admin/admin
            if (username.equals("admin") && password.equals("admin")) {
                JOptionPane.showMessageDialog(frame, "Login bem-sucedido!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                frame.dispose();
                com.meuprojeto.ui.TelaPrincipalHosp.main(new String[0]); // abre tela principal
            } else {
                JOptionPane.showMessageDialog(frame, "Usuário ou senha incorretos!", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        });

        frame.setVisible(true);
    }
}
