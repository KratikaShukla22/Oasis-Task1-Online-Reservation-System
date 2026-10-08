package com.oasis.reservation;

import javax.swing.*;
import java.awt.*;

public class CancellationFrame extends JFrame {

    private JTextField pnrField;
    private JTextArea detailsArea;
    private JButton fetchButton;
    private JButton cancelButton;

    public CancellationFrame() {

        setTitle("Online Reservation System - Cancellation");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel topPanel = new JPanel(new FlowLayout());

        topPanel.add(new JLabel("Enter PNR:"));

        pnrField = new JTextField(10);
        fetchButton = new JButton("Fetch");

        topPanel.add(pnrField);
        topPanel.add(fetchButton);

        detailsArea = new JTextArea();
        detailsArea.setEditable(false);

        cancelButton = new JButton("Cancel Ticket");
        cancelButton.setEnabled(false);

        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(new JScrollPane(detailsArea), BorderLayout.CENTER);
        panel.add(cancelButton, BorderLayout.SOUTH);

        add(panel);

        fetchButton.addActionListener(e -> fetchReservation());

        cancelButton.addActionListener(e -> cancelReservation());
    }

    private void fetchReservation() {

        String pnrText = pnrField.getText();

        if (pnrText.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Please enter PNR."
            );
            return;
        }

        try {
            int pnr = Integer.parseInt(pnrText);

            String sql = "SELECT * FROM reservations WHERE pnr = ?";

            try (java.sql.Connection connection = DatabaseConnection.connect();
                 java.sql.PreparedStatement statement =
                         connection.prepareStatement(sql)) {

                statement.setInt(1, pnr);

                java.sql.ResultSet result = statement.executeQuery();

                if (result.next()) {

                    detailsArea.setText(
                            "PNR: " + result.getInt("pnr") + "\n" +
                                    "Passenger: " + result.getString("passenger_name") + "\n" +
                                    "Train Number: " + result.getInt("train_number") + "\n" +
                                    "Train Name: " + result.getString("train_name") + "\n" +
                                    "Class: " + result.getString("class_type") + "\n" +
                                    "Date: " + result.getString("journey_date") + "\n" +
                                    "From: " + result.getString("source") + "\n" +
                                    "To: " + result.getString("destination")
                    );

                    cancelButton.setEnabled(true);

                } else {

                    detailsArea.setText("No reservation found.");
                    cancelButton.setEnabled(false);
                }

            }

        } catch (NumberFormatException ex) {

            JOptionPane.showMessageDialog(
                    this,
                    "PNR must be numeric."
            );

        } catch (java.sql.SQLException ex) {

            ex.printStackTrace();

            JOptionPane.showMessageDialog(
                    this,
                    "Database error."
            );
        }
    }

    private void cancelReservation() {

        int choice = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to cancel this ticket?",
                "Confirm Cancellation",
                JOptionPane.YES_NO_OPTION
        );

        if (choice != JOptionPane.YES_OPTION) {
            return;
        }

        try {

            int pnr = Integer.parseInt(pnrField.getText());

            String sql = "DELETE FROM reservations WHERE pnr = ?";

            try (java.sql.Connection connection = DatabaseConnection.connect();
                 java.sql.PreparedStatement statement =
                         connection.prepareStatement(sql)) {

                statement.setInt(1, pnr);

                int rowsDeleted = statement.executeUpdate();

                if (rowsDeleted > 0) {

                    JOptionPane.showMessageDialog(
                            this,
                            "Ticket cancelled successfully!"
                    );

                    detailsArea.setText("");
                    pnrField.setText("");
                    cancelButton.setEnabled(false);

                } else {

                    JOptionPane.showMessageDialog(
                            this,
                            "Reservation not found."
                    );
                }
            }

        } catch (Exception ex) {

            ex.printStackTrace();

            JOptionPane.showMessageDialog(
                    this,
                    "Cancellation failed."
            );
        }
    }

    public static void main(String[] args) {
        new CancellationFrame().setVisible(true);
    }
}