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
        frame.setSize(420, 230);
        frame.setLocationRelativeTo(null);
        frame.setLayout(new GridBagLayout());
        frame.getContentPane().setBackground(new Color(245,245,250));
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(10,10,10,10);
        c.fill = GridBagConstraints.HORIZONTAL;

        JLabel lblUser = new JLabel("Usuário:");
        c.gridx = 0; c.gridy = 0;
        frame.add(lblUser, c);
        JTextField tfUser = new JTextField();
        c.gridx = 1; c.gridy = 0; c.weightx = 1.0;
        frame.add(tfUser, c);

        JLabel lblPass = new JLabel("Senha:");
        c.gridx = 0; c.gridy = 1; c.weightx = 0;
        frame.add(lblPass, c);
        JPasswordField pfPass = new JPasswordField();
        c.gridx = 1; c.gridy = 1; c.weightx = 1.0;
        frame.add(pfPass, c);

        JButton btnLogin = new JButton("Entrar");
        btnLogin.setBackground(new Color(70,130,180));
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setPreferredSize(new Dimension(100, 32));
        c.gridx = 0; c.gridy = 2; c.gridwidth = 2; c.anchor = GridBagConstraints.CENTER; c.weightx = 0;
        frame.add(btnLogin, c);

        btnLogin.addActionListener(e -> {
            String user = tfUser.getText().trim();
            String pass = new String(pfPass.getPassword());
            // temporário: credenciais embutidas
            if ("admin".equals(user) && "admin".equals(pass)) {
                frame.dispose();
                TelaPrincipalHosp.main(new String[0]);
            } else {
                JOptionPane.showMessageDialog(frame, "Usuário ou senha inválidos!", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        });

        frame.setVisible(true);
    }
}
