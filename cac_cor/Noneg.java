package cac_cor;

public class Noneg {
    public static double NonegNum(double a) {
       try {
        if (a<0) {
           throw new NegativeNumberExc("Αρνητικός αριθμός");
        }
         return a;
        } catch (NegativeNumberExc e) {
            System.out.println("Λαθός "+e.getMessage());
            return 0;
        }
    }
}
