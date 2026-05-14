package com.booknest.service;

import com.booknest.model.Avis;
import com.booknest.model.Salle;
import com.booknest.model.User;
import com.booknest.repository.AvisRepository;
import com.booknest.repository.SalleRepository;
import com.booknest.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AvisService {

    private final AvisRepository avisRepository;
    private final SalleRepository salleRepository;
    private final UserRepository userRepository;

    public List<Avis> findAll() {
        return avisRepository.findAll();
    }

    public Optional<Avis> findById(Long id) {
        return avisRepository.findById(id);
    }

    public List<Avis> findBySalle(Long salleId) {
        Salle salle = salleRepository.findById(salleId)
                .orElseThrow(() -> new IllegalArgumentException("Salle introuvable."));
        return avisRepository.findBySalle(salle);
    }

    @Transactional
    public Avis create(Long salleId, Long userId, int note, String commentaire) {
        Salle salle = salleRepository.findById(salleId)
                .orElseThrow(() -> new IllegalArgumentException("Salle introuvable."));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur introuvable."));
        Avis avis = Avis.builder()
                .salle(salle)
                .user(user)
                .note(note)
                .commentaire(commentaire)
                .dateAvis(LocalDate.now())
                .build();
        return avisRepository.save(avis);
    }

    @Transactional
    public Optional<Avis> update(Long id, int note, String commentaire) {
        return avisRepository.findById(id).map(existing -> {
            existing.setNote(note);
            existing.setCommentaire(commentaire);
            return avisRepository.save(existing);
        });
    }

    @Transactional
    public void deleteById(Long id) {
        avisRepository.deleteById(id);
    }
}
