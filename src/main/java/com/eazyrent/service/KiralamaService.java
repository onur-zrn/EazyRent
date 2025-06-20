package com.eazyrent.service;

import com.eazyrent.entity.Arac;
import com.eazyrent.entity.Kiralama;
import com.eazyrent.entity.Kisi;
import com.eazyrent.enums.AracDurum;
import com.eazyrent.repository.KiralamaRepository;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit; // Gün farkı hesaplamak için
import java.util.List;
import java.util.Optional;

import static com.eazyrent.utility.Util.printRed;

public class KiralamaService extends ServiceManager<Kiralama, Long> {

    private final KiralamaRepository kiralamaRepository;
    private final AracService aracService; // Araç durumunu güncellemek için
    private final KisiService kisiService; // Kişi bilgilerini kontrol etmek için

    public KiralamaService(KiralamaRepository kiralamaRepository, AracService aracService, KisiService kisiService) {
        super(kiralamaRepository);
        this.kiralamaRepository = kiralamaRepository;
        this.aracService = aracService;
        this.kisiService = kisiService;
    }

    @Override
    public List<Kiralama> findAll() {
        return kiralamaRepository.findAllOrderById();
    }

    /**
     * Yeni bir kiralama oluşturur.
     * Çeşitli iş kuralları kontrollerini yapar ve aracın genel durumunu günceller.
     *
     * @param kiralama Kiralama nesnesi (aracId, kisiId, kiralamaTarihi içermeli).
     * @return Kaydedilen Kiralama nesnesi.
     * @throws IllegalArgumentException Eğer araç veya kişi bulunamazsa, tarihler geçersizse,
     * kişi veya araç belirtilen tarihlerde zaten başka bir kiralamada ise,
     * veya kiralama tarihi/süresi kurallara uymuyorsa.
     */
    public Kiralama saveKiralama(Kiralama kiralama) {
        // Kisi ve Arac entity'lerini ID'leri üzerinden buluyoruz.
        Optional<Arac> aracOptional = aracService.findById(kiralama.getAracId());
        Optional<Kisi> kisiOptional = kisiService.findById(kiralama.getKisiId());

        if (aracOptional.isEmpty()) {
            throw new IllegalArgumentException("Belirtilen araç bulunamadı.");
        }
        if (kisiOptional.isEmpty()) {
            throw new IllegalArgumentException("Belirtilen kişi bulunamadı.");
        }

        Arac arac = aracOptional.get();

        // Araç genel durumu kontrolü: Araç genel olarak müsait değilse kiralanamaz.
        if (arac.getAracDurum() != AracDurum.MUSAIT) {
            throw new IllegalArgumentException("Araç şu anda kiralamaya müsait değil: " + arac.getAracDurum());
        }

        // Kiralama Tarihi Kısıtı: Kiralama başlangıç tarihi geçmiş bir tarih olamaz.
        if (kiralama.getKiralamaTarihi().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Kiralama başlangıç tarihi geçmiş bir tarih olamaz.");
        }

        // Kiralama Tarihi Kısıtı: Kiralama başlangıç tarihi, bugünden en fazla 1 ay sonra olabilir.
        if (kiralama.getKiralamaTarihi().isAfter(LocalDate.now().plusMonths(1))) {
            throw new IllegalArgumentException("Kiralama başlangıç tarihi, bugünden en fazla 1 ay sonrası olabilir.");
        }

        // Teslim tarihi null olabileceği için, eğer varsa kontrol yapalım.
        // Yeni bir kiralama oluşturulurken teslim tarihi başlangıçta null olmalı.
        if (kiralama.getTeslimTarihi() != null) {
            throw new IllegalArgumentException("Yeni kiralama oluşturulurken teslim tarihi boş olmalıdır.");
        }

        // Kişinin aynı tarihlerde başka bir kiralaması olup olmadığı kontrolü.
        // existsAktifKiralamaForKisiInDateRange metodunun son parametresi, bu yeni kiralama kaydının bitiş tarihidir.
        // Yeni kiralama kaydedilirken teslim tarihi null olacağı için, bu kiralama bitiş tarihi olarak,
        // o anki kiralama başlangıç tarihi ve gelecekteki olası bir teslim tarihi arasındaki çakışmayı kontrol etmeliyiz.
        // Basitlik adına, burada sadece kiralama başlangıç tarihi ile başka bir aktif kiralama olup olmadığını kontrol etmek daha mantıklı olabilir.
        // Eğer bir kişi aynı anda sadece bir araç kiralayabilirse bu kontrol önemlidir.
        // Teslim tarihi null olan kiralamalar için, mantıksal olarak "devam eden" bir kiralama olarak kabul edilir.
        if (kiralamaRepository.existsAktifKiralamaForKisiInDateRange(kiralama.getKisiId(), kiralama.getKiralamaTarihi(), kiralama.getTeslimTarihi())) {
            throw new IllegalArgumentException("Bu kişi, belirtilen başlangıç tarihinde zaten aktif bir araç kiralamıştır.");
        }


        // Aracın belirtilen tarihler arasında müsait olup olmadığı kontrolü (tarih çakışması)
        // Burada da yine teslimTarihi null olduğu için, eğer aracın o tarihten sonra devam eden başka bir kiralaması varsa kontrol edilmeli.
        if (kiralamaRepository.existsAktifKiralamaForAracInDateRange(kiralama.getAracId(), kiralama.getKiralamaTarihi(), kiralama.getTeslimTarihi())) {
            throw new IllegalArgumentException("Seçilen araç, belirtilen başlangıç tarihinde zaten kiralanmıştır veya başka bir kiralamada aktif durumdadır.");
        }

        // Yeni kiralama kaydedilirken toplam tutar ve teslim tarihi null olmalıdır.
        kiralama.setToplamTutar(null);
        kiralama.setTeslimTarihi(null);

        Kiralama savedKiralama = kiralamaRepository.save(kiralama);

        // Aracın genel durumunu KIRADA olarak güncelle.
        arac.setAracDurum(AracDurum.KIRADA);
        aracService.update(arac);

        return savedKiralama;
    }

    /**
     * Araç teslim alma
     * Mevcut bir kiralamayı günceller. Özellikle teslim tarihi ve toplam tutar hesaplaması için kullanılır.
     * Kiralamanın tamamlanması durumunda aracın genel durumunu MUSAIT olarak günceller.
     *
     * @param kiralama Güncellenecek Kiralama nesnesi (ID, teslimTarihi ve toplamTutar güncellenebilir).
     * @return Güncellenmiş Kiralama nesnesi.
     * @throws IllegalArgumentException Eğer kiralama bulunamazsa veya geçersiz durum geçişi/tarih varsa,
     * veya kiralama süresi kurallara uymuyorsa.
     */
    public Kiralama updateKiralama(Kiralama kiralama) {
        Optional<Kiralama> existingKiralamaOptional = findById(kiralama.getId());
        if (existingKiralamaOptional.isEmpty()) {
            throw new IllegalArgumentException("Güncellenecek kiralama bulunamadı.");
        }
        Kiralama existingKiralama = existingKiralamaOptional.get();
        Arac arac = aracService.findById(existingKiralama.getAracId())
                .orElseThrow(() -> new IllegalArgumentException("Kiralama ile ilişkili araç bulunamadı."));

        // Teslim tarihi kiralama tarihinden önce olamaz
        if (kiralama.getTeslimTarihi() != null) {
            if (kiralama.getTeslimTarihi().isBefore(existingKiralama.getKiralamaTarihi())) {
                throw new IllegalArgumentException("Teslim tarihi kiralama tarihinden önce olamaz.");
            }
        }


            long gunFarki = ChronoUnit.DAYS.between(existingKiralama.getKiralamaTarihi(), kiralama.getTeslimTarihi());
            if (gunFarki < 0) { // Negatif gün farkı olamaz, zaten üstte kontrol edildi.
                throw new IllegalArgumentException("Hesaplama hatası: Teslim tarihi kiralama tarihinden önce.");
            }
            if (gunFarki == 0) gunFarki = 1; // Aynı gün kiralanıp teslim edilirse 1 gün say

            double toplamFiyat = arac.getGunlukFiyat() * gunFarki;
            kiralama.setToplamTutar(toplamFiyat);

            // Araç teslim edildiğinde genel durumunu MUSAIT yap.
            arac.setAracDurum(AracDurum.MUSAIT);
            aracService.update(arac);

        // Kiralama nesnesinin güncel alanlarını kopyala.
        existingKiralama.setTeslimTarihi(kiralama.getTeslimTarihi());
        existingKiralama.setToplamTutar(kiralama.getToplamTutar());
        // Diğer alanlar (aracId, kisiId, kiralamaTarihi) genellikle güncellenmez.

        return kiralamaRepository.update(existingKiralama);
    }


    /**
     * Bir kiralamayı ID'sine göre siler.
     * Kiralamanın silinmesi durumunda (eğer teslim edilmediyse) aracın durumunu MUSAIT olarak günceller.
     *
     * @param id Silinecek kiralamanın ID'si.
     * @return true eğer silme başarılıysa, false değilse.
     */
    @Override
    public boolean deleteById(Long id) {
        Optional<Kiralama> kiralamaOptional = findById(id);
        if (kiralamaOptional.isPresent()) {
            Kiralama kiralama = kiralamaOptional.get();
            Optional<Arac> aracOptional = aracService.findById(kiralama.getAracId());

            if (aracOptional.isPresent()) {
                Arac arac = aracOptional.get();
                // Eğer kiralama henüz teslim edilmediyse (teslimTarihi null ise) ve siliniyorsa aracı müsait yap.
                if (kiralama.getTeslimTarihi() == null) {
                    arac.setAracDurum(AracDurum.MUSAIT);
                    aracService.update(arac);
                }
            } else {
                printRed("Uyarı: Kiralama ile ilişkili araç bulunamadı (ID: " + kiralama.getAracId() + ").");
            }

            return kiralamaRepository.deleteById(id);
        }
        return false;
    }

    public List<Kiralama> findByKisiId(Long kisiId) {
        return kiralamaRepository.findByKisiId(kisiId);
    }

    public List<Kiralama> findDevamEdenKiralamalar() {
        return kiralamaRepository.findDevamEdenKiralamalar();
    }

    /**
     * Belirli bir kişinin devam eden (teslim tarihi null olan) bir kiralaması olup olmadığını kontrol eder.
     * @param kisiId Kiralayan kişinin ID'si.
     * @return Eğer kişinin devam eden kiralaması varsa true, yoksa false.
     */
    public boolean existsAnyOngoingRentalForKisi(Long kisiId) {
        return kiralamaRepository.existsAnyOngoingRentalForKisi(kisiId);
    }
}