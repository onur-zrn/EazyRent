package com.eazyrent.controller;

import com.eazyrent.entity.Arac;
import com.eazyrent.enums.AracDurum;
import com.eazyrent.service.AracService;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import static com.eazyrent.utility.Util.*;

public class AracController {

    private final AracService aracService;

    public AracController(AracService aracService) {
        this.aracService = aracService;
    }

    public void aracMenu() {
        boolean devamEt = true;
        while (devamEt) {
            baslikYazdir("Araç İşlemleri Menüsü");
            writeMenuItem("1. Yeni Araç Ekle");
            writeMenuItem("2. Araç Bilgilerini Güncelle");
            writeMenuItem("3. Araç Sil");
            writeMenuItem("4. Tüm Araçları Listele");
            writeMenuItem("5. Araç Arama Menüsü");
            writeMenuItemRed("0. Ana Menüye Dön");

            int secim = intDegerAl("Seçiminizi yapın: ", 0, 5);

            switch (secim) {
                case 1:
                    yeniAracEkle();
                    enteraBas();
                    break;
                case 2:
                    aracGuncelle();
                    enteraBas();
                    break;
                case 3:
                    aracSil();
                    enteraBas();
                    break;
                case 4:
                    tumAraclatiListele();
                    enteraBas();
                    break;
                case 5:
                    aracAramaMenu();
                    break;
                case 0:
                    devamEt = false;
                    printYellow("Araç İşlemleri Menüsünden Çıkılıyor...");
                    break;
                default:
                    printRed("Geçersiz seçim! Lütfen belirtilen aralıkta bir sayı girin.");
                    enteraBas();
                    break;
            }
        }
    }

    // Araç Arama Menüsü
    private void aracAramaMenu() {
        boolean devamEt = true;
        while (devamEt) {
            baslikYazdir("Araç Arama Menüsü");
            writeMenuItem("1. Plakaya Göre Araç Bul");
            writeMenuItem("2. Duruma Göre Araçları Listele");
            writeMenuItem("3. Marka ve Modele Göre Araç Bul");
            writeMenuItemRed("0. Geri Dön");

            int secim = intDegerAl("Seçiminizi yapın: ", 0, 3);

            switch (secim) {
                case 1:
                    plakayaGoreAracBul();
                    break;
                case 2:
                    durumaGoreAracListele();
                    break;
                case 3:
                    markaModeleGoreAracBul();
                    break;
                case 0:
                    devamEt = false;
                    printYellow("Araç Arama Menüsünden Çıkılıyor...");
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


    private void yeniAracEkle() {
        baslikYazdir("Yeni Araç Ekle");
        String marka = stringDegerAl("Marka: ");
        String model = stringDegerAl("Model: ");
        String plaka = stringDegerAl("Plaka: ");
        double gunlukFiyat = doubleDegerAl("Günlük Fiyat: ");

        AracDurum aracDurum = null;
        boolean gecerliDurum = false;
        while (!gecerliDurum) {
            printBlue("Araç Durumu Seçimi:");
            printInColor("1. MUSAIT", ANSI_CYAN);
            printInColor("2. BAKIMDA", ANSI_CYAN);
            int durumSecim = intDegerAl("Lütfen bir durum seçin (1-2): ", 1, 2);
            switch (durumSecim) {
                case 1: aracDurum = AracDurum.MUSAIT; gecerliDurum = true; break;
                case 2: aracDurum = AracDurum.BAKIMDA; gecerliDurum = true; break;
                default: printRed("Geçersiz durum seçimi.");
            }
        }

        Arac yeniArac = Arac.builder()
                .marka(marka)
                .model(model)
                .plaka(plaka)
                .gunlukFiyat(gunlukFiyat)
                .aracDurum(aracDurum)
                .build();

        try {
            aracService.save(yeniArac);
            printGreen("Araç başarıyla eklendi: " + yeniArac.getPlaka());
        } catch (Exception e) {
            printRed("Araç eklenirken bir hata oluştu: " + e.getMessage());
        }
    }

    private void aracGuncelle() {
        baslikYazdir("Araç Bilgilerini Güncelle");
        tumAraclatiListele(); // Güncelleme öncesi tüm araçları listele
        if (aracService.findAll().isEmpty()) { // Eğer liste boşsa, işlem yapma
            return;
        }

        Long id = (long) intDegerAl("Güncellenecek aracın ID'sini girin: ");
        Optional<Arac> optionalArac = aracService.findById(id);

        if (optionalArac.isPresent()) {
            Arac arac = optionalArac.get();
            printBlue("Mevcut Araç Bilgileri: " + arac);

            //true boş bırakılabilir anlamında
            String yeniMarka = stringDegerAl("Yeni Marka (" + arac.getMarka() + "): ", true);
            if (!yeniMarka.isEmpty()) arac.setMarka(yeniMarka);

            String yeniModel = stringDegerAl("Yeni Model (" + arac.getModel() + "): ", true);
            if (!yeniModel.isEmpty()) arac.setModel(yeniModel);

            String yeniPlaka = stringDegerAl("Yeni Plaka (" + arac.getPlaka() + "): ", true);
            if (!yeniPlaka.isEmpty()) arac.setPlaka(yeniPlaka);

            double yeniFiyat = doubleDegerAl("Yeni Günlük Fiyat (" + arac.getGunlukFiyat() + "): ", true);
            if (yeniFiyat != -1.0) arac.setGunlukFiyat(yeniFiyat); // -1.0 özel bir işaretçi olarak kullanılabilir

            if (arac.getAracDurum() == AracDurum.KIRADA) {
                printRed("Bu araç kirada olduğu için durumu güncellenemez. Diğer bilgiler güncellenebilir.");
            } else {
                AracDurum aracDurum = null;
                boolean gecerliDurum = false;
                while (!gecerliDurum) {
                    printBlue("Yeni Araç Durumu (" + arac.getAracDurum() + "):");
                    printInColor("1. MUSAIT", ANSI_CYAN);
                    printInColor("2. BAKIMDA", ANSI_CYAN);
                    printInColor("0. Değiştirme", ANSI_YELLOW);
                    int durumSecim = intDegerAl("Lütfen bir durum seçin (0-2): ", 0, 2);
                    if (durumSecim == 0) {
                        gecerliDurum = true; // No change
                    } else {
                        switch (durumSecim) {
                            case 1: aracDurum = AracDurum.MUSAIT; gecerliDurum = true; break;
                            case 2: aracDurum = AracDurum.BAKIMDA; gecerliDurum = true; break;
                            default: printRed("Geçersiz durum seçimi.");
                        }
                    }
                }
                if (aracDurum != null) {
                    arac.setAracDurum(aracDurum);
                }
            }


            try {
                aracService.update(arac);
                printGreen("Araç başarıyla güncellendi: " + arac.getPlaka());
            } catch (Exception e) {
                printRed("Araç güncellenirken bir hata oluştu: " + e.getMessage());
            }
        } else {
            printRed("Belirtilen ID'de araç bulunamadı.");
        }
    }


    private void aracSil() {
        baslikYazdir("Araç Sil");
        tumAraclatiListele(); // Silme öncesi tüm araçları listele
        if (aracService.findAll().isEmpty()) { // Eğer liste boşsa, işlem yapma
            return;
        }

        Long id = (long) intDegerAl("Silinecek aracın ID'sini girin: ");
        Optional<Arac> optionalArac = aracService.findById(id);

        if (optionalArac.isPresent()) {
            Arac arac = optionalArac.get();
            // Aracın kirada olup olmadığını kontrol et
            if (arac.getAracDurum() == AracDurum.KIRADA) {
                printRed("Hata: Bu araç kirada olduğu için silinemez. Önce kiralama işlemini sonlandırmalısınız.");
            } else {
                aracService.deleteById(id);
                printGreen("Araç başarıyla silindi: " + arac.getPlaka());
            }
        } else {
            printRed("Belirtilen ID'de araç bulunamadı.");
        }
    }

    private void tumAraclatiListele() {
        baslikYazdir("Tüm Araçlar");
        List<Arac> araclar = aracService.findAll(); // Servisten tüm araçları al

        if (araclar.isEmpty()) {
            printYellow("Kayıtlı araç bulunmamaktadır.");
        } else {
            araclar.stream()
                    .sorted(Comparator.comparing(Arac::getId)) // ID'ye göre artan sıralama
                    .forEach(arac -> System.out.println(ANSI_GREEN + "ID: " + arac.getId() + " - " + arac + ANSI_RESET));
        }
    }

    private void plakayaGoreAracBul() {
        baslikYazdir("Plakaya Göre Araç Bul");
        String plaka = stringDegerAl("Aranacak plaka: ");
        Optional<Arac> optionalArac = aracService.findByPlaka(plaka);

        if (optionalArac.isPresent()) {
            printGreen("Araç bulundu: " + optionalArac.get());
        } else {
            printYellow("Belirtilen plakada araç bulunamadı.");
        }
    }

    private void durumaGoreAracListele() {
        baslikYazdir("Duruma Göre Araçları Listele");
        printBlue("Listelenecek Araç Durumu Seçimi:");
        printInColor("1. MUSAIT", ANSI_CYAN);
        printInColor("2. KIRADA", ANSI_CYAN);
        printInColor("3. BAKIMDA", ANSI_CYAN);
        int durumSecim = intDegerAl("Lütfen bir durum seçin (1-3): ", 1, 3);

        AracDurum secilenDurum = switch (durumSecim) {
            case 1 -> AracDurum.MUSAIT;
            case 2 -> AracDurum.KIRADA;
            case 3 -> AracDurum.BAKIMDA;
            default -> null;
        };

        if (secilenDurum != null) {
            List<Arac> araclar = aracService.findByAracDurum(secilenDurum);
            if (araclar.isEmpty()) {
                printYellow("Belirtilen durumda araç bulunmamaktadır.");
            } else {
                printGreen(secilenDurum + " Durumundaki Araçlar:");
                araclar.forEach(arac -> System.out.println(ANSI_GREEN + arac + ANSI_RESET));
            }
        }
    }

    // Marka ve Modele Göre Araç Bul
    private void markaModeleGoreAracBul() {
        baslikYazdir("Marka ve Modele Göre Araç Bul");
        String marka = stringDegerAl("Aranacak marka: ");
        String model = stringDegerAl("Aranacak model: ");

        List<Arac> araclar = aracService.findByMarkaAndModel(marka, model);

        if (araclar.isEmpty()) {
            printYellow("Belirtilen marka ve modele sahip araç bulunamadı.");
        } else {
            printGreen("Bulunan Araçlar:");
            araclar.forEach(arac -> System.out.println(ANSI_GREEN + arac + ANSI_RESET));
        }
    }
}