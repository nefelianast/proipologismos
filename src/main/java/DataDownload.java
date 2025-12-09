import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
//import java.util.Scanner;
//η κλάση main ειναι γραμμενη προαιρετικά μην μπερδεύεστε
public class DataDownload 
{
    public static int yearof;


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
		


		for (int epilogi = 1; epilogi <=3; epilogi++ ) {




		String link2025 = "https://minfin.gov.gr/wp-content/uploads/2024/11/Κρατικός-Προϋπολογισμός-2025_ΟΕ.pdf";
		String link2024 = "https://minfin.gov.gr/wp-content/uploads/2023/11/ΚΡΑΤΙΚΟΣ-ΠΡΟΥΠΟΛΟΓΙΣΜΟΣ-2024.pdf";
		String link2023 = "https://minfin.gov.gr/wp-content/uploads/2023/11/21-11-2022-ΚΡΑΤΙΚΟΣ-ΠΡΟΫΠΟΛΟΓΙΣΜΟΣ-2023.pdf";
		String year;

		if (epilogi == 1){
			year= link2023;
		} else if (epilogi == 2){
			year= link2024;
		} else{
			year= link2025;
		}
		if (epilogi == 1){
			yearof= 2023;
		} else if (epilogi == 2){
			yearof= 2024;
		} else{
			yearof= 2025;
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
			 converter.convertiontool();
			 DataStorer Storer = new DataStorer();
			 Storer.Store();
			 
		}
		
    
	
	}

  
}

