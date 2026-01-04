
  package analysis;

import cac_cor.BudgetYear;
import cac_cor.BudgetYear;
import cac_cor.Expense;

public class StatisticalAnalysis {
   //Χαρακτηρισμός Κρατικού Προυπολογισμού
    public static String analyzeBudget(BudgetYear budgetYear) {
        double balance = budgetYear.getBalance();

        if (balance > 0) {
            return "Ο προϋπολογισμός είναι πλεονασματικός όταν τα συνολικά έσοδα είναι μεγαλύτερα από τα έξοδα.
Αυτό σημαίνει ότι υπάρχει οικονομικό πλεόνασμα, το οποίο μπορεί να χρησιμοποιηθεί για αποταμίευση, επενδύσεις ή κάλυψη μελλοντικών αναγκών.";
        } else if (balance == 0) {
            return "Ο προϋπολογισμός είναι ισοσκελισμένος όταν τα έσοδα και τα έξοδα είναι ίσα.
Δεν υπάρχει ούτε κέρδος ούτε ζημία, γεγονός που δείχνει καλή οικονομική ισορροπία αλλά χωρίς περιθώριο ασφάλειας.";
        } else {
            return "Ο προϋπολογισμός είναι ελλειμματικός όταν τα έξοδα ξεπερνούν τα έσοδα.
Αυτό υποδηλώνει οικονομική πίεση και την ανάγκη για μείωση δαπανών ή αύξηση εσόδων ώστε να αποφευχθούν προβλήματα στο μέλλον.";
        }
    }
}
   //Συγκρίσεις Προυπολογισμών,εξόδων,εσόδων χρονικά 
    
   //1)Για τον κρατικό Προυπολογισμό
public static String compareBudgetsDetailed(BudgetYear b1, BudgetYear b2) {

    // Ισοζύγιο
    double balance1 = b1.getBalance();
    double balance2 = b2.getBalance();

    // Συνολικά έσοδα και έξοδα
    double revenue1 = b1.getTotalRevenue();
    double revenue2 = b2.getTotalRevenue();

    double expense1 = b1.getTotalExpenses();
    double expense2 = b2.getTotalExpenses();

    // Ποσοστιαία διαφορά ισοζυγίου (αποφυγή διαίρεσης με μηδέν)
    double percentageDiff = 0;
    if (balance1 != 0) {
        percentageDiff = ((balance2 - balance1) / Math.abs(balance1)) * 100;
    }

    // Ξεκινάμε το μήνυμα
    String msg = "Σύγκριση προϋπολογισμού μεταξύ των ετών " + b1.getYear() + " και " + b2.getYear() + ":\n\n";

    // Σύγκριση ισοζυγίου
    if (balance1 > balance2) {
        msg += "Το έτος " + b1.getYear() + " έχει καλύτερο οικονομικό αποτέλεσμα από το έτος " + b2.getYear() + ".\n" +
               "Το ισοζύγιο είναι υψηλότερο κατά " + String.format("%.2f", Math.abs(percentageDiff)) + "%.\n\n";
    } else if (balance1 < balance2) {
        msg += "Το έτος " + b2.getYear() + " έχει καλύτερο οικονομικό αποτέλεσμα από το έτος " + b1.getYear() + ".\n" +
               "Το ισοζύγιο είναι υψηλότερο κατά " + String.format("%.2f", Math.abs(percentageDiff)) + "%.\n\n";
    } else {
        msg += "Τα έτη έχουν ακριβώς το ίδιο οικονομικό αποτέλεσμα.\n\n";
    }

    // Σύγκριση εσόδων
    if (revenue1 > revenue2) {
        msg += "Τα έσοδα ήταν υψηλότερα στο έτος " + b1.getYear() + " (" +
               String.format("%.2f", revenue1) + " έναντι " + String.format("%.2f", revenue2) + ").\n";
    } else if (revenue1 < revenue2) {
        msg += "Τα έσοδα ήταν υψηλότερα στο έτος " + b2.getYear() + " (" +
               String.format("%.2f", revenue2) + " έναντι " + String.format("%.2f", revenue1) + ").\n";
    } else {
        msg += "Τα έσοδα ήταν ίδια και στα δύο έτη (" + String.format("%.2f", revenue1) + ").\n";
    }

    // Σύγκριση εξόδων
    if (expense1 > expense2) {
        msg += "Τα έξοδα ήταν υψηλότερα στο έτος " + b1.getYear() + " (" +
               String.format("%.2f", expense1) + " έναντι " + String.format("%.2f", expense2) + ").\n";
    } else if (expense1 < expense2) {
        msg += "Τα έξοδα ήταν υψηλότερα στο έτος " + b2.getYear() + " (" +
               String.format("%.2f", expense2) + " έναντι " + String.format("%.2f", expense1) + ").\n";
    } else {
        msg += "Τα έξοδα ήταν ίδια και στα δύο έτη (" + String.format("%.2f", expense1) + ").\n";
    }

    msg += "\nΑυτό το αποτέλεσμα δείχνει τη συνολική οικονομική τάση και την αποδοτικότητα της διαχείρισης εσόδων και εξόδων.";

    return msg;
}

  // 2)Συγκριση χρονική εξόδων και εόδων , πέρνει το είδος και το έτος
    //Εξοδα 
    public static String compareExpenseByCategory(BudgetYear b1, BudgetYear b2, String category) {

        // Βρίσκουμε το Expense για κάθε έτος
        Expense e1 = b1.getExpenses().stream()
                .filter(e -> e.getCategory().equalsIgnoreCase(category))
                .findFirst()
                .orElse(null);

        Expense e2 = b2.getExpenses().stream()
                .filter(e -> e.getCategory().equalsIgnoreCase(category))
                .findFirst()
                .orElse(null);

        // Έλεγχος αν υπάρχει έξοδο για την κατηγορία
        if (e1 == null && e2 == null) {
            return "Δεν υπάρχουν δεδομένα για την κατηγορία '" + category + "' στα δύο έτη.";
        } else if (e1 == null) {
            return "Δεν υπάρχουν δεδομένα για την κατηγορία '" + category + "' στο έτος " + b1.getYear();
        } else if (e2 == null) {
            return "Δεν υπάρχουν δεδομένα για την κατηγορία '" + category + "' στο έτος " + b2.getYear();
        }

        // Ποσά
        double amount1 = e1.getAmount();
        double amount2 = e2.getAmount();

        // Ποσοστιαία μεταβολή
        double percentageChange = 0;
        if (amount1 != 0) {
            percentageChange = ((amount2 - amount1) / Math.abs(amount1)) * 100;
        }

        // Δημιουργία  μηνύματος
        String msg = "Σύγκριση κατηγορίας '" + category + "' μεταξύ των ετών " +
                 b1.getYear() + " και " + b2.getYear() + ":\n" +
                 "Έτος " + b1.getYear() + "  Ποσό: " + String.format("%.2f", amount1) + "\n" +
                 "Έτος " + b2.getYear() + "  Ποσό: " + String.format("%.2f", amount2) + "\n";

    if (amount2 > amount1) {
        msg += "Το ποσό αυξήθηκε κατά " + String.format("%.2f", percentageChange) +
               "% σε σχέση με το προηγούμενο έτος.\n" +
               "Η κατηγορία έχει μεγαλύτερη επιβάρυνση.";
    } else if (amount2 < amount1) {
        msg += "Το ποσό μειώθηκε κατά " + String.format("%.2f", Math.abs(percentageChange)) +
               "% σε σχέση με το προηγούμενο έτος.\n" +
               "Η κατηγορία έχει μικρότερη επιβάρυνση.";
    } else {
        msg += "Δεν παρατηρείται αλλαγή στο ποσό της κατηγορίας μεταξύ των δύο ετών.";
    }

    return msg;
}

   //Εσοδα 
    public static String compareRevenueByCategory(BudgetYear b1, BudgetYear b2, String category) {

        // Βρίσκουμε το Revenue για κάθε έτος και την κατηγορία
        Revenue r1 = b1.getRevenues().stream()
                .filter(r -> r.getCategory().equalsIgnoreCase(category) && r.getYear() == b1.getYear())
                .findFirst()
                .orElse(null);

        Revenue r2 = b2.getRevenues().stream()
                .filter(r -> r.getCategory().equalsIgnoreCase(category) && r.getYear() == b2.getYear())
                .findFirst()
                .orElse(null);

        // Έλεγχος αν υπάρχει revenue για την κατηγορία
        if (r1 == null && r2 == null) {
            return "Δεν υπάρχουν δεδομένα για την κατηγορία '" + category + "' στα δύο έτη.";
        } else if (r1 == null) {
            return "Δεν υπάρχουν δεδομένα για την κατηγορία '" + category + "' στο έτος " + b1.getYear();
        } else if (r2 == null) {
            return "Δεν υπάρχουν δεδομένα για την κατηγορία '" + category + "' στο έτος " + b2.getYear();
        }

        double amount1 = r1.getAmount();
        double amount2 = r2.getAmount();

        // Ποσοστιαία μεταβολή
        double percentageChange = 0;
        if (amount1 != 0) {
            percentageChange = ((amount2 - amount1) / Math.abs(amount1)) * 100;
        }

        // Δημιουργία μηνύματος 
        String msg = "Σύγκριση εσόδων για την κατηγορία '" + category + "' μεταξύ των ετών " +
                     b1.getYear() + " και " + b2.getYear() + ":\n" +
                     "Έτος " + b1.getYear() + " Ποσό: " + String.format("%.2f", amount1) + "\n" +
                     "Έτος " + b2.getYear() + " Ποσό: " + String.format("%.2f", amount2) + "\n";

        if (amount2 > amount1) {
            msg += "Τα έσοδα αυξήθηκαν κατά " + String.format("%.2f", percentageChange) +
                   "% σε σχέση με το προηγούμενο έτος.\n" +
                   "Η κατηγορία έχει μεγαλύτερη εισροή εσόδων.";
        } else if (amount2 < amount1) {
            msg += "Τα έσοδα μειώθηκαν κατά " + String.format("%.2f", Math.abs(percentageChange)) +
                   "% σε σχέση με το προηγούμενο έτος.\n" +
                   "Η κατηγορία έχει μικρότερη εισροή εσόδων.";
        } else {
            msg += "Δεν παρατηρείται αλλαγή στα έσοδα της κατηγορίας μεταξύ των δύο ετών.";
        }

        return msg;
    }
// Σύγκριση προυπολογισμού υπουργίων 
...

// ΜΕΡΟΣ ΠΡΟΥΠΟΛΟΓΙΣΜΟΥ ΚΑΘΕ ΚΑΤΗΓΟΡΙΑΣ
...
// ΥΠΟΘΕΤΙΚΑ ΣΕΝΑΡΙΑ
...



