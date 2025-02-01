// Verzija 2.0
package com.geodetskialati;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.nio.file.*;
import java.util.List; // Precizno koristimo java.util.List
import java.util.ArrayList;

public class KonvertCSVuTXTiXLS {

    public static void izvrsi() {
        String userHome = System.getProperty("user.home");
        String folderPath = userHome + "\\Desktop\\GeodetskiAlati";
        String csvFilePath = folderPath + "\\YXHkoordinate.csv";
        String txtFilePath = folderPath + "\\YXHkoordinate.txt";
        String xlsFilePath = folderPath + "\\YXHkoordinate.xlsx";

        JFrame frame = new JFrame("Konvertovanje CSV u TXT i XLS");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(500, 200);
        frame.setLayout(new BorderLayout());

        try {
            // Proveri postojanje foldera
            File folder = new File(folderPath);
            if (!folder.exists()) {
                showError(frame, "Folder 'GeodetskiAlati' ne postoji. Operacija je otkazana.");
                vratiNaPocetnuMasku();
                return;
            }

            // Proveri postojanje CSV datoteke
            if (!Files.exists(Paths.get(csvFilePath))) {
                showError(frame, "CSV datoteka ne postoji: " + csvFilePath);
                vratiNaPocetnuMasku();
                return;
            }

            // Provera da li TXT i XLS fajlovi već postoje
            boolean txtExists = Files.exists(Paths.get(txtFilePath));
            boolean xlsExists = Files.exists(Paths.get(xlsFilePath));

            if (txtExists || xlsExists) {
                int odgovor = showConfirm(frame, "Jedan ili oba fajla (YXHkoordinate.txt, YXHkoordinate.xlsx) već postoje. Da li želite da ih pregazite?");
                if (odgovor != JOptionPane.YES_OPTION) {
                    vratiNaPocetnuMasku();
                    return;
                }
            }

            // Učitavanje podataka iz CSV fajla
            List<String[]> csvData = ucitajCSV(csvFilePath);

            // Kreiranje TXT i XLS fajlova
            kreirajTXT(txtFilePath, csvData);
            kreirajXLS(xlsFilePath, csvData);

            showInfo(frame, "Datoteke su uspešno konvertovane i postavljene u folder 'GeodetskiAlati'.");

        } catch (IOException e) {
            showError(frame, "Greška prilikom konverzije: " + e.getMessage());
        } finally {
            vratiNaPocetnuMasku();
        }
    }

    private static List<String[]> ucitajCSV(String filePath) throws IOException {
        List<String[]> tackeList = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    tackeList.add(line.split(";"));
                }
            }
        }
        return tackeList;
    }

    private static void kreirajTXT(String filePath, List<String[]> tackeList) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            for (String[] tacka : tackeList) {
                writer.write(String.join(" ", tacka));
                writer.newLine();
            }
        }
    }

    private static void kreirajXLS(String filePath, List<String[]> tackeList) throws IOException {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("YX Koordinate");

            int rowNum = 0;
            for (String[] tacka : tackeList) {
                Row row = sheet.createRow(rowNum++);
                for (int i = 0; i < tacka.length; i++) {
                    row.createCell(i).setCellValue(tacka[i]);
                }
            }

            try (FileOutputStream fileOut = new FileOutputStream(filePath)) {
                workbook.write(fileOut);
            }
        }
    }

    private static int showConfirm(JFrame frame, String message) {
        return JOptionPane.showConfirmDialog(frame, message, "Potvrda pregazivanja", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
    }

    private static void showError(JFrame frame, String message) {
        JOptionPane.showMessageDialog(frame, message, "Greška", JOptionPane.ERROR_MESSAGE);
    }

    private static void showInfo(JFrame frame, String message) {
        JOptionPane.showMessageDialog(frame, message, "Informacija", JOptionPane.INFORMATION_MESSAGE);
    }

    private static void vratiNaPocetnuMasku() {
        Main.main(null);
    }
}
