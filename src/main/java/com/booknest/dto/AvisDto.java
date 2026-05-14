package com.booknest.dto;

import lombok.*;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AvisDto {
    private Long id;
    private int note;
    private String commentaire;
    private LocalDate dateAvis;
    private Long salleId;
    private String salleNom;
    private Long userId;
    private String userEmail;
    private String userNom;
}
