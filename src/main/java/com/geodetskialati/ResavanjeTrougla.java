// Verzija 2.0
package com.geodetskialati;

import javax.swing.*;
import java.awt.*;
import java.text.DecimalFormat;

public class ResavanjeTrougla {

    private static final KoordinateMenadzer koordinateMenadzer = new KoordinateMenadzer();

    public static void main(String[] args) {
        izvrsi();
    }

    public static void izvrsi() {
        JFrame frame = new JFrame("Rešavanje trougla");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(500, 400);
        frame.setLayout(new BorderLayout());

        JLabel infoLabel = new JLabel("<html>Unesite najmanje 3 poznate veličine:<br>" +
                "- 2 strane i jedan ugao<br>" +
                "- 2 ugla i jedna strana<br>" +
                "- 3 strane (Heronov obrazac)<br>" +
                "Ako su poznata sva 3 ugla, unesite i jednu stranu.<br>" +
                "Kod nepoznatih veličina unesite 0 (nulu).</html>");
        infoLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JButton startButton = new JButton("Počni unos");
        JButton cancelButton = new JButton("Otkaži");

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(startButton);
        buttonPanel.add(cancelButton);

        frame.add(infoLabel, BorderLayout.CENTER);
        frame.add(buttonPanel, BorderLayout.SOUTH);

        startButton.addActionListener(e -> pokreniUnos(frame));
        cancelButton.addActionListener(e -> frame.dispose());

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private static void pokreniUnos(JFrame frame) {
        DecimalFormat df = new DecimalFormat("#.###");
        double alfa, beta, gamma;
        double a, b, c;
        double obim, povrsina;

        try {
            alfa = unosDMSJednoPolje(frame, "Unesite ugao α u DMS:");
            beta = unosDMSJednoPolje(frame, "Unesite ugao β u DMS:");
            gamma = unosDMSJednoPolje(frame, "Unesite ugao γ u DMS:");

            a = unosVrednosti(frame, "Unesite stranicu a:");
            b = unosVrednosti(frame, "Unesite stranicu b:");
            c = unosVrednosti(frame, "Unesite stranicu c:");

            if (alfa > 0 && beta > 0 && gamma == 0) gamma = 180 - alfa - beta;
            else if (alfa > 0 && gamma > 0 && beta == 0) beta = 180 - alfa - gamma;
            else if (beta > 0 && gamma > 0 && alfa == 0) alfa = 180 - beta - gamma;

            double poluprecnik = 0;
            if (a > 0 && alfa > 0) poluprecnik = a / Math.sin(Math.toRadians(alfa));
            else if (b > 0 && beta > 0) poluprecnik = b / Math.sin(Math.toRadians(beta));
            else if (c > 0 && gamma > 0) poluprecnik = c / Math.sin(Math.toRadians(gamma));

            if (a == 0 && alfa > 0) a = poluprecnik * Math.sin(Math.toRadians(alfa));
            if (b == 0 && beta > 0) b = poluprecnik * Math.sin(Math.toRadians(beta));
            if (c == 0 && gamma > 0) c = poluprecnik * Math.sin(Math.toRadians(gamma));

            obim = a + b + c;
            povrsina = 0.5 * a * b * Math.sin(Math.toRadians(gamma));

            String alfaDMS = KoordinateMenadzer.formatirajDMS(alfa);
            String betaDMS = KoordinateMenadzer.formatirajDMS(beta);
            String gammaDMS = KoordinateMenadzer.formatirajDMS(gamma);

            prikaziRezultat(frame, alfaDMS, betaDMS, gammaDMS, a, b, c, obim, povrsina);

        } catch (Exception ex) {
            showError(frame, "Greška: " + ex.getMessage());
        }
    }

    private static void prikaziRezultat(JFrame frame, String alfaDMS, String betaDMS, String gammaDMS,
                                        double a, double b, double c, double obim, double povrsina) {
        String rezultat = String.format(
                "Rezultati rešavanja trougla:\n\n" +
                        "Uglovi:\n" +
                        "α: %s\n" +
                        "β: %s\n" +
                        "γ: %s\n\n" +
                        "Stranice:\n" +
                        "a: %.3f m\n" +
                        "b: %.3f m\n" +
                        "c: %.3f m\n\n" +
                        "Obim trougla: %.3f m\n" +
                        "Površina trougla: %.3f m²",
                alfaDMS, betaDMS, gammaDMS, a, b, c, obim, povrsina
        );

        showInfo(frame, rezultat);
    }

    private static double unosDMSJednoPolje(JFrame frame, String poruka) throws Exception {
        String input = JOptionPane.showInputDialog(frame, poruka);
        if (input == null || input.trim().isEmpty()) return 0;
        return KoordinateMenadzer.konvertujDMSuDecimale(Double.parseDouble(input.trim()));
    }

    private static double unosVrednosti(JFrame frame, String poruka) throws Exception {
        String input = JOptionPane.showInputDialog(frame, poruka);
        if (input == null || input.trim().isEmpty()) return 0;
        return Double.parseDouble(input.trim());
    }

    private static void showError(JFrame frame, String message) {
        JOptionPane.showMessageDialog(frame, message, "Greška", JOptionPane.ERROR_MESSAGE);
    }

    private static void showInfo(JFrame frame, String message) {
        JOptionPane.showMessageDialog(frame, message, "Informacija", JOptionPane.INFORMATION_MESSAGE);
    }
}
