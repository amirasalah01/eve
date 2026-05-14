package com.booknest.dto;

import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReservationDto {
    private Long id;
    private LocalDateTime dateDebut;
    private LocalDateTime dateFin;
    private String statut;
    private Long salleId;
    private String salleNom;
    private Long userId;
    private String userEmail;
    private String userNom;
}
