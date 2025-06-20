package com.eazyrent.service;

import com.eazyrent.entity.Kisi;
import com.eazyrent.repository.KisiRepository;

import java.util.List;
import java.util.Optional;

public class KisiService extends ServiceManager<Kisi, Long> {
    private final KisiRepository kisiRepository;
    public KisiService(KisiRepository kisiRepository) {
        super(kisiRepository);
        this.kisiRepository = kisiRepository;
    }

    public Optional<Kisi> findByTcKimlik(String tcKimlik) {
        return kisiRepository.findByTcKimlik(tcKimlik);
    }

    /**
     * Ad ve Soyad bilgilerine göre kişileri bulur.
     * @param ad Aranacak ad bilgisi
     * @param soyad Aranacak soyad bilgisi
     * @return Belirtilen ad ve soyada sahip kişilerin listesi.
     */
    public List<Kisi> findByAdAndSoyad(String ad, String soyad) {
        return kisiRepository.findByAdAndSoyad(ad, soyad);
    }
}