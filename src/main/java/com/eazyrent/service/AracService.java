package com.eazyrent.service;

import com.eazyrent.entity.Arac;
import com.eazyrent.enums.AracDurum;
import com.eazyrent.repository.AracRepository;

import java.util.List;
import java.util.Optional;

public class AracService extends ServiceManager<Arac, Long> {
    private final AracRepository aracRepository;
    public AracService(AracRepository aracRepository) {
        super(aracRepository);
        this.aracRepository = aracRepository;
    }

    public Optional<Arac> findByPlaka(String plaka) {
        return aracRepository.findByPlaka(plaka);
    }

    // Araç durumuna göre listeleme
    public List<Arac> findByAracDurum(AracDurum durum) {
        return aracRepository.findByAracDurum(durum);
    }

    public List<Arac> findByMarkaAndModel(String marka, String model) {
        return aracRepository.findByMarkaAndModel(marka, model);
    }
}