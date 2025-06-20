package com.eazyrent.repository;

import com.eazyrent.entity.Kisi;
import com.eazyrent.utility.JPAUtility;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;

import java.util.List;
import java.util.Optional;

public class KisiRepository extends RepositoryManager<Kisi, Long> {

    private EntityManager entityManager;

    public KisiRepository() {
        super(Kisi.class);
        entityManager = JPAUtility.getEntityManager();
    }

    public Optional<Kisi> findByTcKimlik(String tcKimlik) {
        String jpql = "SELECT k FROM Kisi k WHERE k.tcKimlikNo = :tcKimlik";
        TypedQuery<Kisi> query = entityManager.createQuery(jpql, Kisi.class);
        query.setParameter("tcKimlik", tcKimlik);
        try {
            return Optional.of(query.getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    public List<Kisi> findAllOrderById() {
        String jpql = "SELECT k FROM Kisi k ORDER BY k.id ASC";
        TypedQuery<Kisi> query = entityManager.createQuery(jpql, Kisi.class);
        return query.getResultList();
    }

    /**
     * Ad ve Soyad bilgilerine göre kişileri listeler.
     * JPQL sorgusu kullanılmıştır (LIKE ile kısmi arama destekler).
     * @param ad Aranacak ad bilgisi (kısmi arama destekler)
     * @param soyad Aranacak soyad bilgisi (kısmi arama destekler)
     * @return Belirtilen ad ve soyada sahip kişilerin listesi.
     */
    public List<Kisi> findByAdAndSoyad(String ad, String soyad) {
        String jpql = "SELECT k FROM Kisi k WHERE LOWER(k.ad) LIKE LOWER(:ad) AND LOWER(k.soyad) LIKE LOWER(:soyad)";
        TypedQuery<Kisi> query = entityManager.createQuery(jpql, Kisi.class);
        query.setParameter("ad", "%" + ad + "%");
        query.setParameter("soyad", "%" + soyad + "%");
        return query.getResultList();
    }
}
