package com.booknest.controller;

import com.booknest.model.Avis;
import com.booknest.model.Salle;
import com.booknest.repository.AvisRepository;
import com.booknest.repository.UserRepository;
import com.booknest.service.SalleService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class SalleController {

    private static final int FEATURED_SALLES_LIMIT = 3;

    private final SalleService salleService;
    private final AvisRepository avisRepository;
    private final UserRepository userRepository;

    @GetMapping("/")
    public String indexPage(Model model) {
        Page<Salle> sallesPage = salleService.findAll(PageRequest.of(0, 6, Sort.by("nom")));
        model.addAttribute("featuredSalles", sallesPage.getContent().stream().limit(FEATURED_SALLES_LIMIT).toList());
        model.addAttribute("sallesPage", sallesPage);
        return "index";
    }

    @GetMapping("/salles")
    public String listSalles(@RequestParam(defaultValue = "0") int page,
                             @RequestParam(defaultValue = "6") int size,
                             @RequestParam(required = false) String nom,
                             @RequestParam(required = false) String localisation,
                             @RequestParam(required = false) Integer minCapacite,
                             Model model) {
        PageRequest pageable = PageRequest.of(page, size, Sort.by("nom"));
        Page<Salle> sallesPage;

        boolean hasSearch = (nom != null && !nom.isBlank())
                || (localisation != null && !localisation.isBlank())
                || minCapacite != null;

        if (hasSearch) {
            sallesPage = salleService.search(nom, localisation, minCapacite, pageable);
        } else {
            sallesPage = salleService.findAll(pageable);
        }

        model.addAttribute("sallesPage", sallesPage);
        model.addAttribute("nom", nom);
        model.addAttribute("localisation", localisation);
        model.addAttribute("minCapacite", minCapacite);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", sallesPage.getTotalPages());
        return "salles/list";
    }

    @GetMapping("/salles/{id}")
    public String detailSalle(@PathVariable Long id, Model model) {
        Salle salle = salleService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Salle introuvable: " + id));
        List<Avis> avisList = avisRepository.findBySalle(salle);
        Double avgNote = avisRepository.avgNoteBySalle(salle);

        model.addAttribute("salle", salle);
        model.addAttribute("avisList", avisList);
        model.addAttribute("avgNote", avgNote != null ? Math.round(avgNote * 10.0) / 10.0 : null);
        model.addAttribute("avis", new Avis());
        return "salles/detail";
    }

    @PostMapping("/avis/{salleId}")
    public String saveAvis(@PathVariable Long salleId,
                           @RequestParam int note,
                           @RequestParam(required = false) String commentaire,
                           @AuthenticationPrincipal UserDetails userDetails) {
        Salle salle = salleService.findById(salleId)
                .orElseThrow(() -> new IllegalArgumentException("Salle introuvable"));
        com.booknest.model.User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow();

        Avis avis = Avis.builder()
                .salle(salle)
                .user(user)
                .note(note)
                .commentaire(commentaire)
                .dateAvis(LocalDate.now())
                .build();
        avisRepository.save(avis);
        return "redirect:/salles/" + salleId;
    }
}
