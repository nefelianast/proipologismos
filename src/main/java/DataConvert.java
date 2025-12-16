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
                fix2023_KlimatikiKrisi(csvFile);
                fix2023_ApokentromenesDioikiseis(csvFile);
                fix2023_CreteOnly(csvFile);
            }

            if (yearof == 2024) {
                fix2024(csvFile);
                fix2024Titles(csvFile);
                fix2024_SymmetochikoiTitloi(csvFile);
                fix2024_KlimatikiKrisi(csvFile);
                fix2024_ApokentromenesDioikiseis(csvFile);
            }
            
            if (yearof == 2025) {
                fix2025_SymmetochikoiTitloi(csvFile);
                fix2025_ApokentromenesDioikiseis(csvFile);
            }
            
            if (yearof == 2026) {
                fix2024Titles(csvFile);
                fix2026_SymmetochikoiTitloi(csvFile);
                fix2026_ApokentromenesDioikiseis(csvFile);
            }

            System.out.println("Ο προϋπολογισμός " + yearof + " μετατράπηκε σε CSV.");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

  

    // =========================================================
    // FIX 2023 – ΑΠΟΚΕΝΤΡΩΜΕΝΕΣ ΔΙΟΙΚΗΣΕΙΣ
    // =========================================================
    private static void fix2023_ApokentromenesDioikiseis(String csvFile) {
        try {
            List<String> lines = read(csvFile);
            List<String> fixed = new ArrayList<>();
            
            int i = 0;
            while (i < lines.size()) {
                String line = lines.get(i);
                
                if (i == 0) {
                    fixed.add(line);
                    i++;
                    continue;
                }
                
                if (line.startsWith("3,\"Αποκεντρωμένη Διοίκηση Πελοποννήσου -")) {
                    fixed.add("3,\"Αποκεντρωμένη Διοίκηση Πελοποννήσου - Δυτικής Ελλάδας και Ιονίου\",1904,12.467.000,0,12.467.000");
                    i++;
                    continue;
                }
                
                fixed.add(line);
                i++;
            }
            
            write(csvFile, fixed);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // =========================================================
    // FIX 2023 – ΥΠΟΥΡΓΕΙΟ ΚΛΙΜΑΤΙΚΗΣ ΚΡΙΣΗΣ
    // =========================================================
    private static void fix2023_KlimatikiKrisi(String csvFile) {
        try {
            List<String> lines = read(csvFile);
            List<String> fixed = new ArrayList<>();
            
            int i = 0;
            while (i < lines.size()) {
                String line = lines.get(i);
                
                if (i == 0) {
                    fixed.add(line);
                    i++;
                    continue;
                }
                
                if (i + 1 < lines.size() && 
                    line.startsWith("3,\"Υπουργείο Κλιματικής Κρίσης και Πολιτικής\",1059") &&
                    lines.get(i+1).startsWith("3,\"Προστασίας\"")) {
                    
                    String[] parts = lines.get(i+1).split(",", 5);
                    if (parts.length >= 5) {
                        fixed.add("3,\"Υπουργείο Κλιματικής Κρίσης και Πολιτικής Προστασίας\",1059," + 
                                 parts[2] + "," + parts[3] + "," + parts[4]);
                        i += 2;
                        continue;
                    }
                }
                
                fixed.add(line);
                i++;
            }
            
            write(csvFile, fixed);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // =========================================================
    // FIX 2024 – ΣΥΜΜΕΤΟΧΙΚΟΙ ΤΙΤΛΟΙ
    // =========================================================
    private static void fix2024_SymmetochikoiTitloi(String csvFile) {
        try {
            List<String> lines = read(csvFile);
            List<String> fixed = new ArrayList<>();
            
            int i = 0;
            while (i < lines.size()) {
                String line = lines.get(i);
                
                if (i == 0) {
                    fixed.add(line);
                    i++;
                    continue;
                }
                
                if (i + 4 < lines.size() && 
                    line.startsWith("2,\"\",45.") && 
                    lines.get(i+1).startsWith("2,\"Συμμετοχικοί τίτλοι και μερίδια επενδυτικών\"") &&
                    lines.get(i+2).startsWith("2,\"κεφαλαίων\"") &&
                    lines.get(i+3).startsWith("2,\"»\"") &&
                    lines.get(i+4).startsWith("2,\"\",1.095.000.000")) {
                    
                    fixed.add("2,\"Συμμετοχικοί τίτλοι και μερίδια επενδυτικών κεφαλαίων»,45,1.095.000.000");
                    i += 5;
                    continue;
                }
                
                if (i + 4 < lines.size() && 
                    line.startsWith("2,\"\",45.") && 
                    lines.get(i+1).startsWith("2,\"Συμμετοχικοί τίτλοι και μερίδια επενδυτικών\"") &&
                    lines.get(i+2).startsWith("2,\"κεφαλαίων\"") &&
                    lines.get(i+3).startsWith("2,\"»\"") &&
                    lines.get(i+4).startsWith("2,\"\",1.557.768.000")) {
                    
                    fixed.add("2,\"Συμμετοχικοί τίτλοι και μερίδια επενδυτικών κεφαλαίων»,45,1.557.768.000");
                    i += 5;
                    continue;
                }
                
                fixed.add(line);
                i++;
            }
            
            write(csvFile, fixed);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // =========================================================
    // FIX 2024 – ΚΛΙΜΑΤΙΚΗ ΚΡΙΣΗ
    // =========================================================
    private static void fix2024_KlimatikiKrisi(String csvFile) {
        try {
            List<String> lines = read(csvFile);
            List<String> fixed = new ArrayList<>();
            
            int i = 0;
            while (i < lines.size()) {
                String line = lines.get(i);
                
                if (i == 0) {
                    fixed.add(line);
                    i++;
                    continue;
                }
                
                if (i + 1 < lines.size() && 
                    line.startsWith("3,\"Υπουργείο Κλιματικής Κρίσης και Πολιτικής\"") &&
                    lines.get(i+1).startsWith("3,\"Προστασίας\"")) {
                    
                    String[] parts = lines.get(i+1).split(",", 4);
                    if (parts.length >= 4) {
                        fixed.add("3,\"Υπουργείο Κλιματικής Κρίσης και Πολιτικής Προστασίας\"," + parts[2] + "," + parts[3]);
                        i += 2;
                        continue;
                    }
                }
                
                fixed.add(line);
                i++;
            }
            
            write(csvFile, fixed);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // =========================================================
    // FIX 2024 – ΑΠΟΚΕΝΤΡΩΜΕΝΕΣ ΔΙΟΙΚΗΣΕΙΣ
    // =========================================================
    private static void fix2024_ApokentromenesDioikiseis(String csvFile) {
        try {
            List<String> lines = read(csvFile);
            List<String> fixed = new ArrayList<>();
            
            int i = 0;
            while (i < lines.size()) {
                String line = lines.get(i);
                
                if (i == 0) {
                    fixed.add(line);
                    i++;
                    continue;
                }
                
                if (i + 1 < lines.size() && 
                    line.startsWith("3,\"Αποκεντρωμένη Διοίκηση Θεσσαλίας - Στερεάς\"") &&
                    lines.get(i+1).startsWith("3,\"Ελλάδας\",1903")) {
                    
                    String[] parts = lines.get(i+1).split(",", 5);
                    if (parts.length >= 5) {
                        fixed.add("3,\"Αποκεντρωμένη Διοίκηση Θεσσαλίας - Στερεάς Ελλάδας\"," + 
                                 parts[2] + "," + parts[3] + "," + parts[4]);
                        i += 2;
                        continue;
                    }
                }
                
                if (i + 1 < lines.size() && 
                    line.startsWith("3,\"Αποκεντρωμένη Διοίκηση Ηπείρου - Δυτικής\"") &&
                    lines.get(i+1).startsWith("3,\"Μακεδονίας\",1904")) {
                    
                    String[] parts = lines.get(i+1).split(",", 5);
                    if (parts.length >= 5) {
                        fixed.add("3,\"Αποκεντρωμένη Διοίκηση Ηπείρου - Δυτικής Μακεδονίας\"," + 
                                 parts[2] + "," + parts[3] + "," + parts[4]);
                        i += 2;
                        continue;
                    }
                }
                
                if (i + 1 < lines.size() && 
                    line.startsWith("3,\"Αποκεντρωμένη Διοίκηση Πελοποννήσου - Δυτικής\"") &&
                    lines.get(i+1).startsWith("3,\"Ελλάδας και Ιονίου\"")) {
                    
                    String[] parts = lines.get(i+1).split(",", 5);
                    if (parts.length >= 5) {
                        fixed.add("3,\"Αποκεντρωμένη Διοίκηση Πελοποννήσου - Δυτικής Ελλάδας και Ιονίου\"," + 
                                 parts[2] + "," + parts[3] + "," + parts[4]);
                        i += 2;
                        continue;
                    }
                }
                
                fixed.add(line);
                i++;
            }
            
            write(csvFile, fixed);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // =========================================================
    // FIX 2025 – ΣΥΜΜΕΤΟΧΙΚΟΙ ΤΙΤΛΟΙ
    // =========================================================
    private static void fix2025_SymmetochikoiTitloi(String csvFile) {
        try {
            List<String> lines = read(csvFile);
            List<String> fixed = new ArrayList<>();
            
            int i = 0;
            while (i < lines.size()) {
                String line = lines.get(i);
                
                if (i == 0) {
                    fixed.add(line);
                    i++;
                    continue;
                }
                
                if (i + 3 < lines.size() && 
                    line.startsWith("2,\"\",45.") && 
                    lines.get(i+1).startsWith("2,\"Συμμετοχικοί τίτλοι και μερίδια επενδυτικών\"") &&
                    lines.get(i+2).startsWith("2,\"κεφαλαίων\"") &&
                    lines.get(i+3).startsWith("2,\"»\",467.000.000")) {
                    
                    fixed.add("2,\"Συμμετοχικοί τίτλοι και μερίδια επενδυτικών κεφαλαίων»,45,467.000.000");
                    i += 4;
                    continue;
                }
                
                if (i + 3 < lines.size() && 
                    line.startsWith("2,\"\",45.") && 
                    lines.get(i+1).startsWith("2,\"Συμμετοχικοί τίτλοι και μερίδια επενδυτικών\"") &&
                    lines.get(i+2).startsWith("2,\"κεφαλαίων\"") &&
                    lines.get(i+3).startsWith("2,\"»\",1.755.112.000")) {
                    
                    fixed.add("2,\"Συμμετοχικοί τίτλοι και μερίδια επενδυτικών κεφαλαίων»,45,1.755.112.000");
                    i += 4;
                    continue;
                }
                
                fixed.add(line);
                i++;
            }
            
            write(csvFile, fixed);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // =========================================================
    // FIX 2025 – ΑΠΟΚΕΝΤΡΩΜΕΝΕΣ ΔΙΟΙΚΗΣΕΙΣ
    // =========================================================
    private static void fix2025_ApokentromenesDioikiseis(String csvFile) {
        try {
            List<String> lines = read(csvFile);
            List<String> fixed = new ArrayList<>();
            
            int i = 0;
            while (i < lines.size()) {
                String line = lines.get(i);
                
                if (i == 0) {
                    fixed.add(line);
                    i++;
                    continue;
                }
                
                if (i + 3 < lines.size() && 
                    line.startsWith("3,\"\",1903") &&
                    lines.get(i+1).startsWith("3,\"Αποκεντρωμένη Διοίκηση Ηπείρου - Δυτικής\"") &&
                    lines.get(i+2).startsWith("3,\"Μακεδονίας\"") &&
                    lines.get(i+3).startsWith("3,\"\",9.943.000,0,9.943.000")) {
                    
                    fixed.add("3,\"Αποκεντρωμένη Διοίκηση Ηπείρου - Δυτικής Μακεδονίας\",1903,9.943.000,0,9.943.000");
                    i += 4;
                    continue;
                }
                
                if (i + 3 < lines.size() && 
                    line.startsWith("3,\"\",1904") &&
                    lines.get(i+1).startsWith("3,\"Αποκεντρωμένη Διοίκηση Πελοποννήσου - Δυτικής\"") &&
                    lines.get(i+2).startsWith("3,\"Ελλάδας και Ιονίου\"") &&
                    lines.get(i+3).startsWith("3,\"\",14.918.000,0,14.918.000")) {
                    
                    fixed.add("3,\"Αποκεντρωμένη Διοίκηση Πελοποννήσου - Δυτικής Ελλάδας και Ιονίου\",1904,14.918.000,0,14.918.000");
                    i += 4;
                    continue;
                }
                
                fixed.add(line);
                i++;
            }
            
            write(csvFile, fixed);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // =========================================================
    // FIX 2026 – ΣΥΜΜΕΤΟΧΙΚΟΙ ΤΙΤΛΟΙ
    // =========================================================
    private static void fix2026_SymmetochikoiTitloi(String csvFile) {
        try {
            List<String> lines = read(csvFile);
            List<String> fixed = new ArrayList<>();
            
            int i = 0;
            while (i < lines.size()) {
                String line = lines.get(i);
                
                if (i == 0) {
                    fixed.add(line);
                    i++;
                    continue;
                }
                
                if (i + 3 < lines.size() && 
                    line.startsWith("2,\"\",45.") && 
                    lines.get(i+1).startsWith("2,\"Συμμετοχικοί τίτλοι και μερίδια επενδυτικών\"") &&
                    lines.get(i+2).startsWith("2,\"κεφαλαίων\"") &&
                    lines.get(i+3).startsWith("2,\"»\",228.000.000")) {
                    
                    fixed.add("2,\"Συμμετοχικοί τίτλοι και μερίδια επενδυτικών κεφαλαίων»,45,228.000.000");
                    i += 4;
                    continue;
                }
                
                if (i + 3 < lines.size() && 
                    line.startsWith("2,\"\",45.") && 
                    lines.get(i+1).startsWith("2,\"Συμμετοχικοί τίτλοι και μερίδια επενδυτικών\"") &&
                    lines.get(i+2).startsWith("2,\"κεφαλαίων\"") &&
                    lines.get(i+3).startsWith("2,\"»\",1.587.084.000")) {
                    
                    fixed.add("2,\"Συμμετοχικοί τίτλοι και μερίδια επενδυτικών κεφαλαίων»,45,1.587.084.000");
                    i += 4;
                    continue;
                }
                
                fixed.add(line);
                i++;
            }
            
            write(csvFile, fixed);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    // =========================================================
    // FIX 2023 – ΚΡΗΤΗ (ΒΕΛΤΙΩΜΕΝΗ)
    // =========================================================
    private static void fix2023_CreteOnly(String csvFile) {
        try {
            List<String> lines = read(csvFile);
            List<String> fixed = new ArrayList<>();
            
            boolean hasCrete = false;
            
            for (String line : lines) {
                // ΕΛΕΓΧΟΣ 1: Αν είναι η Κρήτη με λάθος αριθμούς, τη διορθώνουμε
                if (line.contains("Αποκεντρωμένη Διοίκηση Κρήτης")) {
                    hasCrete = true;
                    // Αντικαθιστούμε ΠΑΝΤΑ με τους σωστούς αριθμούς
                    fixed.add("3,\"Αποκεντρωμένη Διοίκηση Κρήτης\",1906,6.068.000,0,6.068.000");
                    continue;
                }
                
                fixed.add(line);
            }
            
            // ΕΛΕΓΧΟΣ 2: Αν η Κρήτη λείπει τελείως, την προσθέτουμε
            if (!hasCrete) {
                List<String> finalFixed = new ArrayList<>();
                boolean addedCrete = false;
                
                for (String line : fixed) {
                    finalFixed.add(line);
                    
                    // Προσθέτουμε την Κρήτη αμέσως μετά το Αιγαίο (1905)
                    if (!addedCrete && (line.contains("Αιγαίου") || line.contains("1905"))) {
                        finalFixed.add("3,\"Αποκεντρωμένη Διοίκηση Κρήτης\",1906,6.068.000,0,6.068.000");
                        addedCrete = true;
                    }
                }
                
                // Αν δεν βρήκαμε το Αιγαίο, προσθέτουμε στο τέλος
                if (!addedCrete) {
                    finalFixed.add("3,\"Αποκεντρωμένη Διοίκηση Κρήτης\",1906,6.068.000,0,6.068.000");
                }
                
                write(csvFile, finalFixed);
                return;
            }
            
            write(csvFile, fixed);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    // =========================================================
    // FIX 2026 – ΑΠΟΚΕΝΤΡΩΜΕΝΕΣ ΔΙΟΙΚΗΣΕΙΣ
    // =========================================================
    private static void fix2026_ApokentromenesDioikiseis(String csvFile) {
        try {
            List<String> lines = read(csvFile);
            List<String> fixed = new ArrayList<>();
            
            int i = 0;
            while (i < lines.size()) {
                String line = lines.get(i);
                
                if (i == 0) {
                    fixed.add(line);
                    i++;
                    continue;
                }
                
                if (i + 3 < lines.size() && 
                    line.startsWith("3,\"\",1903") &&
                    lines.get(i+1).startsWith("3,\"Αποκεντρωμένη Διοίκηση Ηπείρου - Δυτικής\"") &&
                    lines.get(i+2).startsWith("3,\"Μακεδονίας\"") &&
                    lines.get(i+3).startsWith("3,\"\",10.981.000,0,10.981.000")) {
                    
                    fixed.add("3,\"Αποκεντρωμένη Διοίκηση Ηπείρου - Δυτικής Μακεδονίας\",1903,10.981.000,0,10.981.000");
                    i += 4;
                    continue;
                }
                
                if (i + 3 < lines.size() && 
                    line.startsWith("3,\"\",1904") &&
                    lines.get(i+1).startsWith("3,\"Αποκεντρωμένη Διοίκηση Πελοποννήσου - Δυτικής\"") &&
                    lines.get(i+2).startsWith("3,\"Ελλάδας και Ιονίου\"") &&
                    lines.get(i+3).startsWith("3,\"\",15.556.000,0,15.556.000")) {
                    
                    fixed.add("3,\"Αποκεντρωμένη Διοίκηση Πελοποννήσου - Δυτικής Ελλάδας και Ιονίου\",1904,15.556.000,0,15.556.000");
                    i += 4;
                    continue;
                }
                
                fixed.add(line);
                i++;
            }
            
            write(csvFile, fixed);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // =========================================================
    // ΥΠΑΡΧΟΥΣΕΣ ΜΕΘΟΔΟΙ
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

                    name.append(line.substring(line.indexOf("\"") + 1, line.lastIndexOf("\"")));

                    if (i + 1 < lines.size()) {
                        String next = lines.get(i + 1).trim();
                        if (next.startsWith("3,\"") && !next.startsWith("3,\"\",") && !next.matches(".*\\d.*")) {
                            name.append(" ")
                                .append(next.substring(next.indexOf("\"") + 1, next.lastIndexOf("\"")));
                            i++;
                        }
                    }

                    if (i + 1 < lines.size()) {
                        String next = lines.get(i + 1).trim();
                        if (next.contains(",")) {
                            String[] parts = next.split(",", 3);
                            if (parts.length >= 3) {
                                amounts = parts[2];
                            }
                            i++;
                        }
                    }

                    if (i + 1 < lines.size()) {
                        String next = lines.get(i + 1).trim();
                        if (next.startsWith("3,\"\",") && next.split(",").length >= 3) {
                            id = next.split(",", 3)[2];
                            i++;
                        }
                    }

                    if (id != null && amounts != null) {
                        fixed.add("3,\"" + name + "\"," + id + "," + amounts);
                        continue;
                    }
                }

                fixed.add(line);
            }

            write(csvFile, fixed);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

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