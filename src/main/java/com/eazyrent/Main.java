package com.eazyrent;

import com.eazyrent.controller.MainController;
import com.eazyrent.utility.JPAUtility;

import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {
    public static void main(String[] args) {
        Logger.getLogger("org.hibernate").setLevel(Level.SEVERE); // Sadece ERROR ve SEVERE mesajları gösterilir
        Logger.getLogger("org.hibernate.SQL").setLevel(Level.OFF); // SQL sorguları tamamen kapatılır
        MainController mainController = new MainController();
        try {
            mainController.run();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        } finally {
            JPAUtility.closeEntityManagerFactory();
            System.out.println("Program sonlandı.");
        }
    }
}