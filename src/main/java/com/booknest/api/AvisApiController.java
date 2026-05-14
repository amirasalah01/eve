package com.booknest.api;

import com.booknest.dto.AvisDto;
import com.booknest.model.Avis;
import com.booknest.service.AvisService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/avis")
@RequiredArgsConstructor
public class AvisApiController {

    private final AvisService avisService;

    @GetMapping
    public List<AvisDto> getAll() {
        return avisService.findAll().stream().map(this::toDto).toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<AvisDto> getById(@PathVariable Long id) {
        return avisService.findById(id)
                .map(a -> ResponseEntity.ok(toDto(a)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/salle/{salleId}")
    public List<AvisDto> getBySalle(@PathVariable Long salleId) {
        return avisService.findBySalle(salleId).stream().map(this::toDto).toList();
    }

    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody AvisRequest req) {
        try {
            Avis saved = avisService.create(req.getSalleId(), req.getUserId(),
                    req.getNote(), req.getCommentaire());
            return ResponseEntity.status(HttpStatus.CREATED).body(toDto(saved));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<AvisDto> update(@PathVariable Long id,
                                     @Valid @RequestBody AvisRequest req) {
        return avisService.update(id, req.getNote(), req.getCommentaire())
                .map(a -> ResponseEntity.ok(toDto(a)))
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (avisService.findById(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        avisService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    // ---- helpers ----

    private AvisDto toDto(Avis a) {
        return AvisDto.builder()
                .id(a.getId())
                .note(a.getNote())
                .commentaire(a.getCommentaire())
                .dateAvis(a.getDateAvis())
                .salleId(a.getSalle() != null ? a.getSalle().getId() : null)
                .salleNom(a.getSalle() != null ? a.getSalle().getNom() : null)
                .userId(a.getUser() != null ? a.getUser().getId() : null)
                .userEmail(a.getUser() != null ? a.getUser().getEmail() : null)
                .userNom(a.getUser() != null ? a.getUser().getNom() : null)
                .build();
    }

    // ---- inner request DTO ----

    @Data
    @NoArgsConstructor
    public static class AvisRequest {
        @NotNull
        private Long salleId;
        @NotNull
        private Long userId;
        @Min(1)
        @Max(5)
        private int note;
        private String commentaire;
    }
}
