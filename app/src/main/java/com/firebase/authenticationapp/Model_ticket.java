package com.firebase.authenticationapp;

public class Model_ticket {

    public static final String[][] tab_details = {
            {"cmpt","cmpte","cpte","compte","cte","ompte","compt","comp"},
            {"montant","mntant","mntat","montat","ontant"},
            {"debit","total","totel","tota","totle","tatal","tatai","ratal","rotal","otal","ttle","ttc","net ttc"},
            {"prix","prx"},
            {"merci","mercl","mrci","morci","marci","erci"},
            {"nif"},
            {"stat"},
            {"tva"},
            {"siret","sirt","sire"},
            {"siret_check","\\d{14}|\\d{3}\\s\\d{3}\\s\\d{3}\\s\\d{5}"},
            {"solde","sold","slde","olde"},
            {"bank"},
            {"mga","eur"}
    };
    private String siret="Indéfini";
    private String debit="00,00";
    public static final String API_URI = "https://api.insee.fr/entreprises/sirene/V3/siret/";

    public String getDebit() {
        return debit;
    }

    public void setDebit(String debit) {
        this.debit = debit;
    }

    public String getSiret() {
        return siret;
    }

    public void setSiret(String siret) {
        this.siret = siret;
    }

    public Model_ticket(){}

}
