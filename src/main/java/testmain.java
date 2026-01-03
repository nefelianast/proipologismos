import java.io.IOException;
import java.util.Scanner;
//Μ
import ui.BudgetAnalysisUI;

public class testmain{
    public static void main(String args[]) throws IOException{
        //μ
        LoginApp.authenticate();
        //μ
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
        //Μ
        //
        BudgetAnalysisUI.start();
        //ΔΟΚΙΜΗ
       /*System.out.println("Σύγκριση (1) - Ανάλυση (2)");
        int action = scan.nextInt();
        int year1, year2;
        if (action == 1){
            
            System.out.println("First Year:");
            year1 = scan.nextInt();
            while ((year1 < 2023) || (year1 > 2026)){
                System.out.println("Second Year:");
                year1 = scan.nextInt();   
            }
            System.out.println("Second Year:");
            year2 = scan.nextInt();
            while ((year2 < 2023) || (year2 > 2026) || (year2 == year1)){
                System.out.println("Second Year:");
                year2 = scan.nextInt();   
            }
            com.comparisons_of_two_years(year1,year2);
        }
        else {
            System.out.println("Year:");
            year1 = scan.nextInt();
            while ((year1 < 2023) || (year1 > 2026)){
                System.out.println("Year:");
                year1 = scan.nextInt();   
            }
            year2 = 0;
            com.comparisons_of_two_years(year1,year2);
         } */
        } 


    }
}
