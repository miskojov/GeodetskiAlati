// Verzija 2.0
package com.geodetskialati;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.text.DecimalFormat;

public class TO8 {

    private final KoordinateMenadzer koordinateMenadzer;

    public TO8(KoordinateMenadzer koordinateMenadzer) {
        this.koordinateMenadzer = koordinateMenadzer;
    }

    public void izvrsi() {
        JFrame frame = new JFrame("TO8 - Izbor unosa koordinata");
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
            double Ya = unosKoordinate(frame, "Unesite Ya:");
            double Xa = unosKoordinate(frame, "Unesite Xa:");
            double Yb = unosKoordinate(frame, "Unesite Yb:");
            double Xb = unosKoordinate(frame, "Unesite Xb:");

            prikaziRezultate(frame, Ya, Xa, Yb, Xb);
        } catch (Exception ex) {
            showError(frame, "Greška: " + ex.getMessage());
        }
    }

    private void handleFileInput(JFrame frame) {
        try {
            String nazivA = unosNazivaTacke(frame, "Unesite naziv tačke A:");
            double[] koordinateA = koordinateMenadzer.preuzmiKoordinate(nazivA);

            String nazivB = unosNazivaTacke(frame, "Unesite naziv tačke B:");
            double[] koordinateB = koordinateMenadzer.preuzmiKoordinate(nazivB);

            prikaziRezultate(frame, koordinateA[0], koordinateA[1], koordinateB[0], koordinateB[1]);
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

    private void prikaziRezultate(JFrame frame, double Ya, double Xa, double Yb, double Xb) {
        double U = Yb - Ya;
        double V = Xb - Xa;
        if (V == 0) {
            V = 1E-20; // Izbegavanje deljenja nulom
        }

        double K = U / V;
        double T = Math.toDegrees(Math.atan(K)) + (1 - Math.signum(V)) * 90;
        if (T < 0) {
            T += 360;
        }

        double S = Math.sqrt(U * U + V * V);

        String ugaoDMS = KoordinateMenadzer.formatirajDMS(T);
        DecimalFormat df = new DecimalFormat("#.000");
        String duzina = df.format(S);

        showInfo(frame, "Direkcioni ugao ν (ni) = " + ugaoDMS + "\nDužina (d) = " + duzina + " m");
    }

    private void showError(JFrame frame, String message) {
        JOptionPane.showMessageDialog(frame, message, "Greška", JOptionPane.ERROR_MESSAGE);
    }

    private void showInfo(JFrame frame, String message) {
        JOptionPane.showMessageDialog(frame, message, "Informacija", JOptionPane.INFORMATION_MESSAGE);
    }
}
