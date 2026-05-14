package com.booknest.controller;

import com.booknest.model.Reservation;
import com.booknest.model.Reservation.StatutReservation;
import com.booknest.model.Salle;
import com.booknest.repository.ReservationRepository;
import com.booknest.repository.SalleRepository;
import com.booknest.service.ReservationService;
import com.booknest.service.SalleService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final SalleService salleService;
    private final ReservationService reservationService;
    private final ReservationRepository reservationRepository;
    private final SalleRepository salleRepository;

    @Value("${app.upload.dir:uploads}")
    private String uploadDir;

    // Dashboard
    @GetMapping({"/dashboard", ""})
    public String dashboard(Model model) {
        long totalSalles = salleRepository.count();
        long totalReservations = reservationRepository.count();
        long enAttente = reservationRepository.countEnAttente();
        long confirmees = reservationRepository.countConfirmee();

        model.addAttribute("totalSalles", totalSalles);
        model.addAttribute("totalReservations", totalReservations);
        model.addAttribute("enAttente", enAttente);
        model.addAttribute("confirmees", confirmees);

        Page<Reservation> recentPage = reservationService.findAll(PageRequest.of(0, 5, Sort.by("dateDebut").descending()));
        model.addAttribute("recentReservations", recentPage.getContent());
        return "admin/dashboard";
    }

    // Salles list
    @GetMapping("/salles")
    public String adminSalles(@RequestParam(defaultValue = "0") int page,
                              @RequestParam(defaultValue = "10") int size,
                              Model model) {
        Page<Salle> sallesPage = salleService.findAll(PageRequest.of(page, size, Sort.by("nom")));
        model.addAttribute("sallesPage", sallesPage);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", sallesPage.getTotalPages());
        return "admin/salles";
    }

    // New salle form
    @GetMapping("/salles/new")
    public String newSalleForm(Model model) {
        model.addAttribute("salle", new Salle());
        return "admin/salle-form";
    }

    // Edit salle form
    @GetMapping("/salles/edit/{id}")
    public String editSalleForm(@PathVariable Long id, Model model) {
        Salle salle = salleService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Salle introuvable: " + id));
        model.addAttribute("salle", salle);
        return "admin/salle-form";
    }

    // Save salle
    @PostMapping("/salles/save")
    public String saveSalle(@ModelAttribute Salle salle,
                            @RequestParam(required = false) MultipartFile imageFile,
                            RedirectAttributes redirectAttributes) {
        if (imageFile != null && !imageFile.isEmpty()) {
            try {
                Path uploadPath = Paths.get(uploadDir);
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }
                String filename = UUID.randomUUID() + "_" + imageFile.getOriginalFilename();
                Files.copy(imageFile.getInputStream(), uploadPath.resolve(filename), StandardCopyOption.REPLACE_EXISTING);
                salle.setImageUrl(filename);
            } catch (IOException e) {
                redirectAttributes.addFlashAttribute("errorMessage", "Erreur lors du téléchargement de l'image.");
                return "redirect:/admin/salles";
            }
        }
        salleService.save(salle);
        redirectAttributes.addFlashAttribute("successMessage", "Salle sauvegardée avec succès.");
        return "redirect:/admin/salles";
    }

    // Delete salle
    @PostMapping("/salles/delete/{id}")
    public String deleteSalle(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        salleService.delete(id);
        redirectAttributes.addFlashAttribute("successMessage", "Salle supprimée.");
        return "redirect:/admin/salles";
    }

    // Reservations list
    @GetMapping("/reservations")
    public String adminReservations(@RequestParam(defaultValue = "0") int page,
                                    @RequestParam(defaultValue = "10") int size,
                                    Model model) {
        Page<Reservation> reservationsPage = reservationService.findAll(PageRequest.of(page, size));
        model.addAttribute("reservationsPage", reservationsPage);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", reservationsPage.getTotalPages());
        model.addAttribute("statuts", StatutReservation.values());
        return "admin/reservations";
    }

    // Update reservation statut
    @PostMapping("/reservations/{id}/statut")
    public String updateStatut(@PathVariable Long id,
                               @RequestParam StatutReservation statut,
                               RedirectAttributes redirectAttributes) {
        reservationService.updateStatut(id, statut);
        redirectAttributes.addFlashAttribute("successMessage", "Statut mis à jour.");
        return "redirect:/admin/reservations";
    }

    // Export CSV
    @GetMapping("/reservations/export")
    public ResponseEntity<byte[]> exportCsv() {
        List<Reservation> all = reservationRepository.findAll();
        StringBuilder sb = new StringBuilder();
        sb.append("ID,Salle,Utilisateur,Date Début,Date Fin,Statut\n");
        for (Reservation r : all) {
            sb.append(r.getId()).append(",")
              .append(r.getSalle().getNom()).append(",")
              .append(r.getUser().getNom()).append(",")
              .append(r.getDateDebut()).append(",")
              .append(r.getDateFin()).append(",")
              .append(r.getStatut()).append("\n");
        }
        byte[] csvBytes = sb.toString().getBytes();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"reservations.csv\"")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(csvBytes);
    }
}
