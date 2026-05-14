package com.booknest.api;

import com.booknest.dto.SalleDto;
import com.booknest.model.Salle;
import com.booknest.service.SalleService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/salles")
@RequiredArgsConstructor
public class SalleApiController {

    private final SalleService salleService;

    @GetMapping
    public List<SalleDto> getAll() {
        return salleService.findAll(Pageable.unpaged())
                .stream()
                .map(this::toDto)
                .toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<SalleDto> getById(@PathVariable Long id) {
        return salleService.findById(id)
                .map(s -> ResponseEntity.ok(toDto(s)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<SalleDto> create(@Valid @RequestBody SalleRequest req) {
        Salle salle = toEntity(req);
        Salle saved = salleService.save(salle);
        return ResponseEntity.status(HttpStatus.CREATED).body(toDto(saved));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SalleDto> update(@PathVariable Long id,
                                            @Valid @RequestBody SalleRequest req) {
        return salleService.findById(id).map(existing -> {
            existing.setNom(req.getNom());
            existing.setCapacite(req.getCapacite());
            existing.setLocalisation(req.getLocalisation());
            existing.setDescription(req.getDescription());
            existing.setImageUrl(req.getImageUrl());
            existing.setDisponible(req.isDisponible());
            return ResponseEntity.ok(toDto(salleService.save(existing)));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (salleService.findById(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        salleService.delete(id);
        return ResponseEntity.noContent().build();
    }

    // ---- helpers ----

    private SalleDto toDto(Salle s) {
        return SalleDto.builder()
                .id(s.getId())
                .nom(s.getNom())
                .capacite(s.getCapacite())
                .localisation(s.getLocalisation())
                .description(s.getDescription())
                .imageUrl(s.getImageUrl())
                .disponible(s.isDisponible())
                .build();
    }

    private Salle toEntity(SalleRequest req) {
        Salle salle = new Salle();
        salle.setNom(req.getNom());
        salle.setCapacite(req.getCapacite());
        salle.setLocalisation(req.getLocalisation());
        salle.setDescription(req.getDescription());
        salle.setImageUrl(req.getImageUrl());
        salle.setDisponible(req.isDisponible());
        return salle;
    }

    // ---- inner request DTO ----

    @Data
    @NoArgsConstructor
    public static class SalleRequest {
        @NotBlank
        private String nom;
        @Min(1)
        private int capacite;
        private String localisation;
        private String description;
        private String imageUrl;
        private boolean disponible = true;
    }
}
