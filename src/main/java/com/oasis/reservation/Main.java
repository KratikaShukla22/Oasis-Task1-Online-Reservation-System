package com.oasis.reservation;

public class Main {
    public static void main(String[] args) {

        DatabaseConnection.connect();
        DatabaseConnection.createDefaultUser();

    }


}
