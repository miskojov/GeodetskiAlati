// Verzija 2.0
package com.geodetskialati;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.text.DecimalFormat;

public class ObrnutiTO8 {

    private final KoordinateMenadzer koordinateMenadzer;

    public ObrnutiTO8(KoordinateMenadzer koordinateMenadzer) {
        this.koordinateMenadzer = koordinateMenadzer;
    }

    public void izvrsi() {
        JFrame frame = new JFrame("Obrnuti TO8");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(400, 300);
        frame.setLayout(new BorderLayout());

        JLabel label = new JLabel("Izaberite način unosa koordinata:");
        JButton manualInputButton = new JButton("Ručno unesite koordinate");
        JButton fileInputButton = new JButton("Unesite naziv tačke (preuzmi iz datoteke)");
        JButton cancelButton = new JButton("Otkaži");

        label.setHorizontalAlignment(SwingConstants.CENTER);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(3, 1, 10, 10));
        buttonPanel.add(manualInputButton);
        buttonPanel.add(fileInputButton);
        buttonPanel.add(cancelButton);

        frame.add(label, BorderLayout.NORTH);
        frame.add(buttonPanel, BorderLayout.CENTER);

        manualInputButton.addActionListener(e -> handleManualInput(frame));
        fileInputButton.addActionListener(e -> handleFileInput(frame));
        cancelButton.addActionListener(e -> frame.dispose());

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private void handleManualInput(JFrame frame) {
        try {
            double y = unosKoordinate(frame, "Unesite Ya:");
            double x = unosKoordinate(frame, "Unesite Xa:");
            double niDMS = unosKoordinate(frame, "Unesite direkcioni ugao ν (ni) u DMS:");
            double d = unosKoordinate(frame, "Unesite dužinu (d):");

            double m = KoordinateMenadzer.konvertujDMSuDecimale(niDMS);
            double yNovo = y + d * Math.sin(Math.toRadians(m));
            double xNovo = x + d * Math.cos(Math.toRadians(m));

            prikaziRezultate(frame, yNovo, xNovo);
        } catch (Exception ex) {
            showError(frame, "Greška: " + ex.getMessage());
        }
    }

    private void handleFileInput(JFrame frame) {
        try {
            String naziv = unosNazivaTacke(frame, "Unesite naziv tačke:");
            double[] koordinate = koordinateMenadzer.preuzmiKoordinate(naziv);

            double niDMS = unosKoordinate(frame, "Unesite direkcioni ugao ν (ni) u DMS:");
            double d = unosKoordinate(frame, "Unesite dužinu (d):");

            double m = KoordinateMenadzer.konvertujDMSuDecimale(niDMS);
            double yNovo = koordinate[0] + d * Math.sin(Math.toRadians(m));
            double xNovo = koordinate[1] + d * Math.cos(Math.toRadians(m));

            prikaziRezultate(frame, yNovo, xNovo);
        } catch (Exception ex) {
            showError(frame, "Greška: " + ex.getMessage());
        }
    }

    private double unosKoordinate(JFrame frame, String poruka) throws Exception {
        String input = JOptionPane.showInputDialog(frame, poruka);
        if (input == null || input.trim().isEmpty()) {
            throw new Exception("Unos prekinut.");
        }
        return Double.parseDouble(input.trim());
    }

    private String unosNazivaTacke(JFrame frame, String poruka) throws Exception {
        String input = JOptionPane.showInputDialog(frame, poruka);
        if (input == null || input.trim().isEmpty()) {
            throw new Exception("Naziv tačke nije validan.");
        }
        return input.trim().toUpperCase();
    }

    private void prikaziRezultate(JFrame frame, double yNovo, double xNovo) {
        DecimalFormat df = new DecimalFormat("#.000");
        showInfo(frame, "Nove koordinate:\nY = " + df.format(yNovo) + "\nX = " + df.format(xNovo));
    }

    private void showError(JFrame frame, String message) {
        JOptionPane.showMessageDialog(frame, message, "Greška", JOptionPane.ERROR_MESSAGE);
    }

    private void showInfo(JFrame frame, String message) {
        JOptionPane.showMessageDialog(frame, message, "Informacija", JOptionPane.INFORMATION_MESSAGE);
    }
}
