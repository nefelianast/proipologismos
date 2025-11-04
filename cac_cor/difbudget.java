package cac_cor;
public class difbudget {
    public String difpresbudget ( double budget1 ,double budget2 ) {
    double dif = budget1 - budget2;
    double pre = dif/budget2*100;
    return dif>0?"αυξηθικε απο το πρωιγουμενο ετος "+pre+" %" : "μειοθηκε απο το πρωιγουμενο ετος "+(-pre)+" %";
    }
}