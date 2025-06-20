package com.eazyrent;

import com.eazyrent.controller.MainController;
import com.eazyrent.utility.JPAUtility;

public class Main {
    public static void main(String[] args) {
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