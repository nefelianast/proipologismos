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

                    if (numbers.length() > 0) numbers.setLength(numbers.length() - 1);

                    csvBuilder.append(page).append(",")
                              .append("\"").append(textOnly).append("\"").append(",")
                              .append(numbers)
                              .append("\n");
                }
            }

            document.close();

            String csvFile = "proipologismos" + yearof + ".csv";
            FileWriter csvWriter = new FileWriter(csvFile);
            csvWriter.write(csvBuilder.toString());
            csvWriter.close();

            // -------------------------
            // Διόρθωση μόνο για τον προϋπολογισμό 2024
            // -------------------------
            if (yearof == 2024) {
                fix2024SpecificLines(csvFile);
            }

            System.out.println("Ο προϋπολογισμός του έτους " + yearof + " μετατράπηκε σε CSV.");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // -------------------------
    // Μέθοδος για διόρθωση γραμμών μόνο για το 2024
    // -------------------------
    private static void fix2024SpecificLines(String csvFile) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(csvFile));
            List<String> lines = new ArrayList<>();
            String line;

            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
            reader.close();

            List<String> fixedLines = new ArrayList<>();
            for (int i = 0; i < lines.size(); i++) {
                line = lines.get(i);

                // -----------------------------
                // Γραμμές με κενό όνομα ("")
                // -----------------------------
                if (line.matches("^3,\"\",.*") && !fixedLines.isEmpty()) {
                    String prevLine = fixedLines.remove(fixedLines.size() - 1);

                    String[] currParts = line.split(",", 3); // ["3", "\"\"", "ID,ή ποσά"]
                    String id = currParts[2]; // Παίρνουμε το ID

                    // Χωρίζουμε την προηγούμενη γραμμή σε μέρη
                    String[] prevParts = prevLine.split(",", 3); // ["3", "\"Υπουργείο...\"", "παλιά ποσά"]
                    String prevAmounts = prevParts[2];
                    String[] prevAmountsSplit = prevAmounts.split(",", 2); // [παλιό ID, υπόλοιπα ποσά]
                    String amounts = prevAmountsSplit.length > 1 ? prevAmountsSplit[1] : "";

                    // Δημιουργούμε νέα γραμμή: σελίδα, όνομα, νέο ID, ποσά
                    String mergedLine = prevParts[0] + "," + prevParts[1] + "," + id;
                    if (!amounts.isEmpty()) {
                        mergedLine += "," + amounts;
                    }

                    fixedLines.add(mergedLine);
                    continue;
                }

                // -----------------------------
                // Σπασμένο όνομα Υπουργείου Κλιματικής Κρίσης
                // -----------------------------
                if (line.contains("\"Υπουργείο Κλιματικής Κρίσης και Πολιτικής\"") && i + 1 < lines.size()) {
                    String nextLine = lines.get(i + 1);
                    if (nextLine.contains("\"Προστασίας\"")) {
                        String mergedLine = "3,\"Υπουργείο Κλιματικής Κρίσης και Πολιτικής Προστασίας\",1059,575.351.000,68.122.000,643.473.000";
                        fixedLines.add(mergedLine);
                        i++; // Παραλείπουμε την επόμενη γραμμή
                        continue;
                    }
                }

                fixedLines.add(line);
            }

            BufferedWriter writer = new BufferedWriter(new FileWriter(csvFile));
            for (String fixedLine : fixedLines) {
                writer.write(fixedLine);
                writer.newLine();
            }
            writer.close();

            System.out.println("Οι γραμμές με κενά ονόματα για τον προϋπολογισμό 2024 διορθώθηκαν και τα IDs τοποθετήθηκαν σωστά!");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
