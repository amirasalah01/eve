package com.booknest.api;

import com.booknest.dto.ReservationDto;
import com.booknest.model.Reservation;
import com.booknest.model.Reservation.StatutReservation;
import com.booknest.service.ReservationService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/reservations")
@RequiredArgsConstructor
public class ReservationApiController {

    private final ReservationService reservationService;

    @GetMapping
    public List<ReservationDto> getAll() {
        return reservationService.findAll(Pageable.unpaged())
                .stream()
                .map(this::toDto)
                .toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReservationDto> getById(@PathVariable Long id) {
        return reservationService.findById(id)
                .map(r -> ResponseEntity.ok(toDto(r)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody ReservationRequest req) {
        try {
            Reservation saved = reservationService.reserve(
                    req.getSalleId(),
                    req.getDateDebut(),
                    req.getDateFin(),
                    req.getUserEmail());
            return ResponseEntity.status(HttpStatus.CREATED).body(toDto(saved));
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateStatut(@PathVariable Long id,
                                           @RequestBody StatutRequest req) {
        try {
            StatutReservation statut = StatutReservation.valueOf(req.getStatut());
            reservationService.updateStatut(id, statut);
            return reservationService.findById(id)
                    .map(r -> ResponseEntity.ok(toDto(r)))
                    .orElse(ResponseEntity.notFound().build());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Statut invalide: " + req.getStatut());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (reservationService.findById(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        reservationService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    // ---- helpers ----

    private ReservationDto toDto(Reservation r) {
        return ReservationDto.builder()
                .id(r.getId())
                .dateDebut(r.getDateDebut())
                .dateFin(r.getDateFin())
                .statut(r.getStatut() != null ? r.getStatut().name() : null)
                .salleId(r.getSalle() != null ? r.getSalle().getId() : null)
                .salleNom(r.getSalle() != null ? r.getSalle().getNom() : null)
                .userId(r.getUser() != null ? r.getUser().getId() : null)
                .userEmail(r.getUser() != null ? r.getUser().getEmail() : null)
                .userNom(r.getUser() != null ? r.getUser().getNom() : null)
                .build();
    }

    // ---- inner request DTOs ----

    @Data
    @NoArgsConstructor
    public static class ReservationRequest {
        @NotNull
        private Long salleId;
        @NotNull
        private String userEmail;
        @NotNull
        private LocalDateTime dateDebut;
        @NotNull
        private LocalDateTime dateFin;
    }

    @Data
    @NoArgsConstructor
    public static class StatutRequest {
        private String statut;
    }
}
