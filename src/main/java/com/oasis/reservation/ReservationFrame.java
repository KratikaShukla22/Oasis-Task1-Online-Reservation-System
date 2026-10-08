package com.oasis.reservation;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class ReservationFrame extends JFrame {

    public ReservationFrame() {

        setTitle("Online Reservation System - Reservation");
        setSize(500, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridLayout(8, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel passengerLabel = new JLabel("Passenger Name:");
        JTextField passengerField = new JTextField();

        JLabel trainNumberLabel = new JLabel("Train Number:");
        JTextField trainNumberField = new JTextField();

        JLabel trainNameLabel = new JLabel("Train Name:");
        JTextField trainNameField = new JTextField();

        JLabel classLabel = new JLabel("Class Type:");
        JComboBox<String> classBox =
                new JComboBox<>(new String[]{"Sleeper", "AC", "General"});

        JLabel dateLabel = new JLabel("Journey Date:");
        JTextField dateField = new JTextField();

        JLabel sourceLabel = new JLabel("Source:");
        JTextField sourceField = new JTextField();

        JLabel destinationLabel = new JLabel("Destination:");
        JTextField destinationField = new JTextField();

        JButton bookButton = new JButton("Book Ticket");
        JButton cancelButton = new JButton("Cancel Ticket");

        trainNameField.setEditable(false);

        panel.add(passengerLabel);
        panel.add(passengerField);

        panel.add(trainNumberLabel);
        panel.add(trainNumberField);

        panel.add(trainNameLabel);
        panel.add(trainNameField);

        panel.add(classLabel);
        panel.add(classBox);

        panel.add(dateLabel);
        panel.add(dateField);

        panel.add(sourceLabel);
        panel.add(sourceField);

        panel.add(destinationLabel);
        panel.add(destinationField);

        panel.add(bookButton);
        panel.add(cancelButton);

        add(panel);

        trainNumberField.addActionListener(e -> {

            String trainNumber = trainNumberField.getText();

            if (trainNumber.equals("12345")) {
                trainNameField.setText("Rajdhani Express");

            } else if (trainNumber.equals("12346")) {
                trainNameField.setText("Shatabdi Express");

            } else {
                trainNameField.setText("");
                JOptionPane.showMessageDialog(
                        this,
                        "Train not found."
                );
            }
        });

        bookButton.addActionListener(e -> {

            String passengerName = passengerField.getText();
            String trainNumberText = trainNumberField.getText();
            String trainName = trainNameField.getText();
            String classType = (String) classBox.getSelectedItem();
            String journeyDate = dateField.getText();
            String source = sourceField.getText();
            String destination = destinationField.getText();

            if (passengerName.isEmpty() ||
                    trainNumberText.isEmpty() ||
                    trainName.isEmpty() ||
                    journeyDate.isEmpty() ||
                    source.isEmpty() ||
                    destination.isEmpty()) {

                JOptionPane.showMessageDialog(
                        this,
                        "Please fill all fields."
                );
                return;
            }
            cancelButton.addActionListener(event -> {
                new CancellationFrame().setVisible(true);
            });
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

            try {
                LocalDate.parse(journeyDate, formatter);
            } catch (DateTimeParseException ex) {
                JOptionPane.showMessageDialog(
                        this,
                        "Enter a valid date in DD-MM-YYYY format."
                );
                return;
            }

            int trainNumber;

            try {
                trainNumber = Integer.parseInt(trainNumberText);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(
                        this,
                        "Train number must be numeric."
                );
                return;
            }

            String sql = """
            INSERT INTO reservations
            (passenger_name, train_number, train_name, class_type,
             journey_date, source, destination)
            VALUES (?, ?, ?, ?, ?, ?, ?)
            """;

            try (java.sql.Connection connection = DatabaseConnection.connect();
                 java.sql.PreparedStatement statement =
                         connection.prepareStatement(sql,
                                 java.sql.Statement.RETURN_GENERATED_KEYS)) {

                statement.setString(1, passengerName);
                statement.setInt(2, trainNumber);
                statement.setString(3, trainName);
                statement.setString(4, classType);
                statement.setString(5, journeyDate);
                statement.setString(6, source);
                statement.setString(7, destination);

                statement.executeUpdate();

                java.sql.ResultSet generatedKeys = statement.getGeneratedKeys();

                if (generatedKeys.next()) {
                    int pnr = generatedKeys.getInt(1);

                    JOptionPane.showMessageDialog(
                            this,
                            "Ticket booked successfully!\nYour PNR is: " + pnr
                    );
                }

            } catch (java.sql.SQLException ex) {
                ex.printStackTrace();

                JOptionPane.showMessageDialog(
                        this,
                        "Booking failed."
                );
            }
        });
    }

    public static void main(String[] args) {
        new ReservationFrame().setVisible(true);
    }
}