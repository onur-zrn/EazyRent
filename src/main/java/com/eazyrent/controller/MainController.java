package com.eazyrent.controller;

import com.eazyrent.repository.AracRepository;
import com.eazyrent.repository.KisiRepository;
import com.eazyrent.repository.KiralamaRepository;

import com.eazyrent.service.AracService;
import com.eazyrent.service.KisiService;
import com.eazyrent.service.KiralamaService;
import com.eazyrent.utility.DataInitializer;
import static com.eazyrent.utility.Util.*;

public class MainController {

    private AracService aracService;
    private KisiService kisiService;
    private KiralamaService kiralamaService;

    AracController aracController;
    KisiController kisiController;
    KiralamaController kiralamaController;
    RaporController raporController; // Raporlar için ayrı bir controller

    private DataInitializer dataInitializer; // DataInitializer objesi

    public MainController() {
        // Repository katmanlarını initialize ediyoruz
        AracRepository aracRepository = new AracRepository();
        KisiRepository kisiRepository = new KisiRepository();
        KiralamaRepository kiralamaRepository = new KiralamaRepository();

        // Service katmanlarını initialize ediyoruz
        this.aracService = new AracService(aracRepository);
        this.kisiService = new KisiService(kisiRepository);
        this.kiralamaService = new KiralamaService(kiralamaRepository, aracService, kisiService);

        // DataInitializer'ı servislerle birlikte initialize ediyoruz
        this.dataInitializer = new DataInitializer(aracService, kisiService, kiralamaService);

        // Controller'ları initialize ediyoruz
        this.aracController = new AracController(aracService);
        this.kisiController = new KisiController(kisiService,kiralamaService);
        this.kiralamaController = new KiralamaController(kiralamaService, aracService, kisiService);
        this.raporController = new RaporController(aracService, kisiService, kiralamaService);
    }

    public void run() {
        // Uygulama başlarken test verilerini oluştur
        dataInitializer.initData();

        boolean devamEt = true;
        while (devamEt) {
            anaMenuyuGoster();
            int secim = intDegerAl("Lütfen bir seçim yapın: ", 1, 5);

            switch (secim) {
                case 1:
                    aracController.aracMenu();
                    break;
                case 2:
                    kisiController.kisiMenu(); // Kişi işlemlerine yönlendir
                    break;
                case 3:
                    kiralamaController.kiralamaMenu(); // Kiralama işlemlerine yönlendir
                    break;
                case 4:
                    raporController.raporMenu(); // Raporlama işlemlerine yönlendir
                    break;
                case 5:
                    devamEt = false;
                    printRed("Uygulamadan çıkılıyor...");
                    break;
                default:
                    printRed("Geçersiz seçim! Lütfen 1 ile 5 arasında bir sayı girin.");
                    enteraBas();
                    break;
            }
        }
    }

    private void anaMenuyuGoster() {
        baslikYazdir("Araç Kiralama Sistemi Ana Menü");
        writeMenuItem("1. Araç İşlemleri");
        writeMenuItem("2. Kişi İşlemleri");
        writeMenuItem("3. Kiralama İşlemleri");
        writeMenuItem("4. Raporlar");
        writeMenuItemRed("5. Çıkış");
    }
}