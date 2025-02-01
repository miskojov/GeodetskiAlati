// Verzija 2.0
package com.geodetskialati;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class UnosKoordinataPanel extends JPanel {
    private final KoordinateMenadzer koordinateMenadzer;
    private JTextField nazivField, yField, xField, hField;
    private JLabel messageLabel;

    public UnosKoordinataPanel(KoordinateMenadzer koordinateMenadzer) {
        this.koordinateMenadzer = koordinateMenadzer;
        initUI();
    }

    private void initUI() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Naziv tačke
        add(new JLabel("Naziv tačke:"), gbc);
        nazivField = new JTextField(20);
        gbc.gridx = 1;
        add(nazivField, gbc);

        // Y koordinata
        gbc.gridx = 0;
        gbc.gridy = 1;
        add(new JLabel("Y koordinata:"), gbc);
        yField = new JTextField(20);
        gbc.gridx = 1;
        add(yField, gbc);

        // X koordinata
        gbc.gridx = 0;
        gbc.gridy = 2;
        add(new JLabel("X koordinata:"), gbc);
        xField = new JTextField(20);
        gbc.gridx = 1;
        add(xField, gbc);

        // H kota
        gbc.gridx = 0;
        gbc.gridy = 3;
        add(new JLabel("H kota:"), gbc);
        hField = new JTextField(20);
        gbc.gridx = 1;
        add(hField, gbc);

        // Poruka
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        messageLabel = new JLabel(" ");
        messageLabel.setForeground(Color.RED);
        add(messageLabel, gbc);

        // Dugmad
        JPanel buttonPanel = new JPanel();
        JButton saveButton = new JButton("Sačuvaj");
        saveButton.addActionListener(new SaveAction());
        JButton cancelButton = new JButton("Otkaži");
        cancelButton.addActionListener(e -> Main.main(null)); // Povratak na glavni meni
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        add(buttonPanel, gbc);
    }

    private class SaveAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                String naziv = nazivField.getText().trim();
                double y = Double.parseDouble(yField.getText().trim());
                double x = Double.parseDouble(xField.getText().trim());
                double h = Double.parseDouble(hField.getText().trim());

                if (naziv.isEmpty()) {
                    messageLabel.setText("Naziv tačke je obavezan.");
                    return;
                }

                if (koordinateMenadzer.postojeKoordinate(naziv.toUpperCase())) {
                    int odgovor = JOptionPane.showConfirmDialog(
                            UnosKoordinataPanel.this,
                            "Tačka sa tim nazivom već postoji. Želite li da je pregazite?",
                            "Potvrda pregazivanja",
                            JOptionPane.YES_NO_OPTION
                    );

                    if (odgovor == JOptionPane.NO_OPTION) {
                        return;
                    }
                }

                koordinateMenadzer.dodajIliAzuriraj(naziv, y, x, h);
                messageLabel.setForeground(Color.GREEN);
                messageLabel.setText("Koordinata uspešno sačuvana.");
            } catch (NumberFormatException ex) {
                messageLabel.setText("Greška u unosu. Proverite format podataka.");
            } catch (IOException ex) {
                messageLabel.setText("Greška prilikom čuvanja: " + ex.getMessage());
            }
        }
    }

    public static void showFrame(KoordinateMenadzer koordinateMenadzer) {
        JFrame frame = new JFrame("Unos Koordinata");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setContentPane(new UnosKoordinataPanel(koordinateMenadzer));
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
