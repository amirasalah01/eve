package com.booknest.service;

import com.booknest.model.Reservation;
import com.booknest.model.Reservation.StatutReservation;
import com.booknest.model.Salle;
import com.booknest.model.User;
import com.booknest.repository.ReservationRepository;
import com.booknest.repository.SalleRepository;
import com.booknest.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final SalleRepository salleRepository;
    private final UserRepository userRepository;

    @Transactional
    public Reservation reserve(Long salleId, LocalDateTime debut, LocalDateTime fin, String userEmail) {
        if (!fin.isAfter(debut)) {
            throw new IllegalArgumentException("La date de fin doit être après la date de début.");
        }
        Salle salle = salleRepository.findById(salleId)
                .orElseThrow(() -> new IllegalArgumentException("Salle introuvable."));
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UsernameNotFoundException("Utilisateur introuvable."));

        long conflicts = reservationRepository.countConflicts(salle, debut, fin);
        if (conflicts > 0) {
            throw new IllegalStateException("La salle est déjà réservée sur ce créneau.");
        }

        Reservation reservation = Reservation.builder()
                .salle(salle)
                .user(user)
                .dateDebut(debut)
                .dateFin(fin)
                .statut(StatutReservation.EN_ATTENTE)
                .build();
        return reservationRepository.save(reservation);
    }

    @Transactional
    public void cancel(Long id, String userEmail) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Réservation introuvable."));
        if (!reservation.getUser().getEmail().equals(userEmail)) {
            throw new SecurityException("Action non autorisée.");
        }
        reservation.setStatut(StatutReservation.ANNULEE);
        reservationRepository.save(reservation);
    }

    public List<Reservation> findByUser(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Utilisateur introuvable."));
        return reservationRepository.findByUser(user);
    }

    public Page<Reservation> findAll(Pageable pageable) {
        return reservationRepository.findAllByOrderByDateDebutDesc(pageable);
    }

    @Transactional
    public void updateStatut(Long id, StatutReservation statut) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Réservation introuvable."));
        reservation.setStatut(statut);
        reservationRepository.save(reservation);
    }
}
