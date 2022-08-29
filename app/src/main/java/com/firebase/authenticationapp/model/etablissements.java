package com.firebase.authenticationapp.model;

public class etablissements {

    private static String siren;


    private static String nic;


    private static String siret;


    private static String statutDiffusionEtablissement;


    private static String dateCreationEtablissement;


    private static String dateDernierTraitementEtablissement;


    private static String etablissementSiege;


    private static String nombrePeriodesEtablissement;


    public static String getDateCreationEtablissement() {
        return dateCreationEtablissement;
    }

    public static void setDateCreationEtablissement(String dateCreationEtablissement) {
        etablissements.dateCreationEtablissement = dateCreationEtablissement;
    }

    public static String getDateDernierTraitementEtablissement() {
        return dateDernierTraitementEtablissement;
    }

    public static void setDateDernierTraitementEtablissement(String dateDernierTraitementEtablissement) {
        etablissements.dateDernierTraitementEtablissement = dateDernierTraitementEtablissement;
    }

    public static String getEtablissementSiege() {
        return etablissementSiege;
    }

    public static void setEtablissementSiege(String etablissementSiege) {
        etablissements.etablissementSiege = etablissementSiege;
    }

    public static String getNombrePeriodesEtablissement() {
        return nombrePeriodesEtablissement;
    }

    public static void setNombrePeriodesEtablissement(String nombrePeriodesEtablissement) {
        etablissements.nombrePeriodesEtablissement = nombrePeriodesEtablissement;
    }

    public static String getStatutDiffusionEtablissement() {
        return statutDiffusionEtablissement;
    }

    public static void setStatutDiffusionEtablissement(String statutDiffusionEtablissement) {
        etablissements.statutDiffusionEtablissement = statutDiffusionEtablissement;
    }

    public static String getSiren() {
        return siren;
    }

    public void setSiren(String siren) {
        this.siren = siren;
    }


    public static String getNic() {
        return nic;
    }

    public void setNic(String nic) {
        this.nic = nic;
    }

    public static String getSiret() {
        return siret;
    }

    public void setSiret(String siret) {
        this.siret = siret;
    }

}
