// Verzija 2.0
package com.geodetskialati;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.awt.Font;

public class Main {

    private static boolean isMainMenuOpen = false; // Praćenje statusa glavnog menija

    public static void main(String[] args) {
        if (isMainMenuOpen) {
            return; // Ako je meni već otvoren, ne otvaraj ponovo
        }

        isMainMenuOpen = true; // Postavi status na otvoreno

        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Geodetski alati");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(600, 400);

            // Dodavanje događaja za zatvaranje prozora
            frame.addWindowListener(new java.awt.event.WindowAdapter() {
                @Override
                public void windowClosing(java.awt.event.WindowEvent e) {
                    isMainMenuOpen = false; // Resetovanje stanja pri zatvaranju
                }
            });

            // Kreiranje glavnog stabla
            DefaultMutableTreeNode root = new DefaultMutableTreeNode("Glavni meni");

            // Glavne kategorije
            DefaultMutableTreeNode bazaPodataka = createStyledNode("BAZA PODATAKA");
            DefaultMutableTreeNode trigonomObrasci = createStyledNode("TRIGONOMETRIJSKI OBRASCI");
            DefaultMutableTreeNode transformacijaKoordinata = createStyledNode("TRANSFORMACIJA KOORDINATA");

            // Dodavanje podkategorija
            bazaPodataka.add(new DefaultMutableTreeNode("Unos YXH koordinata"));
            bazaPodataka.add(new DefaultMutableTreeNode("Pregled YXH koordinata"));
            bazaPodataka.add(new DefaultMutableTreeNode("Brisanje duplih i neispravnih tačaka"));
            bazaPodataka.add(new DefaultMutableTreeNode("Konvertovanje YXHkoordinate.csv u TXT i Excel"));

            trigonomObrasci.add(new DefaultMutableTreeNode("TO 8 - Računanje nagiba i dužine"));
            trigonomObrasci.add(new DefaultMutableTreeNode("Obrnuti TO 8 - Računanje koordinata iz nagiba i dužine"));
            trigonomObrasci.add(new DefaultMutableTreeNode("Tahimetrijski zapisnik"));
            trigonomObrasci.add(new DefaultMutableTreeNode("Rešavanje trougla (TO 13 , TO 14)"));

            transformacijaKoordinata.add(new DefaultMutableTreeNode("WGS84 Transformacije"));
            transformacijaKoordinata.add(new DefaultMutableTreeNode("Gaus-Kriger Transformacije"));
            transformacijaKoordinata.add(new DefaultMutableTreeNode("UTM Transformacije"));

            // Dodavanje kategorija u stablo
            root.add(bazaPodataka);
            root.add(trigonomObrasci);
            root.add(transformacijaKoordinata);

            // Kreiranje stabla
            JTree tree = new JTree(new DefaultTreeModel(root));
            tree.setFont(new Font("Arial", Font.PLAIN, 14));

            // Automatsko otvaranje svih čvorova
            for (int i = 0; i < tree.getRowCount(); i++) {
                tree.expandRow(i);
            }

            // Dodavanje događaja za selekciju čvorova
            tree.addTreeSelectionListener(event -> {
                TreePath selectedPath = tree.getSelectionPath();
                if (selectedPath != null) {
                    String selectedNode = selectedPath.getLastPathComponent().toString();
                    izvrsiAkciju(selectedNode);
                }
            });

            // Dodavanje stabla u scroll pane
            JScrollPane scrollPane = new JScrollPane(tree);
            frame.add(scrollPane);
            frame.setLocationRelativeTo(null); // Centriranje prozora
            frame.setVisible(true);
        });
    }

    private static void izvrsiAkciju(String selectedNode) {
        KoordinateMenadzer koordinateMenadzer = new KoordinateMenadzer();
        switch (selectedNode) {
            case "Unos YXH koordinata" -> UnosKoordinataPanel.showFrame(koordinateMenadzer);
            case "Pregled YXH koordinata" -> new PregledKoordinata().izvrsi();
            case "Brisanje duplih i neispravnih tačaka" -> new BrisiDuplikatNeispravna().izvrsi();
            case "Konvertovanje YXHkoordinate.csv u TXT i Excel" -> new KonvertCSVuTXTiXLS().izvrsi();
            case "TO 8 - Računanje nagiba i dužine" -> new TO8(koordinateMenadzer).izvrsi();
            case "Obrnuti TO 8 - Računanje koordinata iz nagiba i dužine" -> new ObrnutiTO8(koordinateMenadzer).izvrsi();
            case "Tahimetrijski zapisnik" -> new Tahimetrija(koordinateMenadzer).izvrsi();
            case "Rešavanje trougla (TO 13 , TO 14)" -> new ResavanjeTrougla().izvrsi();
            case "WGS84 Transformacije" -> new WGS84Transformacije().izvrsi();
            case "Gaus-Kriger Transformacije" -> new GausKrigerTransformacije().izvrsi();
            case "UTM Transformacije" -> new UTMTransformacije().izvrsi();
            default -> JOptionPane.showMessageDialog(null, "Nepoznata opcija: " + selectedNode);
        }
    }

    private static DefaultMutableTreeNode createStyledNode(String text) {
        return new DefaultMutableTreeNode("<html><b><u>" + text + "</u></b></html>");
    }
}
