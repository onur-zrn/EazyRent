package com.eazyrent.repository;

import com.eazyrent.entity.Arac;
import com.eazyrent.enums.AracDurum;
import com.eazyrent.utility.JPAUtility;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;

import java.util.List;
import java.util.Optional;

public class AracRepository extends RepositoryManager<Arac, Long> {

    private EntityManager entityManager;

    public AracRepository() {
        super(Arac.class);
        entityManager = JPAUtility.getEntityManager();
    }

    /**
     * Plaka bilgisine göre bir aracı bulur.
     *
     * @param plaka Aranacak plaka bilgisi
     * @return Eğer bulunursa Aracı Optional içinde, bulunamazsa boş Optional döner.
     */
    public Optional<Arac> findByPlaka(String plaka) {
        try {
            return Optional.ofNullable(
                    entityManager
                            .createQuery("SELECT a FROM Arac a WHERE a.plaka = :plaka ORDER BY a.id", Arac.class)
                            .setParameter("plaka", plaka)
                            .getSingleResult()
            );
        } catch (NoResultException e) { //exceptions yapılabilir
            return Optional.empty();
        }
    }

    /**
     * Belirli bir AracDurum'a sahip araçları listeler.
     *
     * @param durum Aranacak araç durumu (MUSAIT, KIRADA, BAKIMDA)
     * @return Belirtilen duruma sahip araçların listesi.
     */
    public List<Arac> findByAracDurum(AracDurum durum) {
        return entityManager
                .createQuery("SELECT a FROM Arac a WHERE a.aracDurum = :durum ORDER BY a.id", Arac.class)
                .setParameter("durum", durum)
                .getResultList();
    }

    /**
     * Marka ve modele göre araçları listeler.
     * Named Query kullanılmıştır.
     * @param marka Aranacak marka (kısmi arama destekler).
     * @param model Aranacak model (kısmi arama destekler).
     * @return Belirtilen marka ve modele sahip araçların listesi.
     */
    public List<Arac> findByMarkaAndModel(String marka, String model) {
        TypedQuery<Arac> query = entityManager.createNamedQuery("Arac.findByMarkaAndModel", Arac.class);
        query.setParameter("marka", "%" + marka + "%");
        query.setParameter("model", "%" + model + "%");
        return query.getResultList();
    }
}
