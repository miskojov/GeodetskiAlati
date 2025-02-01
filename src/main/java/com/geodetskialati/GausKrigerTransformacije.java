// Verzija 2.0
package com.geodetskialati;

import org.locationtech.proj4j.*;

import javax.swing.*;
import java.awt.*;

public class GausKrigerTransformacije {

    public static void main(String[] args) {
        izvrsi();
    }

    public static void izvrsi() {
        JFrame frame = new JFrame("Gaus-Kriger Transformacije");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(600, 400);
        frame.setLayout(new BorderLayout());

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel infoLabel = new JLabel("Unesite Gaus-Kriger koordinate (Y, X, H):");
        JTextField gkYField = new JTextField(15);
        JTextField gkXField = new JTextField(15);
        JTextField gkHField = new JTextField(15);
        JButton transformButton = new JButton("Transformiši");
        JButton cancelButton = new JButton("Otkaži");

        panel.add(infoLabel);
        panel.add(new JLabel("GK Y (Easting):"));
        panel.add(gkYField);
        panel.add(new JLabel("GK X (Northing):"));
        panel.add(gkXField);
        panel.add(new JLabel("GK H (Visina):"));
        panel.add(gkHField);
        panel.add(Box.createVerticalStrut(10)); // Spacer
        panel.add(transformButton);
        panel.add(Box.createVerticalStrut(5)); // Spacer
        panel.add(cancelButton);

        frame.add(panel, BorderLayout.CENTER);

        transformButton.addActionListener(e -> {
            try {
                double gkY = Double.parseDouble(gkYField.getText().trim());
                double gkX = Double.parseDouble(gkXField.getText().trim());
                double gkH = Double.parseDouble(gkHField.getText().trim());

                String rezultat = transformCoordinatesAndFormat(gkY, gkX, gkH);
                showInfo(frame, rezultat);
            } catch (Exception ex) {
                showError(frame, "Greška: " + ex.getMessage());
            }
        });

        cancelButton.addActionListener(e -> frame.dispose());

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private static String transformCoordinatesAndFormat(double gkY, double gkX, double gkH) {
        ProjCoordinate wgs84Coord = transformCoordinates(gkY, gkX, "EPSG:6316", "EPSG:4326");
        ProjCoordinate utmCoord = transformCoordinates(wgs84Coord.x, wgs84Coord.y, "EPSG:4326", "EPSG:32634");

        String latitudeDMS = formatDMS(wgs84Coord.y);
        String longitudeDMS = formatDMS(wgs84Coord.x);
        String latitudeDM = formatDM(wgs84Coord.y);
        String longitudeDM = formatDM(wgs84Coord.x);

        String utmEasting = "34" + String.format("%.3f", utmCoord.x);

        return String.format(
                "Unete Gaus-Kriger koordinate (EPSG:6316):\n" +
                        "GK Y (Easting): %.3f\nGK X (Northing): %.3f\nGK H(FR-NVT2): %.3f\n\n" +
                        "Transformisane WGS84 koordinate (EPSG:4326):\n" +
                        "- Decimalni zapis:\nLatitude (φ): %.8f\nLongitude (λ): %.8f\nAltitude (H): %.3f\n\n" +
                        "- Stepeni i minuti:\nLatitude (DM): %s\nLongitude (DM): %s\n\n" +
                        "- Stepeni, minuti i sekunde:\nLatitude (DMS): %s\nLongitude (DMS): %s\n\n" +
                        "Transformisane UTM koordinate (EPSG:32634):\n" +
                        "UTM Easting (E): %s\nUTM Northing (N): %.3f\nUTM h: %.3f",
                gkY, gkX, gkH,
                wgs84Coord.y, wgs84Coord.x, gkH,
                latitudeDM, longitudeDM,
                latitudeDMS, longitudeDMS,
                utmEasting, utmCoord.y, gkH
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
