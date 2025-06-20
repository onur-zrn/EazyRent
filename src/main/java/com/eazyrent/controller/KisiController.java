package com.eazyrent.controller;

import com.eazyrent.entity.Kisi;
import com.eazyrent.service.KiralamaService;
import com.eazyrent.service.KisiService;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import static com.eazyrent.utility.Util.*;

public class KisiController {

    private final KisiService kisiService;
    private final KiralamaService kiralamaService;

    public KisiController(KisiService kisiService, KiralamaService kiralamaService) {
        this.kisiService = kisiService;
        this.kiralamaService = kiralamaService;
    }

    public void kisiMenu() {
        boolean devamEt = true;
        while (devamEt) {
            baslikYazdir("Kişi İşlemleri Menüsü");
            writeMenuItem("1. Yeni Kişi Ekle");
            writeMenuItem("2. Kişi Bilgilerini Güncelle");
            writeMenuItem("3. Kişi Sil");
            writeMenuItem("4. Tüm Kişileri Listele");
            writeMenuItem("5. Kişi Arama Menüsü");
            writeMenuItemRed("0. Ana Menüye Dön");

            int secim = intDegerAl("Seçiminizi yapın: ", 0, 5);

            switch (secim) {
                case 1:
                    yeniKisiEkle();
                    enteraBas();
                    break;
                case 2:
                    kisiGuncelle();
                    enteraBas();
                    break;
                case 3:
                    kisiSil();
                    enteraBas();
                    break;
                case 4:
                    tumKisileriListele();
                    enteraBas();
                    break;
                case 5:
                    kisiAramaMenu();
                    break;
                case 0:
                    devamEt = false;
                    printYellow("Kişi İşlemleri Menüsünden Çıkılıyor...");
                    break;
                default:
                    printRed("Geçersiz seçim! Lütfen belirtilen aralıkta bir sayı girin.");
                    enteraBas();
                    break;
            }
        }
    }

    private void kisiAramaMenu() {
        boolean devamEt = true;
        while (devamEt) {
            baslikYazdir("Kişi Arama Menüsü");
            writeMenuItem("1. TC Kimlik Numarasına Göre Kişi Bul");
            writeMenuItem("2. Ad ve Soyada Göre Kişi Bul");
            writeMenuItemRed("0. Geri Dön");

            int secim = intDegerAl("Seçiminizi yapın: ", 0, 2);

            switch (secim) {
                case 1:
                    tcKimlikNoIleKisiBul();
                    break;
                case 2: // YENİ CASE
                    adSoyadaGoreKisiBul();
                    break;
                case 0:
                    devamEt = false;
                    printYellow("Kişi Arama Menüsünden Çıkılıyor...");
                    break;
                default:
                    printRed("Geçersiz seçim! Lütfen belirtilen aralıkta bir sayı girin.");
                    break;
            }
            if (devamEt) {
                enteraBas();
            }
        }
    }


    private void yeniKisiEkle() {
        baslikYazdir("Yeni Kişi Ekle");
        String ad = stringDegerAl("Ad: ");
        String soyad = stringDegerAl("Soyad: ");
        String tcKimlik = tcKimlikNoDegerAl("TC Kimlik Numarası (11 hane): ");

        String telefon = telefonDegerAl("Telefon Numarası: ");
        String email = eMailDegerAl("Email Adresi. ");

        Kisi yeniKisi = Kisi.builder()
                .ad(ad)
                .soyad(soyad)
                .tcKimlikNo(tcKimlik)
                .telefon(telefon != null && !telefon.isEmpty() ? telefon : null)
                .email(email != null && !email.isEmpty() ? email : null)
                .build();

        try {
            kisiService.save(yeniKisi);
            printGreen("Kişi başarıyla eklendi: " + yeniKisi.getAd() + " " + yeniKisi.getSoyad());
        } catch (Exception e) {
            printRed("Kişi eklenirken bir hata oluştu: " + e.getMessage());
        }
    }

    private void kisiGuncelle() {
        baslikYazdir("Kişi Bilgilerini Güncelle");
        tumKisileriListele();
        if (kisiService.findAll().isEmpty()) {
            return;
        }

        Long id = (long) intDegerAl("Güncellenecek kişinin ID'sini girin: ");
        Optional<Kisi> optionalKisi = kisiService.findById(id);

        if (optionalKisi.isPresent()) {
            Kisi kisi = optionalKisi.get();
            printBlue("Mevcut Kişi Bilgileri: " + kisi);

            String yeniAd = stringDegerAl("Yeni Ad (" + kisi.getAd() + "): ", true);
            if (!yeniAd.isEmpty()) kisi.setAd(yeniAd);

            String yeniSoyad = stringDegerAl("Yeni Soyad (" + kisi.getSoyad() + "): ", true);
            if (!yeniSoyad.isEmpty()) kisi.setSoyad(yeniSoyad);

            String yeniTelefon = telefonDegerAl("Yeni Telefon Numarası (" + (kisi.getTelefon() != null ? kisi.getTelefon() : "") + "): ", true);
            if (yeniTelefon!=null) kisi.setTelefon(yeniTelefon);

            String yeniEmail = eMailDegerAl("Yeni Email Adresi (" + (kisi.getEmail() != null ? kisi.getEmail() : "") + "): ", true);
            if (yeniEmail!=null) kisi.setEmail(yeniEmail);

            try {
                kisiService.update(kisi);
                printGreen("Kişi başarıyla güncellendi: " + kisi.getAd() + " " + kisi.getSoyad());
            } catch (Exception e) {
                printRed("Kişi güncellenirken bir hata oluştu: " + e.getMessage());
            }
        } else {
            printRed("Belirtilen ID'de kişi bulunamadı.");
        }
    }

    private void kisiSil() {
        baslikYazdir("Kişi Sil");
        tumKisileriListele();
        if (kisiService.findAll().isEmpty()) {
            return;
        }

        Long id = (long) intDegerAl("Silinecek kişinin ID'sini girin: ");
        Optional<Kisi> optionalKisi = kisiService.findById(id);

        if (optionalKisi.isPresent()) {
            // Kişinin devam eden bir kiralaması olup olmadığını kontrol et
            if (kiralamaService.existsAnyOngoingRentalForKisi(id)) {
                printRed("Hata: Bu kişinin devam eden bir araç kiralaması bulunmaktadır. Kişi silinemez.");
            } else {
                kisiService.deleteById(id);
                printGreen("Kişi başarıyla silindi: " + optionalKisi.get().getAd() + " " + optionalKisi.get().getSoyad());
            }
        } else {
            printRed("Belirtilen ID'de kişi bulunamadı.");
        }
    }

    private void tumKisileriListele() {
        baslikYazdir("Tüm Kişiler");
        List<Kisi> kisiler = kisiService.findAll();

        if (kisiler.isEmpty()) {
            printYellow("Kayıtlı kişi bulunmamaktadır.");
        } else {
            kisiler.stream()
                    .sorted(Comparator.comparing(Kisi::getId))
                    .forEach(kisi -> System.out.println(ANSI_GREEN + "ID: " + kisi.getId() + " - " + kisi + ANSI_RESET));
        }
    }

    private void tcKimlikNoIleKisiBul() {
        baslikYazdir("TC Kimlik Numarasına Göre Kişi Bul");
        String tcKimlik = stringDegerAl("Aranacak TC Kimlik Numarası: ");
        Optional<Kisi> optionalKisi = kisiService.findByTcKimlik(tcKimlik);

        if (optionalKisi.isPresent()) {
            printGreen("Kişi bulundu: " + optionalKisi.get());
        } else {
            printYellow("Belirtilen TC Kimlik Numarasına sahip kişi bulunamadı.");
        }
    }

    /**
     * Ad ve Soyad bilgilerine göre kişileri arar ve listeler.
     */
    private void adSoyadaGoreKisiBul() {
        baslikYazdir("Ad ve Soyada Göre Kişi Bul");
        String ad = stringDegerAl("Aranacak ad: ");
        String soyad = stringDegerAl("Aranacak soyad: ");

        List<Kisi> kisiler = kisiService.findByAdAndSoyad(ad, soyad);

        if (kisiler.isEmpty()) {
            printYellow("Belirtilen ad ve soyada sahip kişi bulunamadı.");
        } else {
            printGreen("Bulunan Kişiler:");
            kisiler.stream()
                    .sorted(Comparator.comparing(Kisi::getId)) // Sonuçları ID'ye göre sırala
                    .forEach(kisi -> System.out.println(ANSI_GREEN + "ID: " + kisi.getId() + " - " + kisi + ANSI_RESET));
        }
    }
}