import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.File;
import java.io.FileWriter;

public class DataConvert {

    public static void convertiontool() {
        try {
            File pdfFile = new File("proipologismos.pdf");
            if (!pdfFile.exists()) {
                System.out.println("Δεν βρέθηκε το αρχείο proipologismos.pdf στον τρέχοντα φάκελο");
                return;
            }

            PDDocument document = PDDocument.load(pdfFile);
            PDFTextStripper pdfStripper = new PDFTextStripper();

            StringBuilder csvBuilder = new StringBuilder();
            csvBuilder.append("Σελίδα,Κείμενο,Αριθμοί\n"); // header CSV

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

                    // Κρατάμε αριθμούς ξεχωριστά
                    String[] tokens = line.split("\\s+");
                    for (String token : tokens) {
                        if (token.matches(".*\\d.*")) {
                            numbers.append(token).append(",");
                        } else {
                            if (textOnly.length() > 0) textOnly.append(" ");
                            textOnly.append(token);
                        }
                    }

                    // Αφαίρεση τελευταίου κόμματος
                    if (numbers.length() > 0) numbers.setLength(numbers.length() - 1);

                    csvBuilder.append(page).append(",")
                              .append("\"").append(textOnly).append("\"").append(",")
                              .append(numbers)
                              .append("\n");
                }
            }

            document.close();

            FileWriter csvWriter = new FileWriter("proipologismos_full_formatted.csv");
            csvWriter.write(csvBuilder.toString());
            csvWriter.close();

            System.out.println("Ολόκληρος ο προϋπολογισμός μετατράπηκε σε CSV με μορφοποιημένα κείμενα σε ένα κελί: proipologismos_full_formatted.csv");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}


