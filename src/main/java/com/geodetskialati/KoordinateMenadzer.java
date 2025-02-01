// Verzija 1.64
package com.geodetskialati;

import javax.swing.*;
import java.io.*;
import java.text.DecimalFormat;
import java.util.*;

public class KoordinateMenadzer {
    private final String filePath;
    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("0.000");

    public KoordinateMenadzer() {
        String desktopPath = System.getProperty("user.home") + "\\Desktop";
        String folderPath = desktopPath + "\\GeodetskiAlati";
        this.filePath = folderPath + "\\YXHkoordinate.csv";

        try {
            File folder = new File(folderPath);
            if (!folder.exists() && !folder.mkdir()) {
                throw new IOException("Folder 'GeodetskiAlati' nije mogao biti kreiran na Desktopu.");
            }

            File file = new File(filePath);
            if (!file.exists() && !file.createNewFile()) {
                throw new IOException("Datoteka 'YXHkoordinate.csv' nije mogla biti kreirana u folderu 'GeodetskiAlati'.");
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(
                    null,
                    "Greška prilikom inicijalizacije: " + e.getMessage(),
                    "Greška",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    public boolean podaciZahtevajuAzuriranje() throws IOException {
        File file = new File(filePath);
        if (!file.exists()) {
            return false;
        }

        boolean needsUpdate = false;
        boolean hasBlankLines = false;
        Set<String> existingNazivi = new HashSet<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    hasBlankLines = true;
                    continue; // Detektovan blanko red
                }

                // Provera da li su podaci razdvojeni zarezima sa blanko karakterima
                if (line.matches(".*\\s*,\\s*.*")) {
                    needsUpdate = true; // Ako postoji zarez sa blanko karakterima, potrebno je ažuriranje
                }

                // Provera za duple razmake kao u prethodnim zahtevima
                if (line.matches(".*\\s{2,}.*")) {
                    needsUpdate = true; // Ako postoje dupli razmaci, potrebno je ažuriranje
                }

                String cleanedLine = cleanAndFormatLine(line);
                String[] parts = cleanedLine.split(";");

                if (parts.length < 3 || parts.length > 4) {
                    return true; // Neispravan broj kolona
                }

                for (int i = 1; i < parts.length; i++) {
                    if (!canBeFormattedAsCoordinate(parts[i])) {
                        return true; // Nevalidna koordinata ili kota
                    }
                    if (!parts[i].equals(formatCoordinate(parts[i]))) {
                        needsUpdate = true; // Potrebno zaokruživanje
                    }
                }

                String upperCaseNaziv = parts[0].toUpperCase();
                if (existingNazivi.contains(upperCaseNaziv) && !parts[0].startsWith("DUPLIKAT")) {
                    needsUpdate = true; // Duplikat zahteva ažuriranje
                } else {
                    existingNazivi.add(upperCaseNaziv);
                }
            }
        }

        // Ažuriranje se pokreće ako ima blanko redova ili podataka razdvojenih zarezima ili blanko karakterima
        return needsUpdate || hasBlankLines;
    }



    public void validateAndAdjustFile() throws IOException {
        File file = new File(filePath);
        if (!file.exists()) {
            file.createNewFile();
            return;
        }

        List<String[]> adjustedLines = new ArrayList<>();
        Set<String> existingNazivi = new HashSet<>();
        boolean updated = false;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue;
                }

                String cleanedLine = cleanAndFormatLine(line);
                String[] parts = cleanedLine.split(";");

                if (isValidLine(parts)) {
                    parts = adjustValidLine(parts);
                    updated = true;

                    String upperCaseNaziv = parts[0].toUpperCase();
                    if (existingNazivi.contains(upperCaseNaziv)) {
                        if (!parts[0].startsWith("DUPLIKAT")) {
                            parts[0] = "DUPLIKAT" + parts[0];
                            updated = true;
                        }
                    } else {
                        existingNazivi.add(upperCaseNaziv);
                    }
                } else {
                    parts = markAsInvalid(parts);
                    updated = true;
                }

                adjustedLines.add(parts);
            }
        }

        if (updated) {
            writeAdjustedLinesToFile(adjustedLines, file);
        }
    }

    private String cleanAndFormatLine(String line) {
        line = line.trim();
        line = line.replaceAll("\\s*,\\s*", ";"); // Zarez u tačka-zarez
        line = line.replaceAll("\\s+", " ");     // Ukloni višestruke razmake
        line = line.replaceAll(" ", ";");        // Zameni razmake tačka-zarezom
        line = line.replaceAll(";+", ";");       // Ukloni višestruke tačka-zareze
        return line;
    }

    private boolean isValidLine(String[] parts) {
        if (parts.length < 3 || parts.length > 4) {
            return false; // Broj kolona mora biti 3 ili 4
        }

        for (int i = 1; i < parts.length; i++) {
            if (!canBeFormattedAsCoordinate(parts[i])) {
                return false; // Nevalidna vrednost
            }
        }

        return true;
    }

    private String[] adjustValidLine(String[] parts) {
        for (int i = 1; i < parts.length; i++) {
            parts[i] = formatCoordinate(parts[i]);
        }

        if (parts.length == 3) {
            parts = new String[]{parts[0], parts[1], parts[2], "0.000"};
        }

        return parts;
    }

    private String[] markAsInvalid(String[] parts) {
        if (!parts[0].startsWith("NEISPRAVNA")) {
            parts[0] = "NEISPRAVNA" + parts[0];
        }
        return parts;
    }

    private void writeAdjustedLinesToFile(List<String[]> adjustedLines, File file) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            for (String[] parts : adjustedLines) {
                writer.write(String.join(";", parts));
                writer.newLine();
            }
        }

        JOptionPane.showMessageDialog(
                null,
                "Datoteka 'YXHkoordinate.csv' je uspešno ažurirana.",
                "Informacija",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    private boolean canBeFormattedAsCoordinate(String value) {
        try {
            value = value.replace(',', '.');
            Double.parseDouble(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private String formatCoordinate(String value) {
        value = value.replace(',', '.');
        try {
            double coordinate = Double.parseDouble(value);
            return DECIMAL_FORMAT.format(coordinate);
        } catch (NumberFormatException e) {
            return value;
        }
    }

    public boolean postojeKoordinate(String nazivTacke) throws IOException {
        File file = new File(filePath);
        if (!file.exists()) {
            return false;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] delovi = cleanAndFormatLine(line).split(";");
                if (delovi.length > 0 && delovi[0].equalsIgnoreCase(nazivTacke)) {
                    return true;
                }
            }
        }
        return false;
    }

    public double[] preuzmiKoordinate(String naziv) throws IOException {
        File file = new File(filePath);
        if (!file.exists()) {
            throw new IOException("Datoteka 'YXHkoordinate.csv' ne postoji.");
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] delovi = cleanAndFormatLine(line).split(";");
                if (delovi[0].equalsIgnoreCase(naziv)) {
                    if (delovi.length < 3) {
                        JOptionPane.showMessageDialog(
                                null,
                                "Tačka '" + naziv + "' nema ispravne koordinate.",
                                "Greška",
                                JOptionPane.ERROR_MESSAGE
                        );
                        break;
                    }
                    double y = Double.parseDouble(delovi[1]);
                    double x = Double.parseDouble(delovi[2]);
                    return new double[]{y, x};
                }
            }
        }

        JOptionPane.showMessageDialog(
                null,
                "Tačka '" + naziv + "' nije pronađena u datoteci.",
                "Greška",
                JOptionPane.ERROR_MESSAGE
        );
        throw new IOException("Tačka nije pronađena.");
    }

    public void dodajIliAzuriraj(String nazivTacke, double y, double x, double h) throws IOException {
        File file = new File(filePath);
        List<String[]> tacke = new ArrayList<>();

        if (file.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    tacke.add(line.split(";"));
                }
            }
        }

        boolean found = false;
        for (String[] tacka : tacke) {
            if (tacka[0].equalsIgnoreCase(nazivTacke)) {
                tacka[1] = DECIMAL_FORMAT.format(y);
                tacka[2] = DECIMAL_FORMAT.format(x);
                tacka[3] = DECIMAL_FORMAT.format(h);
                found = true;
                break;
            }
        }

        if (!found) {
            tacke.add(new String[]{
                    nazivTacke,
                    DECIMAL_FORMAT.format(y),
                    DECIMAL_FORMAT.format(x),
                    DECIMAL_FORMAT.format(h)
            });
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            for (String[] tacka : tacke) {
                writer.write(String.join(";", tacka));
                writer.newLine();
            }
        }
    }

    public static String formatirajDMS(double ugao) {
        boolean negativan = ugao < 0;
        ugao = Math.abs(ugao);
        int stepeni = (int) ugao;
        double minutniDeo = (ugao - stepeni) * 60;
        int minuti = (int) minutniDeo;
        int sekunde = (int) Math.round((minutniDeo - minuti) * 60);

        if (sekunde == 60) {
            sekunde = 0;
            minuti++;
            if (minuti == 60) {
                minuti = 0;
                stepeni++;
            }
        }

        return String.format("%s%d°%02d'%02d\"",
                negativan ? "-" : "", stepeni, minuti, sekunde);
    }

    public static double konvertujDMSuDecimale(double dms) {
        int stepeni = (int) dms; // Stepeni su deo pre decimalne tačke
        double decimalniDeo = Math.abs(dms - stepeni); // Deo iza decimalne tačke
        int minuti = (int) (decimalniDeo * 100); // Prva dva broja iza decimalne tačke
        double sekunde = (decimalniDeo * 100 - minuti) * 100; // Sledeća dva broja

        // Konverzija u decimale
        double decimalniStepeni = stepeni + (minuti / 60.0) + (sekunde / 3600.0);
        return decimalniStepeni;
    }
}
