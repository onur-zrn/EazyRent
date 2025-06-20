package com.eazyrent.repository;

import com.eazyrent.entity.Kiralama;
import com.eazyrent.utility.JPAUtility;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

import java.time.LocalDate;
import java.util.List;

public class KiralamaRepository extends RepositoryManager<Kiralama, Long> {

    private EntityManager entityManager;

    public KiralamaRepository() {
        super(Kiralama.class);
        this.entityManager = JPAUtility.getEntityManager();
    }

    /**
     * Belirli bir kişinin tüm kiralamalarını listeler.
     * @param kisiId Kiralayan kişinin ID'si.
     * @return Belirtilen kişinin kiralamalarının listesi.
     */
    public List<Kiralama> findByKisiId(Long kisiId) {
        String jpql = "SELECT k FROM Kiralama k WHERE k.kisiId = :kisiId ORDER BY k.kiralamaTarihi DESC";
        TypedQuery<Kiralama> query = entityManager.createQuery(jpql, Kiralama.class); // getEntityManager() kullanıldı
        query.setParameter("kisiId", kisiId);
        List<Kiralama> resultList = query.getResultList();

        // Her bir Kiralama objesini veritabanından yenile
        for (Kiralama kiralama : resultList) {
            try {
                if (entityManager.contains(kiralama)) {
                    entityManager.refresh(kiralama);
                }
            } catch (Exception e) {
                System.err.println("Kiralama ID " + kiralama.getId() + " yenilenirken hata: " + e.getMessage());
            }
        }
        return resultList;
    }// güncel entity manager kullandım hep aynı em geliyor ama refreshlemezsem veritabanından eski veriler geliyor.
//    public List<Kiralama> findByKisiId(Long kisiId) {
//        String jpql = "SELECT k FROM Kiralama k WHERE k.kisiId = :kisiId ORDER BY k.kiralamaTarihi DESC";
//        TypedQuery<Kiralama> query = entityManager.createQuery(jpql, Kiralama.class);
//        query.setParameter("kisiId", kisiId);
//        return query.getResultList();
//    }


    /**
     * Tüm kiralamaları ID'ye göre artan sırada listeler.
     * @return ID'ye göre sıralanmış kiralamaların listesi.
     */
    public List<Kiralama> findAllOrderById() {
        String jpql = "SELECT k FROM Kiralama k ORDER BY k.id ASC";
        TypedQuery<Kiralama> query = entityManager.createQuery(jpql, Kiralama.class);
        return query.getResultList();
    }

    /**
     * Belirli bir kişinin belirtilen tarihler arasında (teslim tarihi null veya teslim tarihi bitiş tarihinden sonra olan)
     * başka bir kiralaması olup olmadığını kontrol eder.
     * Çakışan tarihler için kontrol: (start1 <= end2 AND end1 >= start2)
     * Burada end1, kiralamaBitisTarihi (teslimTarihi) veya mevcut kiralama devam ediyorsa gelecekte bir tarih olarak kabul edilir.
     *
     * @param kisiId Kiralayan kişinin ID'si.
     * @param baslangicTarihi Kontrol edilecek kiralama başlangıç tarihi.
     * @param bitisTarihi Kontrol edilecek kiralama bitiş tarihi.
     * @return Eğer kişinin belirtilen tarihler arasında aktif kiralaması varsa true, yoksa false.
     */
    public boolean existsAktifKiralamaForKisiInDateRange(Long kisiId, LocalDate baslangicTarihi, LocalDate bitisTarihi) {
        String jpql = "SELECT COUNT(k) FROM Kiralama k WHERE k.kisiId = :kisiId " +
                "AND (k.teslimTarihi IS NULL OR k.teslimTarihi >= :baslangicTarihi) " + // Kiralama hala aktifse veya bitiş tarihi çakışıyorsa
                "AND k.kiralamaTarihi <= :bitisTarihi"; // Başlangıç tarihi çakışıyorsa
        TypedQuery<Long> query = entityManager.createQuery(jpql, Long.class);
        query.setParameter("kisiId", kisiId);
        query.setParameter("baslangicTarihi", baslangicTarihi);
        query.setParameter("bitisTarihi", bitisTarihi);
        return query.getSingleResult() > 0;
    }

    /**
     * Belirli bir aracın belirtilen tarihler arasında (teslim tarihi null veya teslim tarihi bitiş tarihinden sonra olan)
     * başka bir kiralaması olup olmadığını kontrol eder.
     * Çakışan tarihler için kontrol: (start1 <= end2 AND end1 >= start2)
     *
     * @param aracId Kiralanan aracın ID'si.
     * @param baslangicTarihi Kontrol edilecek kiralama başlangıç tarihi.
     * @param bitisTarihi Kontrol edilecek kiralama bitiş tarihi.
     * @return Eğer aracın belirtilen tarihler arasında aktif kiralaması varsa true, yoksa false.
     */
    public boolean existsAktifKiralamaForAracInDateRange(Long aracId, LocalDate baslangicTarihi, LocalDate bitisTarihi) {
        String jpql = "SELECT COUNT(k) FROM Kiralama k WHERE k.aracId = :aracId " +
                "AND (k.teslimTarihi IS NULL OR k.teslimTarihi >= :baslangicTarihi) " + // Kiralama hala aktifse veya bitiş tarihi çakışıyorsa
                "AND k.kiralamaTarihi <= :bitisTarihi"; // Başlangıç tarihi çakışıyorsa
        TypedQuery<Long> query = entityManager.createQuery(jpql, Long.class);
        query.setParameter("aracId", aracId);
        query.setParameter("baslangicTarihi", baslangicTarihi);
        query.setParameter("bitisTarihi", bitisTarihi);
        return query.getSingleResult() > 0;
    }

    /**
     * Henüz teslim edilmemiş (teslimTarihi null olan) tüm kiralamaları listeler.
     * Bu kiralamalar aktif olarak devam etmektedir.
     * @return Teslim edilmemiş kiralamaların listesi.
     */
    public List<Kiralama> findDevamEdenKiralamalar() {
        String jpql = "SELECT k FROM Kiralama k WHERE k.teslimTarihi IS NULL ORDER BY k.kiralamaTarihi ASC";
        TypedQuery<Kiralama> query = entityManager.createQuery(jpql, Kiralama.class);
        return query.getResultList();
    }

    /**
     * Belirli bir kişinin henüz teslim edilmemiş (teslimTarihi null olan) aktif bir kiralaması olup olmadığını kontrol eder.
     * @param kisiId Kiralayan kişinin ID'si.
     * @return Eğer kişinin devam eden kiralaması varsa true, yoksa false.
     */
    public boolean existsAnyOngoingRentalForKisi(Long kisiId) {
        String jpql = "SELECT COUNT(k) FROM Kiralama k WHERE k.kisiId = :kisiId AND k.teslimTarihi IS NULL";
        TypedQuery<Long> query = entityManager.createQuery(jpql, Long.class);
        query.setParameter("kisiId", kisiId);
        return query.getSingleResult() > 0;
    }
}