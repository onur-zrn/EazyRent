package com.eazyrent.controller;

import com.eazyrent.entity.Arac;
import com.eazyrent.entity.Kiralama;
import com.eazyrent.entity.Kisi;
import com.eazyrent.enums.AracDurum;
import com.eazyrent.service.AracService;
import com.eazyrent.service.KiralamaService;
import com.eazyrent.service.KisiService;
import jakarta.transaction.Transactional;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import static com.eazyrent.utility.Util.*;

public class KiralamaController {

    private final KiralamaService kiralamaService;
    private final AracService aracService;
    private final KisiService kisiService;

    public KiralamaController(KiralamaService kiralamaService, AracService aracService, KisiService kisiService) {
        this.kiralamaService = kiralamaService;
        this.aracService = aracService;
        this.kisiService = kisiService;
    }

    public void kiralamaMenu() {
        boolean devamEt = true;
        while (devamEt) {
            baslikYazdir("Kiralama İşlemleri Menüsü");
            writeMenuItem("1. Araç Kiralama"); // Yeni Kiralama Oluşturma
            writeMenuItem("2. Araç Teslim Alma"); // Mevcut Kiralamayı Tamamlama
            writeMenuItemRed("0. Ana Menüye Dön");

            int secim = intDegerAl("Seçiminizi yapın: ", 0, 2);

            switch (secim) {
                case 1:
                    aracKirala();
                    enteraBas();
                    break;
                case 2:
                    aracTeslimAl();
                    enteraBas();
                    break;
                case 0:
                    devamEt = false;
                    printYellow("Kiralama İşlemleri Menüsünden Çıkılıyor...");
                    break;
                default:
                    printRed("Geçersiz seçim! Lütfen belirtilen aralıkta bir sayı girin.");
                    enteraBas();
                    break;
            }
        }
    }

    /**
     * Yeni bir araç kiralama işlemi başlatır.
     */
    private void aracKirala() {
        baslikYazdir("Yeni Araç Kiralama");

        // 1. Kişi Seçimi
        printBlue("Kiralayacak kişiyi seçin:");
        List<Kisi> kisiler = kisiService.findAll();
        if (kisiler.isEmpty()) {
            printRed("Kayıtlı kişi bulunmamaktadır. Lütfen önce bir kişi ekleyin.");
            return;
        }
        kisiler.stream()
                .sorted(Comparator.comparing(Kisi::getId))
                .forEach(kisi -> System.out.println(ANSI_CYAN + "ID: " + kisi.getId() + " - " + kisi.getAd() + " " + kisi.getSoyad() + ANSI_RESET));

        Long kisiId = (long) intDegerAl("Kiralayacak kişinin ID'sini girin: ");
        Optional<Kisi> optionalKisi = kisiService.findById(kisiId);
        if (optionalKisi.isEmpty()) {
            printRed("Belirtilen ID'de kişi bulunamadı.");
            return;
        }

        // 2. Araç Seçimi (Sadece Müsait Araçlar)
        printBlue("Kiralanacak aracı seçin (Sadece Müsait Olanlar):");
        List<Arac> musaitAraclar = aracService.findByAracDurum(AracDurum.MUSAIT);
        if (musaitAraclar.isEmpty()) {
            printRed("Şu anda kiralanabilir müsait araç bulunmamaktadır.");
            return;
        }
        musaitAraclar.stream()
                .sorted(Comparator.comparing(Arac::getId))
                .forEach(arac -> System.out.println(ANSI_CYAN + "ID: " + arac.getId() + " - " + arac.getMarka() + " " + arac.getModel() + " (" + arac.getPlaka() + ") - Günlük Fiyat: " + arac.getGunlukFiyat() + ANSI_RESET));

        Long aracId = (long) intDegerAl("Kiralanacak aracın ID'sini girin: ");
        Optional<Arac> optionalArac = aracService.findById(aracId);
        if (optionalArac.isEmpty() || optionalArac.get().getAracDurum() != AracDurum.MUSAIT) {
            printRed("Belirtilen ID'de araç bulunamadı veya araç kiralamaya müsait değil.");
            return;
        }

        // 3. Tarih Seçimi
        LocalDate kiralamaTarihi;
        while (true) {
            kiralamaTarihi = localDateDegerAl("Kiralama Başlangıç Tarihi: ");
            if (kiralamaTarihi.isBefore(LocalDate.now())) {
                printRed("Kiralama başlangıç tarihi geçmiş bir tarih olamaz.");
            } else {
                break;
            }
        }

        // Kiralama nesnesini oluştur
        Kiralama yeniKiralama = Kiralama.builder()
                .kisiId(kisiId)
                .aracId(aracId)
                .kiralamaTarihi(kiralamaTarihi)
                .build(); // Teslim tarihi ve toplam tutar başlangıçta null olacak

        try {
            kiralamaService.saveKiralama(yeniKiralama);
            printGreen("Kiralama başarıyla oluşturuldu.");
        } catch (IllegalArgumentException e) {
            printRed("Kiralama oluşturulurken hata: " + e.getMessage());
        } catch (Exception e) {
            printRed("Beklenmedik bir hata oluştu: " + e.getMessage());
        }
    }

    /**
     * Kiralanmış bir aracı teslim alır ve kiralama işlemini tamamlar.
     */
    @Transactional
    private void aracTeslimAl() {
        baslikYazdir("Araç Teslim Alma");

        // Teslim alınabilecek kiralamaları listele (teslimTarihi null olanlar)
        List<Kiralama> devamEdenKiralamalar = kiralamaService.findDevamEdenKiralamalar();
        if (devamEdenKiralamalar.isEmpty()) {
            printYellow("Teslim alınabilecek aktif (devam eden) kiralama bulunmamaktadır.");
            return;
        }

        printBlue("Teslim Alınacak Kiralamalar:");
        devamEdenKiralamalar.stream()
                .sorted(Comparator.comparing(Kiralama::getId))
                .forEach(kiralama -> {
                    Optional<Kisi> kisi = kisiService.findById(kiralama.getKisiId());
                    Optional<Arac> arac = aracService.findById(kiralama.getAracId());
                    System.out.println(ANSI_CYAN + "ID: " + kiralama.getId() +
                            " | Kişi: " + kisi.map(k -> k.getAd() + " " + k.getSoyad()).orElse("Bilinmeyen Kişi") +
                            " | Araç: " + arac.map(a -> a.getMarka() + " " + a.getModel() + " (" + a.getPlaka() + ")").orElse("Bilinmeyen Araç") +
                            " | Kiralama Tarihi: " + kiralama.getKiralamaTarihi() + ANSI_RESET);
                });

        Long kiralamaId = (long) intDegerAl("Teslim alınacak kiralamanın ID'sini girin: ");
        Optional<Kiralama> optionalKiralama = kiralamaService.findById(kiralamaId);

        // Kiralama bulundu mu ve gerçekten teslim edilmemiş bir kiralama mı kontrolü
        if (optionalKiralama.isEmpty() || optionalKiralama.get().getTeslimTarihi() != null) {
            printRed("Belirtilen ID'de devam eden bir kiralama bulunamadı veya kiralama zaten teslim alınmış.");
            return;
        }

        Kiralama kiralama = optionalKiralama.get();

        //teslim tarihi normalde bugunün tarihi ama deneyebilmemiz için kullanıcıdan aldım geçmiş tarihli değil kiralamalar.
        LocalDate teslimTarihi;
        while (true) {
            teslimTarihi = localDateDegerAl("Teslim Tarihi: ");
            if (teslimTarihi.isBefore(kiralama.getKiralamaTarihi())) {
                printRed("Teslim tarihi, kiralama tarihinden önce olamaz.");
            }
            else {
                break;
            }
        }
        kiralama.setTeslimTarihi(teslimTarihi);

        try {
            Kiralama guncellenmisKiralama = kiralamaService.updateKiralama(kiralama);
            printGreen("Araç başarıyla teslim alındı ve kiralama tamamlandı.");
            printGreen("Toplam Tutar: " + String.format("%.2f", guncellenmisKiralama.getToplamTutar()) + " TL");
        } catch (IllegalArgumentException e) {
            printRed("Araç teslim alınırken hata: " + e.getMessage());
        } catch (Exception e) {
            printRed("Beklenmedik bir hata oluştu: " + e.getMessage());
        }
    }
}