package com.booknest.config;

import com.booknest.model.*;
import com.booknest.model.Reservation.StatutReservation;
import com.booknest.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final SalleRepository salleRepository;
    private final ReservationRepository reservationRepository;
    private final AvisRepository avisRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (userRepository.count() > 0) return;

        // Users
        User admin = userRepository.save(User.builder()
                .nom("Admin BookNest")
                .email("admin@booknest.com")
                .motDePasse(passwordEncoder.encode("admin123"))
                .role(User.Role.ADMIN)
                .build());

        User alice = userRepository.save(User.builder()
                .nom("Alice Martin")
                .email("alice@mail.com")
                .motDePasse(passwordEncoder.encode("password123"))
                .role(User.Role.USER)
                .build());

        User bob = userRepository.save(User.builder()
                .nom("Bob Dupont")
                .email("bob@mail.com")
                .motDePasse(passwordEncoder.encode("password123"))
                .role(User.Role.USER)
                .build());

        // Salles
        Salle salleAlpha = salleRepository.save(Salle.builder()
                .nom("Salle Alpha")
                .capacite(20)
                .localisation("Bâtiment A")
                .description("Salle de conférence moderne équipée d'un vidéoprojecteur, tableau blanc et système de visioconférence.")
                .imageUrl(null)
                .disponible(true)
                .build());

        Salle salleBeta = salleRepository.save(Salle.builder()
                .nom("Salle Beta")
                .capacite(8)
                .localisation("Bâtiment B")
                .description("Salle de réunion cosy avec climatisation, idéale pour les petites équipes.")
                .imageUrl(null)
                .disponible(true)
                .build());

        Salle openSpaceC = salleRepository.save(Salle.builder()
                .nom("Open Space C")
                .capacite(50)
                .localisation("RDC")
                .description("Grand open space modulable pour formations, séminaires et événements d'entreprise.")
                .imageUrl(null)
                .disponible(true)
                .build());

        Salle salleGamma = salleRepository.save(Salle.builder()
                .nom("Salle Gamma")
                .capacite(12)
                .localisation("Bâtiment C")
                .description("Salle workshop équipée de tables modulables et de matériel créatif.")
                .imageUrl(null)
                .disponible(true)
                .build());

        Salle boardroom = salleRepository.save(Salle.builder()
                .nom("Boardroom Delta")
                .capacite(6)
                .localisation("3ème étage")
                .description("Salle de direction prestige avec mobilier haut de gamme, écran 4K et accès sécurisé.")
                .imageUrl(null)
                .disponible(true)
                .build());

        // Reservations
        LocalDateTime now = LocalDateTime.now();

        reservationRepository.save(Reservation.builder()
                .salle(salleAlpha)
                .user(alice)
                .dateDebut(now.plusDays(1).withHour(9).withMinute(0))
                .dateFin(now.plusDays(1).withHour(11).withMinute(0))
                .statut(StatutReservation.CONFIRMEE)
                .build());

        reservationRepository.save(Reservation.builder()
                .salle(salleBeta)
                .user(bob)
                .dateDebut(now.plusDays(2).withHour(14).withMinute(0))
                .dateFin(now.plusDays(2).withHour(16).withMinute(0))
                .statut(StatutReservation.EN_ATTENTE)
                .build());

        reservationRepository.save(Reservation.builder()
                .salle(openSpaceC)
                .user(alice)
                .dateDebut(now.minusDays(3).withHour(10).withMinute(0))
                .dateFin(now.minusDays(3).withHour(17).withMinute(0))
                .statut(StatutReservation.ANNULEE)
                .build());

        // Avis
        avisRepository.save(Avis.builder()
                .salle(salleAlpha)
                .user(alice)
                .note(5)
                .commentaire("Excellente salle, très bien équipée et lumineuse !")
                .dateAvis(LocalDate.now().minusDays(5))
                .build());

        avisRepository.save(Avis.builder()
                .salle(salleAlpha)
                .user(bob)
                .note(4)
                .commentaire("Bonne salle, le vidéoprojecteur est de qualité. Légèrement bruyant depuis le couloir.")
                .dateAvis(LocalDate.now().minusDays(2))
                .build());

        avisRepository.save(Avis.builder()
                .salle(salleBeta)
                .user(alice)
                .note(4)
                .commentaire("Très agréable pour les petites réunions, confortable et calme.")
                .dateAvis(LocalDate.now().minusDays(1))
                .build());

        avisRepository.save(Avis.builder()
                .salle(boardroom)
                .user(bob)
                .note(5)
                .commentaire("La salle Boardroom est parfaite pour les présentations importantes. Très impressionnante !")
                .dateAvis(LocalDate.now().minusDays(7))
                .build());
    }
}
