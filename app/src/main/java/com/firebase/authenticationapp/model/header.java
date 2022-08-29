package com.firebase.authenticationapp.model;

public class header {
    private static String statut;
    private static String message;

    public static String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

    public static String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
