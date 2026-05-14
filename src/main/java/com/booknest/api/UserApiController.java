package com.booknest.api;

import com.booknest.dto.UserDto;
import com.booknest.model.User;
import com.booknest.service.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserApiController {

    private final UserService userService;

    @GetMapping
    public List<UserDto> getAll() {
        return userService.findAll().stream().map(this::toDto).toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getById(@PathVariable Long id) {
        return userService.findById(id)
                .map(u -> ResponseEntity.ok(toDto(u)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<UserDto> create(@Valid @RequestBody UserRequest req) {
        User user = new User();
        user.setNom(req.getNom());
        user.setEmail(req.getEmail());
        user.setMotDePasse(req.getMotDePasse());
        try {
            User saved = userService.register(user);
            return ResponseEntity.status(HttpStatus.CREATED).body(toDto(saved));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserDto> update(@PathVariable Long id,
                                           @Valid @RequestBody UserRequest req) {
        return userService.findById(id).map(existing -> {
            existing.setNom(req.getNom());
            // email is not updated to keep it as the unique identifier
            User saved = userService.save(existing);
            return ResponseEntity.ok(toDto(saved));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (userService.findById(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        userService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    // ---- helpers ----

    private UserDto toDto(User u) {
        return UserDto.builder()
                .id(u.getId())
                .nom(u.getNom())
                .email(u.getEmail())
                .role(u.getRole() != null ? u.getRole().name() : null)
                .build();
    }

    // ---- inner request DTO ----

    @Data
    @NoArgsConstructor
    public static class UserRequest {
        @NotBlank
        private String nom;
        @Email
        @NotBlank
        private String email;
        @NotBlank
        private String motDePasse;
    }
}
