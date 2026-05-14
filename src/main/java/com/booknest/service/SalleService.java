package com.booknest.service;

import com.booknest.model.Salle;
import com.booknest.repository.SalleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SalleService {

    private final SalleRepository salleRepository;

    public Page<Salle> findAll(Pageable pageable) {
        return salleRepository.findAll(pageable);
    }

    public Optional<Salle> findById(Long id) {
        return salleRepository.findById(id);
    }

    @Transactional
    public Salle save(Salle salle) {
        return salleRepository.save(salle);
    }

    @Transactional
    public void delete(Long id) {
        salleRepository.deleteById(id);
    }

    public Page<Salle> search(String nom, String localisation, Integer minCapacite, Pageable pageable) {
        String nomParam = (nom != null && !nom.isBlank()) ? nom.trim() : null;
        String locParam = (localisation != null && !localisation.isBlank()) ? localisation.trim() : null;
        return salleRepository.search(nomParam, locParam, minCapacite, pageable);
    }
}
