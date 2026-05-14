package com.booknest.controller;

import com.booknest.model.Reservation;
import com.booknest.service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/reservations")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;

    @GetMapping("/mes")
    public String mesReservations(@AuthenticationPrincipal UserDetails userDetails,
                                  Model model) {
        List<Reservation> reservations = reservationService.findByUser(userDetails.getUsername());
        model.addAttribute("reservations", reservations);
        return "reservations/mes-reservations";
    }

    @PostMapping("/reserver")
    public String reserver(@RequestParam Long salleId,
                           @RequestParam String dateDebut,
                           @RequestParam String dateFin,
                           @AuthenticationPrincipal UserDetails userDetails,
                           RedirectAttributes redirectAttributes) {
        try {
            LocalDateTime debut = LocalDateTime.parse(dateDebut);
            LocalDateTime fin = LocalDateTime.parse(dateFin);
            reservationService.reserve(salleId, debut, fin, userDetails.getUsername());
            redirectAttributes.addFlashAttribute("successMessage", "Réservation effectuée avec succès !");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/reservations/mes";
    }

    @PostMapping("/annuler/{id}")
    public String annuler(@PathVariable Long id,
                          @AuthenticationPrincipal UserDetails userDetails,
                          RedirectAttributes redirectAttributes) {
        try {
            reservationService.cancel(id, userDetails.getUsername());
            redirectAttributes.addFlashAttribute("successMessage", "Réservation annulée.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/reservations/mes";
    }
}
