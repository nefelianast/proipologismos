import java.io.IOException;
import java.util.Scanner;
public class testmain{
    public static void main(String args[]) throws IOException{
        System.out.println("αυτή η κλάση είναι test κλάση που θαχρησιμοποιει τις κλάσεις DataDownload, DataConvert, DataStorer, SQLmaker");
        System.out.println("1:download 2:SQL 3:Comnparisons");
        Scanner scan = new Scanner(System.in);
        int Y = scan.nextInt();
        if (Y==1) {
        DataDownload downloader = new DataDownload();
        downloader.Download();
        } else if (Y==2){
        SQLmaker sql = new SQLmaker();
        sql.make();
        } else if (Y==3){
        Comparisons com = new Comparisons();
        com.comparisons_of_two_years(2025,2026);
        } 


    }
}