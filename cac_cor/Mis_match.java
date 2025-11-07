package cac_cor;
public class Mis_match {
    

    public static double contodouble (String a) {
        try {
                return Double.parseDouble(a) ;  
        }  
             catch (NumberFormatException e) {
              System.out.println("δεν ειναι δεχτεσ αυτες οι τιμεσ");
             }
    
    return 0;
}
}
