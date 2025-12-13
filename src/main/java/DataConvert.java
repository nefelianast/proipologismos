import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class DataConvert {

    private static StringBuilder csvBuilder;

    public static void convertiontool(int yearof) {
        try {
            File pdfFile = new File("proipologismos" + yearof + ".pdf");
            if (!pdfFile.exists()) {
                System.out.println("Δεν βρέθηκε το αρχείο proipologismos" + yearof + ".pdf στον τρέχοντα φάκελο");
                return;
            }

            PDDocument document = PDDocument.load(pdfFile);
            PDFTextStripper pdfStripper = new PDFTextStripper();

            csvBuilder = new StringBuilder();
            csvBuilder.append("Σελίδα,Κείμενο,Αριθμοί\n");

            int totalPages = document.getNumberOfPages();

            for (int page = 1; page <= totalPages; page++) {
                pdfStripper.setStartPage(page);
                pdfStripper.setEndPage(page);

                String pageText = pdfStripper.getText(document);
                String[] lines = pageText.split("\\r?\\n");

                for (String line : lines) {
                    line = line.trim();
                    if (line.isEmpty()) continue;

                    StringBuilder numbers = new StringBuilder();
                    StringBuilder textOnly = new StringBuilder();

                    String[] tokens = line.split("\\s+");
                    for (String token : tokens) {
                        if (token.matches(".*\\d.*")) {
                            numbers.append(token).append(",");
                        } else {
                            if (textOnly.length() > 0) textOnly.append(" ");
                            textOnly.append(token);
                        }
                    }

                    if (numbers.length() > 0)
                        numbers.setLength(numbers.length() - 1);

                    csvBuilder.append(page).append(",")
                            .append("\"").append(textOnly).append("\"").append(",")
                            .append(numbers)
                            .append("\n");
                }
            }

            document.close();

            String csvFile = "proipologismos" + yearof + ".csv";
            FileWriter writer = new FileWriter(csvFile);
            writer.write(csvBuilder.toString());
            writer.close();

            // ============================
            // FIXES ΑΝΑ ΕΤΟΣ
            // ============================
            if (yearof == 2023) {
                fix2023Specific(csvFile);
            }

            if (yearof == 2024) {
                fix2024(csvFile);
                fix2024Titles(csvFile);
            }

            System.out.println("Ο προϋπολογισμός " + yearof + " μετατράπηκε σε CSV.");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // =========================================================
    // FIX 2023 – ΣΥΓΚΕΚΡΙΜΕΝΕΣ Αποκεντρωμένες Διοικήσεις
    // =========================================================
    private static void fix2023Specific(String csvFile) {
        try {
            List<String> lines = read(csvFile);
            List<String> fixed = new ArrayList<>();

            for (int i = 0; i < lines.size(); i++) {
                String line = lines.get(i).trim();

                if (line.startsWith("3,\"Αποκεντρωμένη Διοίκηση")) {
                    StringBuilder name = new StringBuilder();
                    String amounts = null;
                    String id = null;

                    // Προσθέτουμε πρώτο μέρος του ονόματος
                    name.append(line.substring(line.indexOf("\"") + 1, line.lastIndexOf("\"")));

                    // Έλεγχος επόμενης γραμμής για συνέχεια ονόματος
                    if (i + 1 < lines.size()) {
                        String next = lines.get(i + 1).trim();
                        if (next.startsWith("3,\"") && !next.startsWith("3,\"\",") && !next.matches(".*\\d.*")) {
                            name.append(" ")
                                .append(next.substring(next.indexOf("\"") + 1, next.lastIndexOf("\"")));
                            i++;
                        }
                    }

                    // Επόμενη γραμμή για ποσά
                    if (i + 1 < lines.size()) {
                        String next = lines.get(i + 1).trim();
                        if (next.contains(",")) {
                            String[] parts = next.split(",", 3);
                            if (parts.length >= 3) {
                                amounts = parts[2]; // τα ποσά
                            }
                            i++;
                        }
                    }

                    // Επόμενη γραμμή για ID
                    if (i + 1 < lines.size()) {
                        String next = lines.get(i + 1).trim();
                        if (next.startsWith("3,\"\",") && next.split(",").length >= 3) {
                            id = next.split(",", 3)[2];
                            i++;
                        }
                    }

                    // Προσθήκη της σωστής γραμμής
                    if (id != null && amounts != null) {
                        fixed.add("3,\"" + name + "\"," + id + "," + amounts);
                        continue;
                    }
                }

                // Όλες οι υπόλοιπες γραμμές μένουν ίδιες
                fixed.add(line);
            }

            write(csvFile, fixed);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // =========================================================
    // FIX 2024 – όπως το είχες
    // =========================================================
    private static void fix2024(String csvFile) {
        try {
            List<String> lines = read(csvFile);
            List<String> fixed = new ArrayList<>();

            for (int i = 0; i < lines.size(); i++) {
                String line = lines.get(i);

                if (line.matches("^3,\"\",.*") && !fixed.isEmpty()) {
                    String prev = fixed.remove(fixed.size() - 1);

                    String id = line.split(",", 3)[2];
                    String[] p = prev.split(",", 3);
                    String[] a = p[2].split(",", 2);

                    fixed.add(p[0] + "," + p[1] + "," + id + "," + a[1]);
                    continue;
                }

                fixed.add(line);
            }

            write(csvFile, fixed);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // =========================================================
    // FIX 2024 – Χρεωστικοί / Συμμετοχικοί τίτλοι
    // =========================================================
    private static void fix2024Titles(String csvFile) {
        try {
            List<String> lines = read(csvFile);
            List<String> fixed = new ArrayList<>();

            for (int i = 0; i < lines.size(); i++) {
                String line = lines.get(i);

                if (line.matches("^2,\".*\",\\d+\\.?$") && i + 1 < lines.size()
                        && lines.get(i + 1).matches("^2,\"\",\\d+\\.?$")) {

                    String[] a = line.split(",", 3);
                    String id = lines.get(i + 1).split(",", 3)[2];
                    fixed.add(a[0] + "," + a[1] + "," + id);
                    i++;
                    continue;
                }

                fixed.add(line);
            }

            write(csvFile, fixed);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // =========================================================
    // HELPERS
    // =========================================================
    private static List<String> read(String f) throws Exception {
        List<String> l = new ArrayList<>();
        BufferedReader r = new BufferedReader(new FileReader(f));
        String s;
        while ((s = r.readLine()) != null) l.add(s);
        r.close();
        return l;
    }

    private static void write(String f, List<String> l) throws Exception {
        BufferedWriter w = new BufferedWriter(new FileWriter(f));
        for (String s : l) {
            w.write(s);
            w.newLine();
        }
        w.close();
    }
}
