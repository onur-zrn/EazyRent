package com.eazyrent.utility;

import com.eazyrent.entity.Arac;
import com.eazyrent.entity.Kisi;
import com.eazyrent.entity.Kiralama;
import com.eazyrent.enums.AracDurum;
import com.eazyrent.service.AracService;
import com.eazyrent.service.KiralamaService; // KiralamaService'i henüz oluşturmadık, placeholder olarak ekliyorum
import com.eazyrent.service.KisiService;     // KisiService'i henüz oluşturmadık, placeholder olarak ekliyorum

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class DataInitializer {

    private final AracService aracService;
    private final KisiService kisiService;
    private final KiralamaService kiralamaService;

    // Constructor: Gerekli servisleri dışarıdan alıyoruz
    public DataInitializer(AracService aracService, KisiService kisiService, KiralamaService kiralamaService) {
        this.aracService = aracService;
        this.kisiService = kisiService;
        this.kiralamaService = kiralamaService;
    }

    /**
     * Uygulama başlangıcında test verilerini veritabanına ekler.
     * Sadece ilgili servisler boşsa ekleme yapar.
     */
    public void initData() {
        Util.printCyan("Test verileri kontrol ediliyor...");

        // Araç test verileri
        if (aracService.findAll().isEmpty()) {
            Util.printCyan("Araç test verileri oluşturuluyor...");
            aracService.save(Arac.builder().marka("Ford").model("Focus").plaka("34 ABC 01").gunlukFiyat(300.0).aracDurum(AracDurum.MUSAIT).build());
            aracService.save(Arac.builder().marka("Renault").model("Clio").plaka("06 XYZ 02").gunlukFiyat(250.0).aracDurum(AracDurum.KIRADA).build()); // Ali - Devam eden
            aracService.save(Arac.builder().marka("Opel").model("Astra").plaka("42 DEF 03").gunlukFiyat(350.0).aracDurum(AracDurum.MUSAIT).build());
            aracService.save(Arac.builder().marka("Fiat").model("Egea").plaka("01 GHI 04").gunlukFiyat(280.0).aracDurum(AracDurum.BAKIMDA).build());
            // Yeni araçlar
            aracService.save(Arac.builder().marka("Mercedes").model("C200").plaka("34 MBM 05").gunlukFiyat(600.0).aracDurum(AracDurum.MUSAIT).build()); // Ali - Tamamlanmış 1
            aracService.save(Arac.builder().marka("BMW").model("320i").plaka("06 BMW 06").gunlukFiyat(550.0).aracDurum(AracDurum.MUSAIT).build());
            aracService.save(Arac.builder().marka("Audi").model("A4").plaka("42 AUD 07").gunlukFiyat(450.0).aracDurum(AracDurum.MUSAIT).build()); // Ali - Tamamlanmış 2
            aracService.save(Arac.builder().marka("Volkswagen").model("Passat").plaka("01 VWP 08").gunlukFiyat(400.0).aracDurum(AracDurum.BAKIMDA).build());
            aracService.save(Arac.builder().marka("Peugeot").model("308").plaka("77 PGT 09").gunlukFiyat(320.0).aracDurum(AracDurum.MUSAIT).build()); // Ali - Gelecek
            Util.printGreen("Test araçları eklendi.");
        } else {
            Util.printYellow("Araç verileri zaten mevcut, test verisi eklenmedi.");
        }

        // Kişi test verileri
        if (kisiService.findAll().isEmpty()) {
            Util.printCyan("Kişi test verileri oluşturuluyor...");
            kisiService.save(Kisi.builder().ad("Ali").soyad("Demir").tcKimlikNo("11111111111").telefon("5321112233").email("ali@example.com").build());
            kisiService.save(Kisi.builder().ad("Ayşe").soyad("Yılmaz").tcKimlikNo("22222222222").telefon("5432223344").email("ayse@example.com").build());
            kisiService.save(Kisi.builder().ad("Mehmet").soyad("Can").tcKimlikNo("33333333333").telefon("5053334455").email("mehmet@example.com").build());
            // Yeni kişiler
            kisiService.save(Kisi.builder().ad("Zeynep").soyad("Kara").tcKimlikNo("44444444444").telefon("5544445566").email("zeynep@example.com").build());
            kisiService.save(Kisi.builder().ad("Mustafa").soyad("Gür").tcKimlikNo("55555555555").telefon("5335556677").email("mustafa@example.com").build());
            kisiService.save(Kisi.builder().ad("Elif").soyad("Tekin").tcKimlikNo("66666666666").telefon("5426667788").email("elif@example.com").build());
            Util.printGreen("Test kişileri eklendi.");
        } else {
            Util.printYellow("Kişi verileri zaten mevcut, test verisi eklenmedi.");
        }

        // Kiralama test verileri
        if (kiralamaService.findAll().isEmpty() && !aracService.findAll().isEmpty() && !kisiService.findAll().isEmpty()) {
            Util.printCyan("Kiralama test verileri oluşturuluyor...");

            // Gerekli ID'leri alalım
            Long aliId = kisiService.findByTcKimlik("11111111111").map(Kisi::getId).orElse(null);
            Long ayseId = kisiService.findByTcKimlik("22222222222").map(Kisi::getId).orElse(null);
            Long mehmetId = kisiService.findByTcKimlik("33333333333").map(Kisi::getId).orElse(null);
            Long zeynepId = kisiService.findByTcKimlik("44444444444").map(Kisi::getId).orElse(null);

            Long clioId = aracService.findByPlaka("06 XYZ 02").map(Arac::getId).orElse(null); // KIRADA (Ali için)
            Long focusId = aracService.findByPlaka("34 ABC 01").map(Arac::getId).orElse(null); // MUSAIT (Mehmet için tamamlanmış)
            Long astraId = aracService.findByPlaka("42 DEF 03").map(Arac::getId).orElse(null); // MUSAIT (Zeynep için tamamlanmış)
            Long bmwId = aracService.findByPlaka("06 BMW 06").map(Arac::getId).orElse(null); // KIRADA (Ayşe için)
            Long audiId = aracService.findByPlaka("42 AUD 07").map(Arac::getId).orElse(null); // MUSAIT (Ali için tamamlanmış 2)
            Long peugeotId = aracService.findByPlaka("77 PGT 09").map(Arac::getId).orElse(null); // KIRADA (Ali için gelecek)
            Long mercedesId = aracService.findByPlaka("34 MBM 05").map(Arac::getId).orElse(null); // MUSAIT (Ali için tamamlanmış 1)


            if (aliId != null && ayseId != null && mehmetId != null && zeynepId != null &&
                    clioId != null && focusId != null && astraId != null && bmwId != null && audiId != null && peugeotId != null && mercedesId != null) {

                // --- Ali'nin Kiralamaları (4 adet, 2'si teslim edilmiş) ---

                // Ali'nin 1. Kiralaması: Tamamlanmış (Mercedes C200)
                LocalDate aliKiralamaTarihi1 = LocalDate.now().minusDays(30);
                LocalDate aliTeslimTarihi1 = LocalDate.now().minusDays(25); // 5 gün kiralama
                long aliGunFarki1 = ChronoUnit.DAYS.between(aliKiralamaTarihi1, aliTeslimTarihi1);
                if (aliGunFarki1 == 0) aliGunFarki1 = 1;
                double aliToplamTutar1 = aracService.findById(mercedesId).get().getGunlukFiyat() * aliGunFarki1;

                kiralamaService.save(Kiralama.builder()
                        .aracId(mercedesId)
                        .kisiId(aliId)
                        .kiralamaTarihi(aliKiralamaTarihi1)
                        .teslimTarihi(aliTeslimTarihi1)
                        .toplamTutar(aliToplamTutar1)
                        .build());
                // Mercedes C200'ün durumunu MUSAIT olarak güncelleyelim (çünkü kiralama tamamlandı)
                aracService.findById(mercedesId).ifPresent(a -> {
                    a.setAracDurum(AracDurum.MUSAIT);
                    aracService.update(a);
                });
                Util.printGreen("Test kiralaması (Mercedes C200 - Ali - Tamamlanmış 1) eklendi.");

                // Ali'nin 2. Kiralaması: Tamamlanmış (Audi A4)
                LocalDate aliKiralamaTarihi2 = LocalDate.now().minusDays(20);
                LocalDate aliTeslimTarihi2 = LocalDate.now().minusDays(18); // 2 gün kiralama
                long aliGunFarki2 = ChronoUnit.DAYS.between(aliKiralamaTarihi2, aliTeslimTarihi2);
                if (aliGunFarki2 == 0) aliGunFarki2 = 1;
                double aliToplamTutar2 = aracService.findById(audiId).get().getGunlukFiyat() * aliGunFarki2;

                kiralamaService.save(Kiralama.builder()
                        .aracId(audiId)
                        .kisiId(aliId)
                        .kiralamaTarihi(aliKiralamaTarihi2)
                        .teslimTarihi(aliTeslimTarihi2)
                        .toplamTutar(aliToplamTutar2)
                        .build());
                // Audi A4'ün durumunu MUSAIT olarak güncelleyelim (çünkü kiralama tamamlandı)
                aracService.findById(audiId).ifPresent(a -> {
                    a.setAracDurum(AracDurum.MUSAIT);
                    aracService.update(a);
                });
                Util.printGreen("Test kiralaması (Audi A4 - Ali - Tamamlanmış 2) eklendi.");

                // Ali'nin 3. Kiralaması: Devam eden (Renault Clio)
                // KiralamaService.saveKiralama() metodu geçmiş tarihleri kabul etmediği için repository'den kaydediyoruz.
                // Clio'nun durumu KIRADA olarak init data'da belirlendiği için burada değiştirmiyoruz.
                kiralamaService.save(Kiralama.builder()
                        .aracId(clioId)
                        .kisiId(aliId)
                        .kiralamaTarihi(LocalDate.now().minusDays(5)) // Geçmiş tarih
                        .teslimTarihi(null) // Henüz teslim edilmedi
                        .toplamTutar(null) // Henüz hesaplanmadı
                        .build());
                Util.printGreen("Test kiralaması (Renault Clio - Ali - Devam Eden) eklendi.");
                // Clio'nun AracDurum'u zaten KIRADA olarak ayarlı olduğu için burada değiştirmeye gerek yok.

                // Ali'nin 4. Kiralaması: Gelecek (Peugeot 308) - KiralamaService üzerinden kaydedelim
                // AracDurum'u MUSAIT olmalı, KiralamaService bunu KIRADA yapacak (kiralama tarihi geldiğinde değil, kayıt edildiğinde).
                try {
                    kiralamaService.saveKiralama(Kiralama.builder()
                            .aracId(peugeotId)
                            .kisiId(aliId)
                            .kiralamaTarihi(LocalDate.now().plusDays(2)) // Gelecek tarih
                            .build()); // Teslim tarihi ve toplam tutar servis tarafından null set edilecek
                    Util.printGreen("Test kiralaması (Peugeot 308 - Ali - Gelecek) eklendi.");
                } catch (IllegalArgumentException e) {
                    Util.printRed("Peugeot kiralaması eklenirken hata: " + e.getMessage());
                }

                // --- Diğer Kiralamalar ---

                // 1. Yeni aktif kiralama (BMW - Ayşe) - KiralamaService üzerinden kaydedelim
                // AracDurum'u MUSAIT olmalı, KiralamaService bunu KIRADA yapacak.
                try {
                    kiralamaService.saveKiralama(Kiralama.builder()
                            .aracId(bmwId)
                            .kisiId(ayseId)
                            .kiralamaTarihi(LocalDate.now().plusDays(5)) // Gelecek tarih
                            .build());
                    Util.printGreen("Test kiralaması (BMW - Ayşe) eklendi.");
                } catch (IllegalArgumentException e) {
                    Util.printRed("BMW kiralaması eklenirken hata: " + e.getMessage());
                }

                // 2. Tamamlanmış kiralama (Focus - Mehmet)
                LocalDate focusKiralamaTarihi = LocalDate.now().minusDays(20);
                LocalDate focusTeslimTarihi = LocalDate.now().minusDays(15); // 5 gün kiralama
                long focusGunFarki = ChronoUnit.DAYS.between(focusKiralamaTarihi, focusTeslimTarihi);
                if (focusGunFarki == 0) focusGunFarki = 1;
                double focusToplamTutar = aracService.findById(focusId).get().getGunlukFiyat() * focusGunFarki;

                kiralamaService.save(Kiralama.builder()
                        .aracId(focusId)
                        .kisiId(mehmetId)
                        .kiralamaTarihi(focusKiralamaTarihi)
                        .teslimTarihi(focusTeslimTarihi)
                        .toplamTutar(focusToplamTutar)
                        .build());
                // Focus'un durumu MUSAIT olarak güncelleyelim
                aracService.findById(focusId).ifPresent(a -> {
                    a.setAracDurum(AracDurum.MUSAIT);
                    aracService.update(a);
                });
                Util.printGreen("Test kiralaması (Focus - Mehmet) eklendi.");

                // 3. Tamamlanmış kiralama (Astra - Zeynep)
                LocalDate astraKiralamaTarihi = LocalDate.now().minusMonths(1).plusDays(10);
                LocalDate astraTeslimTarihi = LocalDate.now().minusMonths(1).plusDays(20); // 10 gün kiralama
                long astraGunFarki = ChronoUnit.DAYS.between(astraKiralamaTarihi, astraTeslimTarihi);
                if (astraGunFarki == 0) astraGunFarki = 1;
                double astraToplamTutar = aracService.findById(astraId).get().getGunlukFiyat() * astraGunFarki;

                kiralamaService.save(Kiralama.builder()
                        .aracId(astraId)
                        .kisiId(zeynepId)
                        .kiralamaTarihi(astraKiralamaTarihi)
                        .teslimTarihi(astraTeslimTarihi)
                        .toplamTutar(astraToplamTutar)
                        .build());
                // Astra'nın durumu MUSAIT olarak güncelleyelim
                aracService.findById(astraId).ifPresent(a -> {
                    a.setAracDurum(AracDurum.MUSAIT);
                    aracService.update(a);
                });
                Util.printGreen("Test kiralaması (Astra - Zeynep) eklendi.");

            } else {
                Util.printRed("Kiralama için yeterli araç veya kişi verisi bulunamadı. ID'ler null geldi.");
            }
        } else if (kiralamaService.findAll().isEmpty()) {
            Util.printYellow("Kiralama verileri zaten mevcut veya araç/kişi verisi eksik, test verisi eklenmedi.");
        }
    }
}