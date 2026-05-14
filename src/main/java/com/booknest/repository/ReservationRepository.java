package com.booknest.repository;

import com.booknest.model.Reservation;
import com.booknest.model.Salle;
import com.booknest.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    List<Reservation> findByUser(User user);

    List<Reservation> findBySalle(Salle salle);

    Page<Reservation> findAllByOrderByDateDebutDesc(Pageable pageable);

    @Query("SELECT COUNT(r) FROM Reservation r WHERE r.salle = :salle " +
           "AND r.statut != 'ANNULEE' " +
           "AND r.dateDebut < :dateFin " +
           "AND r.dateFin > :dateDebut")
    long countConflicts(@Param("salle") Salle salle,
                        @Param("dateDebut") LocalDateTime dateDebut,
                        @Param("dateFin") LocalDateTime dateFin);

    @Query("SELECT COUNT(r) FROM Reservation r WHERE r.statut = 'EN_ATTENTE'")
    long countEnAttente();

    @Query("SELECT COUNT(r) FROM Reservation r WHERE r.statut = 'CONFIRMEE'")
    long countConfirmee();
}
