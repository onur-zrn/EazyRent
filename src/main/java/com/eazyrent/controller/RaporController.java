package com.eazyrent.controller;

import com.eazyrent.entity.Arac;
import com.eazyrent.entity.Kiralama;
import com.eazyrent.entity.Kisi;
import com.eazyrent.enums.AracDurum;
import com.eazyrent.service.AracService;
import com.eazyrent.service.KiralamaService;
import com.eazyrent.service.KisiService;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import static com.eazyrent.utility.Util.*; // ANSI renkleri ve yardımcı metotlar için

public class RaporController {

    private final AracService aracService;
    private final KisiService kisiService;
    private final KiralamaService kiralamaService;

    public RaporController(AracService aracService, KisiService kisiService, KiralamaService kiralamaService) {
        this.aracService = aracService;
        this.kisiService = kisiService;
        this.kiralamaService = kiralamaService;
    }

    public void raporMenu() {
        boolean devamEt = true;
        while (devamEt) {
            baslikYazdir("Raporlar Menüsü");
            writeMenuItem("1. Şuan Kirada Olan Araçları Listele");
            writeMenuItem("2. Boşta Olan Araçları Listele");
            writeMenuItem("3. Müşterinin Kiraladığı Tüm Araçları Listele");
            writeMenuItemRed("0. Ana Menüye Dön");

            int secim = intDegerAl("Seçiminizi yapın: ", 0, 3);

            switch (secim) {
                case 1:
                    kiradakiAraclariListele();
                    enteraBas();
                    break;
                case 2:
                    bostaAraclariListele();
                    enteraBas();
                    break;
                case 3:
                    musteriKiraladigiAraclariListele();
                    enteraBas();
                    break;
                case 0:
                    devamEt = false;
                    printYellow("Raporlar Menüsünden Çıkılıyor...");
                    break;
                default:
                    printRed("Geçersiz seçim! Lütfen belirtilen aralıkta bir sayı girin.");
                    enteraBas();
                    break;
            }
        }
    }

    /**
     * Şuan KIRADA olan (AracDurum.KIRADA) tüm araçları listeler.
     */
    private void kiradakiAraclariListele() {
        baslikYazdir("Şuan Kirada Olan Araçlar");
        List<Arac> kiradakiAraclar = aracService.findByAracDurum(AracDurum.KIRADA);

        if (kiradakiAraclar.isEmpty()) {
            printYellow("Şu an kirada olan araç bulunmamaktadır.");
        } else {
            kiradakiAraclar.stream()
                    .sorted(Comparator.comparing(Arac::getId))
                    .forEach(arac -> System.out.println(ANSI_GREEN + "ID: " + arac.getId() + " - " + arac.getMarka() + " " + arac.getModel() + " (" + arac.getPlaka() + ")" + ANSI_RESET));
        }
    }

    /**
     * Şuan MUSAIT olan (AracDurum.MUSAIT) tüm araçları listeler.
     */
    private void bostaAraclariListele() {
        baslikYazdir("Boşta (Müsait) Olan Araçlar");
        List<Arac> bostaAraclar = aracService.findByAracDurum(AracDurum.MUSAIT);

        if (bostaAraclar.isEmpty()) {
            printYellow("Şu an boşta (kiralanabilir) araç bulunmamaktadır.");
        } else {
            bostaAraclar.stream()
                    .sorted(Comparator.comparing(Arac::getId))
                    .forEach(arac -> System.out.println(ANSI_GREEN + "ID: " + arac.getId() + " - " + arac.getMarka() + " " + arac.getModel() + " (" + arac.getPlaka() + ")" + ANSI_RESET));
        }
    }

    /**
     * Belirli bir müşterinin kiraladığı tüm araçları (geçmiş ve aktif) listeler.
     */
    private void musteriKiraladigiAraclariListele() {
        baslikYazdir("Müşterinin Kiraladığı Tüm Araçları Listele");

        // Önce kişileri listeleyelim ki kullanıcı ID seçebilsin
        printBlue("Kiralamalarını görmek istediğiniz müşteriyi seçin:");
        List<Kisi> kisiler = kisiService.findAll();
        if (kisiler.isEmpty()) {
            printRed("Kayıtlı müşteri bulunmamaktadır. Lütfen önce bir kişi ekleyin.");
            return;
        }
        kisiler.stream()
                .sorted(Comparator.comparing(Kisi::getId))
                .forEach(kisi -> System.out.println(ANSI_CYAN + "ID: " + kisi.getId() + " - " + kisi.getAd() + " " + kisi.getSoyad() + " (TC: " + kisi.getTcKimlikNo() + ")" + ANSI_RESET));

        Long kisiId = (long) intDegerAl("Müşterinin ID'sini girin: ");
        Optional<Kisi> optionalKisi = kisiService.findById(kisiId);

        if (optionalKisi.isEmpty()) {
            printRed("Belirtilen ID'de müşteri bulunamadı.");
            return;
        }

        Kisi secilenKisi = optionalKisi.get();
        printBlue(secilenKisi.getAd() + " " + secilenKisi.getSoyad() + " adlı müşterinin kiraladığı araçlar:");

        List<Kiralama> kiralamalar = kiralamaService.findByKisiId(kisiId);

        if (kiralamalar.isEmpty()) {
            printYellow(secilenKisi.getAd() + " " + secilenKisi.getSoyad() + " adlı müşteriye ait kiralama bulunmamaktadır.");
        } else {
            kiralamalar.stream()
                    .sorted(Comparator.comparing(Kiralama::getId))
                    .forEach(kiralama -> {
                        
                        Optional<Arac> arac = aracService.findById(kiralama.getAracId());
                        String aracBilgi = arac.map(a -> a.getMarka() + " " + a.getModel() + " (" + a.getPlaka() + ")").orElse("Bilinmeyen Araç");

                        System.out.println(ANSI_GREEN + "ID: " + kiralama.getId() +
                                " | Araç: " + aracBilgi +
                                " | Kiralama Tarihi: " + kiralama.getKiralamaTarihi() +
                                " | Teslim Tarihi: " + (kiralama.getTeslimTarihi() != null ? kiralama.getTeslimTarihi() : "Devam Ediyor") +
                                " | Toplam Tutar: " + (kiralama.getToplamTutar() != null ? String.format("%.2f TL", kiralama.getToplamTutar()) : "Hesaplanmadı") + ANSI_RESET);
                    });
        }
    }
}