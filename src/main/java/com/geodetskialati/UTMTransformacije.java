// Verzija 2.0
package com.geodetskialati;

import org.locationtech.proj4j.*;

import javax.swing.*;
import java.awt.*;

public class UTMTransformacije {

    public static void main(String[] args) {
        izvrsi();
    }

    public static void izvrsi() {
        JFrame frame = new JFrame("UTM Transformacije");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(500, 300);
        frame.setLayout(new BorderLayout());

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel infoLabel = new JLabel("Unesite UTM koordinate:");
        JTextField utmEField = new JTextField(15);
        JTextField utmNField = new JTextField(15);
        JTextField utmHField = new JTextField(15);

        JButton transformButton = new JButton("Transformiši");
        JButton cancelButton = new JButton("Otkaži");

        panel.add(infoLabel);
        panel.add(new JLabel("UTM Easting (E) sa prefiksom '34':"));
        panel.add(utmEField);
        panel.add(new JLabel("UTM Northing (N):"));
        panel.add(utmNField);
        panel.add(new JLabel("Altitude (H):"));
        panel.add(utmHField);
        panel.add(Box.createVerticalStrut(10)); // Spacer
        panel.add(transformButton);
        panel.add(Box.createVerticalStrut(5)); // Spacer
        panel.add(cancelButton);

        frame.add(panel, BorderLayout.CENTER);

        transformButton.addActionListener(e -> {
            try {
                String utmEInput = utmEField.getText().trim();
                String utmNInput = utmNField.getText().trim();
                String utmHInput = utmHField.getText().trim();

                if (!utmEInput.startsWith("34")) {
                    showError(frame, "UTM Easting mora početi sa prefiksom '34'.");
                    return;
                }

                double utmE = Double.parseDouble(utmEInput.substring(2));
                double utmN = Double.parseDouble(utmNInput);
                double utmH = Double.parseDouble(utmHInput);

                String rezultat = transformCoordinatesAndFormat(utmE, utmN, utmH);
                showInfo(frame, rezultat);
            } catch (NumberFormatException ex) {
                showError(frame, "Greška prilikom parsiranja: Unesite validne numeričke vrednosti.");
            } catch (Exception ex) {
                showError(frame, "Došlo je do greške: " + ex.getMessage());
            }
        });

        cancelButton.addActionListener(e -> frame.dispose());

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private static String transformCoordinatesAndFormat(double utmE, double utmN, double utmH) {
        ProjCoordinate wgs84Coord = transformCoordinates(utmE, utmN, "EPSG:32634", "EPSG:4326");
        ProjCoordinate gkCoord = transformCoordinates(wgs84Coord.x, wgs84Coord.y, "EPSG:4326", "EPSG:6316");

        String latitudeDMS = formatDMS(wgs84Coord.y);
        String longitudeDMS = formatDMS(wgs84Coord.x);
        String latitudeDM = formatDM(wgs84Coord.y);
        String longitudeDM = formatDM(wgs84Coord.x);

        return String.format(
                "Unete UTM koordinate (EPSG:32634):\n" +
                        "UTM Easting (E): 34%.3f\nUTM Northing (N): %.3f\nUTM h: %.3f\n\n" +
                        "Transformisane WGS84 koordinate (EPSG:4326):\n" +
                        "Latitude (φ): %.8f\nLongitude (λ): %.8f\nAltitude (H): %.3f\n\n" +
                        "- Stepeni i minuti:\nLatitude (DM): %s\nLongitude (DM): %s\n\n" +
                        "- Stepeni, minuti i sekunde:\nLatitude (DMS): %s\nLongitude (DMS): %s\n\n" +
                        "Transformisane Gaus-Krigerove koordinate (EPSG:6316):\n" +
                        "GK Y (Easting): %.3f\nGK X (Northing): %.3f\nGK H(FR-NVT2): %.3f",
                utmE, utmN, utmH,
                wgs84Coord.y, wgs84Coord.x, utmH,
                latitudeDM, longitudeDM,
                latitudeDMS, longitudeDMS,
                gkCoord.x, gkCoord.y, utmH - 45.1107 // Korekcija visine za Gaus-Kriger
        );
    }

    private static String formatDMS(double decimalDegree) {
        int degrees = (int) decimalDegree;
        double fractionalPart = Math.abs(decimalDegree - degrees);
        int minutes = (int) (fractionalPart * 60);
        double seconds = (fractionalPart * 60 - minutes) * 60;
        return String.format("%d°%02d'%06.3f\"", degrees, minutes, seconds);
    }

    private static String formatDM(double decimalDegree) {
        int degrees = (int) decimalDegree;
        double fractionalPart = Math.abs(decimalDegree - degrees);
        double minutes = fractionalPart * 60;
        return String.format("%d°%06.3f'", degrees, minutes);
    }

    private static ProjCoordinate transformCoordinates(double x, double y, String srcCRS, String destCRS) {
        CRSFactory crsFactory = new CRSFactory();
        CoordinateReferenceSystem sourceCRS = crsFactory.createFromName(srcCRS);
        CoordinateReferenceSystem destinationCRS = crsFactory.createFromName(destCRS);

        CoordinateTransformFactory ctFactory = new CoordinateTransformFactory();
        CoordinateTransform transform = ctFactory.createTransform(sourceCRS, destinationCRS);

        ProjCoordinate srcCoord = new ProjCoordinate(x, y);
        ProjCoordinate dstCoord = new ProjCoordinate();
        transform.transform(srcCoord, dstCoord);

        return dstCoord;
    }

    private static void showError(JFrame frame, String message) {
        JOptionPane.showMessageDialog(frame, message, "Greška", JOptionPane.ERROR_MESSAGE);
    }

    private static void showInfo(JFrame frame, String message) {
        JOptionPane.showMessageDialog(frame, message, "Informacija", JOptionPane.INFORMATION_MESSAGE);
    }
}
