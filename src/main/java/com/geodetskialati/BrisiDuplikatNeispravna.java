// Verzija 2.0
package com.geodetskialati;

import java.io.*;
import java.nio.file.*;
import java.util.List; // Precizno navodimo java.util.List
import java.util.ArrayList;
import javax.swing.*;
import java.awt.*;

public class BrisiDuplikatNeispravna {

    public void izvrsi() {
        String userHome = System.getProperty("user.home");
        String folderPath = userHome + "\\Desktop\\GeodetskiAlati";
        String filePath = folderPath + "\\YXHkoordinate.csv";

        JFrame frame = new JFrame("Brisanje duplikata i neispravnih redova");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(500, 200);
        frame.setLayout(new BorderLayout());

        try {
            File folder = new File(folderPath);
            if (!folder.exists()) {
                showError(frame, "Folder 'GeodetskiAlati' ne postoji. Operacija je otkazana.");
                vratiNaPocetnuMasku();
                return;
            }

            File file = new File(filePath);
            if (!file.exists()) {
                showError(frame, "Datoteka 'YXHkoordinate.csv' ne postoji u folderu 'GeodetskiAlati'.");
                vratiNaPocetnuMasku();
                return;
            }

            String[] options = {
                    "Sve duplikate i neispravne tačke",
                    "Samo duplikate",
                    "Samo neispravne tačke",
                    "Odustajem od brisanja"
            };

            JComboBox<String> comboBox = new JComboBox<>(options);
            JButton confirmButton = new JButton("Izvrši brisanje");
            JButton cancelButton = new JButton("Otkaži");

            JPanel panel = new JPanel(new FlowLayout());
            panel.add(new JLabel("Izaberite opciju brisanja:"));
            panel.add(comboBox);
            frame.add(panel, BorderLayout.CENTER);

            JPanel buttonPanel = new JPanel();
            buttonPanel.add(confirmButton);
            buttonPanel.add(cancelButton);
            frame.add(buttonPanel, BorderLayout.SOUTH);

            confirmButton.addActionListener(e -> {
                String izbor = (String) comboBox.getSelectedItem();
                if (izbor.equals(options[3])) {
                    showInfo(frame, "Brisanje je otkazano.");
                    vratiNaPocetnuMasku();
                    frame.dispose();
                    return;
                }

                try {
                    List<String> linije = Files.readAllLines(file.toPath());
                    List<String> filtriraneLinije = new ArrayList<>();

                    for (String linija : linije) {
                        if (linija.trim().isEmpty()) {
                            continue;
                        }

                        boolean dodajLiniju = true;

                        if (izbor.equals(options[0]) && (linija.startsWith("DUPLIKAT") || linija.startsWith("NEISPRAVNA"))) {
                            dodajLiniju = false;
                        } else if (izbor.equals(options[1]) && linija.startsWith("DUPLIKAT")) {
                            dodajLiniju = false;
                        } else if (izbor.equals(options[2]) && linija.startsWith("NEISPRAVNA")) {
                            dodajLiniju = false;
                        }

                        if (dodajLiniju) {
                            filtriraneLinije.add(linija);
                        }
                    }

                    try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                        for (String linija : filtriraneLinije) {
                            writer.write(linija);
                            writer.newLine();
                        }
                    }

                    showInfo(frame, "Odabrani redovi su uspešno obrisani.");
                } catch (IOException ex) {
                    showError(frame, "Greška prilikom obrade datoteke: " + ex.getMessage());
                }
            });

            cancelButton.addActionListener(e -> {
                frame.dispose();
                vratiNaPocetnuMasku();
            });

            frame.setLocationRelativeTo(null);
            frame.setVisible(true);

        } catch (Exception e) {
            showError(frame, "Greška: " + e.getMessage());
        }
    }

    private void showError(JFrame frame, String message) {
        JOptionPane.showMessageDialog(frame, message, "Greška", JOptionPane.ERROR_MESSAGE);
    }

    private void showInfo(JFrame frame, String message) {
        JOptionPane.showMessageDialog(frame, message, "Informacija", JOptionPane.INFORMATION_MESSAGE);
    }

    private void vratiNaPocetnuMasku() {
        Main.main(null);
    }
}
