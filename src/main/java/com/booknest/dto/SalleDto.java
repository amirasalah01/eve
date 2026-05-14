package com.booknest.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SalleDto {
    private Long id;
    private String nom;
    private int capacite;
    private String localisation;
    private String description;
    private String imageUrl;
    private boolean disponible;
}
