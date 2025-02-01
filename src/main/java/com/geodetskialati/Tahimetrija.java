// Verzija 1.22
package com.geodetskialati;

import javax.swing.*;
import java.io.*;
import java.text.DecimalFormat;

public class Tahimetrija {

    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#.000");
    private final KoordinateMenadzer koordinateMenadzer;

    public Tahimetrija(KoordinateMenadzer koordinateMenadzer) {
        this.koordinateMenadzer = koordinateMenadzer;
    }

    public void izvrsi() {
        try {
            // Izbor opažanja na jednu ili dve orijentacije
            String[] opcije = {"Jedna orijentacija", "Dve orijentacije"};
            int izbor = JOptionPane.showOptionDialog(
                    null,
                    "Da li je opažanje vršeno sa jednom ili dve orijentacije?",
                    "Izbor orijentacije",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.INFORMATION_MESSAGE,
                    null,
                    opcije,
                    opcije[0]
            );

// Ako korisnik zatvori prozor ili klikne na Cancel
            if (izbor == JOptionPane.CLOSED_OPTION) {
                vratiNaPocetnuMasku(); // Povratak na osnovni meni
                return;
            }

            String nazivA, nazivB, nazivC;
            double[] koordinateA, koordinateB, koordinateC;
            double Ya, Xa, Yb, Xb, Yc, Xc, P, H, Pk, Mk;

            // Unos tačaka A, B, i C samo jednom
            nazivA = unosNazivaTacke("Unesite naziv tačke A:");
            koordinateA = koordinateMenadzer.preuzmiKoordinate(nazivA);
            Ya = koordinateA[0];
            Xa = koordinateA[1];

            nazivB = unosNazivaTacke("Unesite naziv tačke B:");
            koordinateB = koordinateMenadzer.preuzmiKoordinate(nazivB);
            Yb = koordinateB[0];
            Xb = koordinateB[1];

            P = KoordinateMenadzer.konvertujDMSuDecimale(unosKoordinate("Unesite Orjentacioni ugao na B:"));

            if (izbor == 0) { // Jedna orijentacija
                Yc = Yb;
                Xc = Xb;
                H = P;
            } else { // Dve orijentacije
                nazivC = unosNazivaTacke("Unesite naziv tačke C:");
                koordinateC = koordinateMenadzer.preuzmiKoordinate(nazivC);
                Yc = koordinateC[0];
                Xc = koordinateC[1];

                H = KoordinateMenadzer.konvertujDMSuDecimale(unosKoordinate("Unesite Orjentacioni ugao na C:"));
            }

            // Računanje odstupanja pre unosa ugla beta
            double U = Yb - Ya; // Razlika Y koordinata
            double V = Xb - Xa; // Razlika X koordinata
            if (V == 0) V = 1E-20; // Sprečavanje deljenja nulom

            double K = U / V; // Nagib
            double C = Math.toDegrees(Math.atan(K)) + (1 - Math.signum(V)) * 90; // Ugao C
            if (C < 0) C += 360; // Korekcija negativnog ugla

// Računanje razlika za nagib između C i A
            double Uc = Yc - Ya;
            double Vc = Xc - Xa;
            if (Vc == 0) Vc = 1E-20; // Sprečavanje deljenja nulom

// Izračunavanje nagiba između C i B
            double Kc = Uc / Vc;

// Izračunavanje odstupanja
            double T = Math.toDegrees(Math.atan(Kc)) + (1 - Math.signum(Vc)) * 90; // Ugao T
            if (T < 0) T += 360; // Korekcija negativnog ugla
            double G = T - C;

// Korekcija za negativne uglove
            if (G < 0) G += 360;

// Korekcija za razliku orijentacionih uglova
            double L = H - P;
            if (L < 0) L += 360;

            G -= L;

// Dovođenje odstupanja u opseg od -360° do 360°
            if (G > 360) {
                G -= 360;
            } else if (G < -360) {
                G += 360;
            }

// Prikaz odstupanja u DMS formatu sa ispravnim negativnim stepenima
            String odstupanjeDMS = KoordinateMenadzer.formatirajDMS(G);
            JOptionPane.showMessageDialog(null, "Odstupanje: " + odstupanjeDMS);

// Korekcija orijentacionog ugla na B
           Pk = P - G / 2;
           if (Pk < 0) Pk += 360;

            // Ponovna petlja za unos ugla beta
            while (true) {
                C = Math.toDegrees(Math.atan2(Yb - Ya, Xb - Xa));

                // Unos beta
                String betaInput = JOptionPane.showInputDialog("Unesite ugao (beta):");
                if (betaInput == null || betaInput.trim().isEmpty()) {
                    vratiNaPocetnuMasku(); // Povratak na osnovni meni
                    return; // Izlazak iz metode izvrsi
                }

                double beta;
                try {
                    beta = KoordinateMenadzer.konvertujDMSuDecimale(Double.parseDouble(betaInput.trim()));
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(null, "Unos nije validan broj. Pokušajte ponovo.", "Greška", JOptionPane.ERROR_MESSAGE);
                    continue; // Vraća na ponovni unos
                }

                // Unos d
                String dInput = JOptionPane.showInputDialog("Unesite dužinu (d):");
                if (dInput == null || dInput.trim().isEmpty()) {
                    vratiNaPocetnuMasku(); // Povratak na osnovni meni
                    return; // Izlazak iz metode izvrsi
                }

                double d;
                try {
                    d = Double.parseDouble(dInput.trim());
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(null, "Unos nije validan broj. Pokušajte ponovo.", "Greška", JOptionPane.ERROR_MESSAGE);
                    continue; // Vraća na ponovni unos
                }

                double M = beta - Pk; // Razlika između beta i Pk
                if (M < 0) M += 360;

                Mk = M + C; // Dodavanje C na M
                if (Mk > 360) Mk -= 360;

                double YNovo = Ya + d * Math.sin(Math.toRadians(Mk)); // Nove Y koordinate
                double XNovo = Xa + d * Math.cos(Math.toRadians(Mk)); // Nove X koordinate

                JOptionPane.showMessageDialog(null, "Nove koordinate:\nY = " + DECIMAL_FORMAT.format(YNovo) + "\nX = " + DECIMAL_FORMAT.format(XNovo));

                // Ponuda za čuvanje koordinata
                int unosOpcija = JOptionPane.showConfirmDialog(
                        null,
                        "Želite li da sačuvate koordinate u YXHkoordinate.csv?",
                        "Unos u CSV",
                        JOptionPane.YES_NO_OPTION
                );

                if (unosOpcija == JOptionPane.YES_OPTION) {
                    String nazivTacke = null;
                    while (true) {
                        nazivTacke = JOptionPane.showInputDialog("Unesite naziv tačke:");
                        if (nazivTacke == null || nazivTacke.trim().isEmpty()) {
                            JOptionPane.showMessageDialog(null, "Naziv tačke nije unet. Koordinate nisu sačuvane.");
                            break;
                        }

                        try {
                            if (koordinateMenadzer.postojeKoordinate(nazivTacke)) {
                                int odgovor = JOptionPane.showConfirmDialog(
                                        null,
                                        "Tačka sa tim nazivom već postoji. Želite li da je pregazite?",
                                        "Potvrda pregazivanja",
                                        JOptionPane.YES_NO_OPTION,
                                        JOptionPane.WARNING_MESSAGE
                                );

                                if (odgovor == JOptionPane.NO_OPTION) {
                                    continue;
                                }
                            }

                            koordinateMenadzer.dodajIliAzuriraj(nazivTacke, YNovo, XNovo, 0.0);
                            JOptionPane.showMessageDialog(null, "Koordinate uspešno sačuvane.");
                            break;
                        } catch (IOException e) {
                            JOptionPane.showMessageDialog(null, "Greška prilikom čuvanja podataka: " + e.getMessage(), "Greška", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                }

                // Nastavlja na unos beta
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Greška prilikom pristupa datoteci: " + e.getMessage(), "Greška", JOptionPane.ERROR_MESSAGE);
        }
    }

    private String unosNazivaTacke(String poruka) {
        while (true) {
            String naziv = JOptionPane.showInputDialog(null, poruka);
            if (naziv == null || naziv.trim().isEmpty()) {
                vratiNaPocetnuMasku(); // Povratak na osnovni meni
                return null; // Prekid daljeg izvršavanja
            }
            naziv = naziv.trim().toUpperCase();
            try {
                if (koordinateMenadzer.postojeKoordinate(naziv)) {
                    return naziv; // Validan unos
                } else {
                    JOptionPane.showMessageDialog(null, "Naziv tačke '" + naziv + "' ne postoji u datoteci. Pokušajte ponovo.", "Greška", JOptionPane.ERROR_MESSAGE);
                }
            } catch (IOException e) {
                JOptionPane.showMessageDialog(null, "Greška prilikom provere naziva tačke: " + e.getMessage(), "Greška", JOptionPane.ERROR_MESSAGE);
                vratiNaPocetnuMasku(); // Povratak na osnovni meni u slučaju greške
                return null;
            }
        }
    }

    private double unosKoordinate(String poruka) {
        while (true) {
            String input = JOptionPane.showInputDialog(null, poruka);
            if (input == null) {
                throw new RuntimeException("Unos prekinut.");
            }
            try {
                return Double.parseDouble(input);
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "Unos nije validan broj. Pokušajte ponovo.", "Greška", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    private void vratiNaPocetnuMasku() {
        Main.main(null); // Pokreće glavni meni aplikacije
    }
}
