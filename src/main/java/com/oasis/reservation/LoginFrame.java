package com.oasis.reservation;

import javax.swing.*;
import java.awt.*;

public class LoginFrame extends JFrame {

    private JTextField usernameField;
    private JPasswordField passwordField;

    public LoginFrame() {

        setTitle("Online Reservation System - Login");
        setSize(400, 250);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridLayout(3, 2, 10, 10));

        JLabel usernameLabel = new JLabel("Username:");
        JLabel passwordLabel = new JLabel("Password:");

        usernameField = new JTextField();
        passwordField = new JPasswordField();

        JButton loginButton = new JButton("Login");

        panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        panel.add(usernameLabel);
        panel.add(usernameField);
        panel.add(passwordLabel);
        panel.add(passwordField);
        panel.add(new JLabel());
        panel.add(loginButton);

        add(panel);

        loginButton.addActionListener(e -> login());
    }

    private void login() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter username and password.");
            return;
        }

        String sql = "SELECT * FROM users WHERE username = ? AND password = ?";

        try (java.sql.Connection connection = DatabaseConnection.connect();
             java.sql.PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, username);
            statement.setString(2, password);

            java.sql.ResultSet result = statement.executeQuery();

            if (result.next()) {
                JOptionPane.showMessageDialog(this, "Login successful!");
                new ReservationFrame().setVisible(true);
                dispose();
                  } else {
                JOptionPane.showMessageDialog(this, "Invalid username or password.");
            }

        } catch (java.sql.SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database error.");
        }
    }

    public static void main(String[] args) {
        new LoginFrame().setVisible(true);
    }
}
