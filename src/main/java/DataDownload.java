import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * Class responsible for downloading budget PDF files from the Ministry of Finance website.
 * Downloads budget documents for years 2023, 2024, 2025, and 2026.
 * After downloading, automatically converts the PDF to CSV format using DataConvert.
 */
public class DataDownload 
{
    /**
     * Static field to store the year of the downloaded budget document
     */
    public static int yearof;

    /**
     * Downloads budget PDF files for all available years (2023-2026) from the Ministry of Finance website.
     * Each PDF is saved locally and then automatically converted to CSV format.
     * 
     * @throws IOException if there is an error during the download or file operations
     */
	public void Download() throws IOException
	{
		//Scanner scan = new Scanner(System.in);
		//System.out.println("ΠΟΙΑΣ ΧΡΟΝΙΑΣ ΠΡΟΥΠΟΛΟΓΙΣΜΟ ΘΕΣ ΝΑ ΔΕΙΣ 1:2023, 2:2024, 3:2025");
		//int epilogi = scan.nextInt();
		//do {
		//	if (epilogi !=1 && epilogi !=2 && epilogi!=3) {
		//		System.out.println("έγκυρες απαντήσεις ειναι μονο οι αριθμοι 1,2,3 παρακαλώ ξανά πληκτρολογήστε");
		//		System.out.println("ΠΟΙΑΣ ΧΡΟΝΙΑΣ ΠΡΟΥΠΟΛΟΓΙΣΜΟ ΘΕΣ ΝΑ ΔΕΙΣ 1:2023, 2:2024, 3:2025");
		//		epilogi = scan.nextInt();
		//	}
		
		//}while(epilogi !=1 && epilogi !=2 && epilogi!=3);
		


		for (int epilogi = 1; epilogi <=4; epilogi++ ) {



		String link2026 = "https://minfin.gov.gr/wp-content/uploads/2025/11/Κρατικός-Προϋπολογισμός-2026.pdf";
		String link2025 = "https://minfin.gov.gr/wp-content/uploads/2024/11/Κρατικός-Προϋπολογισμός-2025_ΟΕ.pdf";
		String link2024 = "https://minfin.gov.gr/wp-content/uploads/2023/11/ΚΡΑΤΙΚΟΣ-ΠΡΟΥΠΟΛΟΓΙΣΜΟΣ-2024.pdf";
		String link2023 = "https://minfin.gov.gr/wp-content/uploads/2023/11/21-11-2022-ΚΡΑΤΙΚΟΣ-ΠΡΟΫΠΟΛΟΓΙΣΜΟΣ-2023.pdf";
		
		String year;

		if (epilogi == 1){
			year= link2023;
		} else if (epilogi == 2){
			year= link2024;
		} else if(epilogi == 3){
			year= link2025;
		} else{
			year = link2026;
		}
		if (epilogi == 1){
			yearof= 2023;
		} else if (epilogi == 2){
			yearof= 2024;
		} else if (epilogi == 3){
			yearof= 2025;
		} else {
			yearof = 2026;
		}

		
		
		
	

		



		try
			{					
					
					
				String url = year;
				URLConnection connection = new URL(url).openConnection();
				connection.setConnectTimeout(60000);
				connection.setReadTimeout(60000);
					
       
				InputStream input = connection.getInputStream();
		
		
				File destination = new File("proipologismos"+yearof+".pdf");
				FileOutputStream output = new FileOutputStream(destination, false);
					
				byte[] buffer = new byte[2048];
				int read;
				
				System.out.println("downloading...");
				while((read = input.read(buffer)) > -1)
				{
					output.write(buffer, 0, read);
					}
					if (epilogi==3) {
					System.out.println(
					" _               _            _     _____  _____  _____  _____ \n" +
					"| |             | |          | |   / __  \\|  _  |/ __  \\|  ___|\n" +
                   "| |__  _   _  __| | __ _  ___| |_  `' / /'| |/' |`' / /'|___ \\ \n" +
                   "| '_ \\| | | |/ _` |/ _` |/ _ \\ __|   / /  |  /| |  / /      \\ \\\n" +
                   "| |_) | |_| | (_| | (_| |  __/ |_  ./ /___\\ |_/ /./ /___/\\__/ /\n" +
                   "|_.__/ \\__,_|\\__,_|\\__, |\\___|\\__| \\_____/ \\___/ \\_____/\\____/ \n" +
                   "                    __/ |                                      \n" +
                   "                   |___/                                      ");}
				   else if (epilogi==2) {
					System.out.println(
					" _               _            _     _____  _____  _____   ___ \n" +
					"| |             | |          | |   / __  \\|  _  |/ __  \\ /   |\n" +
					"| |__  _   _  __| | __ _  ___| |_  `' / /'| |/' |`' / /'/ /| |\n" +
					"| '_ \\| | | |/ _` |/ _` |/ _ \\ __|   / /  |  /| |  / / / /_| |\n" +
					"| |_) | |_| | (_| | (_| |  __/ |_  ./ /___\\ |_/ /./ /__\\___  |\n" +
					"|_.__/ \\__,_|\\__,_|\\__, |\\___|\\__| \\_____/ \\___/ \\_____/   |_/\n" +
					"                    __/ |                                      \n" +
					"                   |___/                                       \n" +
					
					"                                                               ");}
					else if (epilogi==1) {
						System.out.println(
					" _               _            _     _____  _____  _____  _____ \n" +
					"| |             | |          | |   / __  \\|  _  |/ __  \\|____ |\n" +
					"| |__  _   _  __| | __ _  ___| |_  `' / /'| |/' |`' / /'    | |\n" +
					"| '_ \\| | | |/ _` |/ _` |/ _ \\ __|   / /  |  /| |  / /      \\ \\\n" +
					"| |_) | |_| | (_| | (_| |  __/ |_  ./ /___\\ |_/ /./ /___.___/ /\n" +
					"|_.__/ \\__,_|\\__,_|\\__, |\\___|\\__| \\_____/ \\___/ \\_____\\/\\____/ \n" +
					"                    __/ |                                      \n" +
					"                   |___/                                       \n" +

					"                                                               "
					);
						


				
				   } else if (epilogi ==4) {
					System.out.println(
        " _               _            _     _____  _____  _____   ____ \n" +
        "| |             | |          | |   / __  \\|  _  |/ __  \\ / ___|\n" +
        "| |__  _   _  __| | __ _  ___| |_  `' / /'| |/' |`' / /'/ /___ \n" +
        "| '_ \\| | | |/ _` |/ _` |/ _ \\ __|   / /  |  /| |  / /  | ___ \\\n" +
        "| |_) | |_| | (_| | (_| |  __/ |_  ./ /___\\ |_/ /./ /___| \\_/ |\n" +
        "|_.__/ \\__,_|\\__,_|\\__, |\\___|\\__| \\_____/ \\___/ \\_____/\\_____/\n" +
        "                    __/ |                                      \n" +
        "                   |___/                                       \n"
);

			
				   }

					
				output.flush();
				output.close();
				input.close();
				}
				
			catch (Exception e) 
			{
				e.printStackTrace();
			}

			 DataConvert converter = new DataConvert();
			 converter.convertiontool(yearof);
			 
			 
		}
		
    
	
	}

  
}

