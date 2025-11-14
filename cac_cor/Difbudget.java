package cac_cor;
//κλαση υπολογισμου διαφωρων 
public class Difbudget {
    public String difpresbudget (double budget1, double budget2) {
        if (budget2 != 0) { 
        double dif = budget1 - budget2;
        double pre = dif / budget2 * 100;
        return dif > 0 ? "Αυξήθηκε από το προηγούμενο έτος" + pre + " %" : "Μειώθηκε από το προηγούμενο έτος" + (-pre) + " %";
        } else {
        return "Δεν μπόρεσε να γίνει η σύγκριση";
        }
    }
}
