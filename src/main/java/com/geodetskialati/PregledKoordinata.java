// Verzija 2.0
package com.geodetskialati;

import java.io.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class PregledKoordinata {

    public void izvrsi() {
        // Dobijanje putanje do foldera GeodetskiAlati na Desktopu
        String userHome = System.getProperty("user.home");
        String folderPath = userHome + "\\Desktop\\GeodetskiAlati";
        String filePath = folderPath + "\\YXHkoordinate.csv";

        JFrame frame = new JFrame("Pregled koordinata");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(600, 400);
        frame.setLayout(new BorderLayout());

        try {
            // Provera da li folder GeodetskiAlati postoji, ako ne, kreira se
            File folder = new File(folderPath);
            if (!folder.exists()) {
                folder.mkdirs();
            }

            StringBuilder sadrzajDatoteke = ucitajDatoteku(filePath);

            if (sadrzajDatoteke.length() > 0) {
                // Kreiranje JTextArea komponente za prikaz sadržaja sa skrolovanjem
                JTextArea textArea = new JTextArea(20, 50);
                textArea.setText(sadrzajDatoteke.toString());
                textArea.setEditable(false); // Onemogućava unos teksta

                // Kreiranje JScrollPane komponente koja omogućava skrolovanje
                JScrollPane scrollPane = new JScrollPane(textArea);

                // Kreiranje dugmeta za otvaranje datoteke u tekst editoru
                JButton openEditorButton = new JButton("Otvori u tekst editoru");
                openEditorButton.addActionListener(e -> {
                    try {
                        new ProcessBuilder("notepad.exe", filePath).start();
                    } catch (IOException ex) {
                        showError(frame, "Greška prilikom otvaranja datoteke u tekst editoru: " + ex.getMessage());
                    }
                });

                // Kreiranje dugmeta za otvaranje datoteke u Excel-u
                JButton openExcelButton = new JButton("Otvori u Excel-u");
                openExcelButton.addActionListener(e -> {
                    try {
                        new ProcessBuilder("cmd", "/c", "start", "excel.exe", filePath).start();
                    } catch (IOException ex) {
                        showError(frame, "Greška prilikom otvaranja datoteke u Excel-u: " + ex.getMessage());
                    }
                });

                // Kreiranje panela koji sadrži JScrollPane i dva dugmeta
                JPanel panel = new JPanel(new BorderLayout());
                panel.add(scrollPane, BorderLayout.CENTER);

                // Panel sa dugmićima
                JPanel buttonPanel = new JPanel();
                buttonPanel.setLayout(new FlowLayout());
                buttonPanel.add(openEditorButton);
                buttonPanel.add(openExcelButton);

                panel.add(buttonPanel, BorderLayout.SOUTH);

                frame.add(panel, BorderLayout.CENTER);
            } else {
                showWarning(frame, "Datoteka je prazna.");
            }
        } catch (IOException e) {
            showError(frame, "Greška prilikom čitanja datoteke: " + e.getMessage());
        } finally {
            frame.setVisible(true);
        }
    }

    private static StringBuilder ucitajDatoteku(String filePath) throws IOException {
        StringBuilder sadrzaj = new StringBuilder();
        File file = new File(filePath);

        if (file.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    sadrzaj.append(line).append("\n");
                }
            }
        } else {
            throw new IOException("Datoteka ne postoji na zadatoj putanji.");
        }
        return sadrzaj;
    }

    private void showError(JFrame frame, String message) {
        JOptionPane.showMessageDialog(frame, message, "Greška", JOptionPane.ERROR_MESSAGE);
    }

    private void showWarning(JFrame frame, String message) {
        JOptionPane.showMessageDialog(frame, message, "Upozorenje", JOptionPane.WARNING_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new PregledKoordinata().izvrsi());
    }
}
