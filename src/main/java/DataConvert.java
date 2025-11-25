import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.json.JSONObject;
import java.io.File;
import java.io.FileWriter;

public class DataConvert extends DataDownload{
    public static void convertiontool() {
        try {
            File pdfFile = new File("proipologismos.pdf");
             if (!pdfFile.exists()) {
                System.out.println("Δεν βρέθηκε το αρχείο proipologismos.pdf στον τρέχοντα φάκελο");
                return;
            }
            PDDocument document = PDDocument.load(pdfFile);

            PDFTextStripper pdfStripper = new PDFTextStripper();
            String text = pdfStripper.getText(document);
            document.close();

            JSONObject json = new JSONObject();
            json.put("text", text);

            FileWriter writer = new FileWriter("proipologismos.json");
            writer.write(json.toString(4));
            writer.close();
            
            
            System.out.println("Ο Προυπολογισμός του έτους " +DataDownload.yearof + " μετατράπηκε σε αρχείο JSON: proipologismos.json");
            

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
