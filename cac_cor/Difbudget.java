package cac_cor;
public class Difbudget {
    public String difpresbudget ( double budget1 ,double budget2 ) {
    double dif = budget1 - budget2;
    double pre = dif/budget2*100;
    return dif>0?"αυξηθικε απο το πρωιγουμενο ετος "+pre+" %" : "μειοθηκε απο το πρωιγουμενο ετος "+(-pre)+" %";
    }
    public String dif ( double budget1 ,double budget2 ) {
    double dif = budget1 - budget2;
    double pre = dif/budget2*100;
    return dif>0?" me diafora"+pre+" %" : " me diafora "+(-pre)+" %";
    }
}