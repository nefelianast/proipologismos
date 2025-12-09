import java.io.IOException;

public class testmain{
    public static void main(String args[]) throws IOException{
        System.out.println("αυτή η κλάση είναι test κλάση που θαχρησιμοποιει τις κλάσεις DataDownload, DataConvert, DataStorer");
        DataDownload downloader = new DataDownload();
        downloader.Download();
    }
}