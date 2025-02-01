// Verzija 2.0
package com.geodetskialati;

import org.locationtech.proj4j.*;

import javax.swing.*;
import java.awt.*;

public class WGS84Transformacije {

    public static void main(String[] args) {
        izvrsi();
    }

    public static void izvrsi() {
        JFrame frame = new JFrame("WGS84 Transformacije");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(600, 400);
        frame.setLayout(new BorderLayout());

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel infoLabel = new JLabel("Unesite WGS84 koordinate (latitude, longitude, altitude):");
        JTextField latField = new JTextField(15);
        JTextField lonField = new JTextField(15);
        JTextField altField = new JTextField(15);
        JButton transformButton = new JButton("Transformiši");
        JButton cancelButton = new JButton("Otkaži");

        panel.add(infoLabel);
        panel.add(new JLabel("Latitude (φ):"));
        panel.add(latField);
        panel.add(new JLabel("Longitude (λ):"));
        panel.add(lonField);
        panel.add(new JLabel("Altitude (m):"));
        panel.add(altField);
        panel.add(Box.createVerticalStrut(10)); // Spacer
        panel.add(transformButton);
        panel.add(Box.createVerticalStrut(5)); // Spacer
        panel.add(cancelButton);

        frame.add(panel, BorderLayout.CENTER);

        transformButton.addActionListener(e -> {
            try {
                double latitude = Double.parseDouble(latField.getText().trim());
                double longitude = Double.parseDouble(lonField.getText().trim());
                double altitude = Double.parseDouble(altField.getText().trim());

                String rezultat = transformCoordinatesAndFormat(latitude, longitude, altitude);
                showInfo(frame, rezultat);
            } catch (Exception ex) {
                showError(frame, "Greška: " + ex.getMessage());
            }
        });

        cancelButton.addActionListener(e -> frame.dispose());

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private static String transformCoordinatesAndFormat(double latitude, double longitude, double altitude) {
        String latitudeDMS = formatDMS(latitude);
        String longitudeDMS = formatDMS(longitude);
        String latitudeDM = formatDM(latitude);
        String longitudeDM = formatDM(longitude);

        ProjCoordinate gkCoord = transformCoordinates(latitude, longitude, "EPSG:4326", "EPSG:6316");
        double correctedGK_Y = gkCoord.x;
        double correctedGK_X = gkCoord.y;
        double correctedGK_H = altitude - 45.1107;

        ProjCoordinate utmCoord = transformCoordinates(latitude, longitude, "EPSG:4326", "EPSG:32634");
        String utmEasting = "34" + String.format("%.3f", utmCoord.x);

        return String.format(
                "Unete WGS84 koordinate:\n" +
                        "- Decimalni zapis:\nLatitude (φ): %.8f\nLongitude (λ): %.8f\nAltitude (N): %.3f m\n\n" +
                        "- Stepeni i minuti:\nLatitude (DM): %s\nLongitude (DM): %s\n\n" +
                        "- Stepeni, minuti i sekunde:\nLatitude (DMS): %s\nLongitude (DMS): %s\n\n" +
                        "Gaus-Kriger (EPSG: 6316):\n" +
                        "GK Y (Easting): %.3f\nGK X (Northing): %.3f\nGK H(FR-NVT2): %.4f\n\n" +
                        "UTM (EPSG: 32634):\n" +
                        "UTM Easting (E): %s\nUTM Northing (N): %.3f\nUTM h: %.3f",
                latitude, longitude, altitude,
                latitudeDM, longitudeDM,
                latitudeDMS, longitudeDMS,
                correctedGK_Y, correctedGK_X, correctedGK_H,
                utmEasting, utmCoord.y, altitude
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

    private static ProjCoordinate transformCoordinates(double latitude, double longitude, String srcCRS, String destCRS) {
        CRSFactory crsFactory = new CRSFactory();
        CoordinateReferenceSystem sourceCRS = crsFactory.createFromName(srcCRS);
        CoordinateReferenceSystem destinationCRS = crsFactory.createFromName(destCRS);

        CoordinateTransformFactory ctFactory = new CoordinateTransformFactory();
        CoordinateTransform transform = ctFactory.createTransform(sourceCRS, destinationCRS);

        ProjCoordinate srcCoord = new ProjCoordinate(longitude, latitude);
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
