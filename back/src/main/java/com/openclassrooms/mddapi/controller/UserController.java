package com.openclassrooms.mddapi.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

import com.openclassrooms.mddapi.dto.UpdateProfileRequest;
import com.openclassrooms.mddapi.dto.UserDto;
import com.openclassrooms.mddapi.model.User;
import com.openclassrooms.mddapi.service.IUserService;

/**
 * Expose les endpoints du profil utilisateur authentifié
 * ({@code GET /api/users/me}, {@code PUT /api/users/me}).
 */
@RestController
@RequestMapping("/api/users")
@Tag(name = "Profil utilisateur", description = "Consultation et mise à jour du profil de l'utilisateur authentifié")
public class UserController {

    private final IUserService userService;

    public UserController(IUserService userService) {
        this.userService = userService;
    }

    @Operation(summary = "Profil de l'utilisateur courant")
    @ApiResponse(responseCode = "200", description = "Données du profil")
    @GetMapping("/me")
    public UserDto me(@AuthenticationPrincipal User user) {
        return userService.toDto(user);
    }

    @Operation(summary = "Mettre à jour le profil", description = "Tous les champs sont optionnels. Seuls les champs fournis sont mis à jour.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Profil mis à jour"),
        @ApiResponse(responseCode = "400", description = "Champs invalides"),
        @ApiResponse(responseCode = "409", description = "Email ou nom d'utilisateur déjà utilisé")
    })
    @PutMapping("/me")
    public ResponseEntity<UserDto> updateProfile(@AuthenticationPrincipal User user,
                                                 @Valid @RequestBody UpdateProfileRequest request) {
        return ResponseEntity.ok(userService.updateProfile(user.getEmail(), request));
    }
}
