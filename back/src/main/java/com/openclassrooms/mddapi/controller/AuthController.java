package com.openclassrooms.mddapi.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

import com.openclassrooms.mddapi.dto.AuthResponse;
import com.openclassrooms.mddapi.dto.LoginRequest;
import com.openclassrooms.mddapi.dto.RegisterRequest;
import com.openclassrooms.mddapi.model.User;
import com.openclassrooms.mddapi.security.JwtUtils;
import com.openclassrooms.mddapi.service.IUserService;

/**
 * Expose les endpoints d'inscription ({@code POST /api/auth/register})
 * et de connexion ({@code POST /api/auth/login}).
 */
@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentification", description = "Inscription et connexion — endpoints publics, aucun token requis")
@SecurityRequirements
public class AuthController {

    private final AuthenticationManager authManager;
    private final JwtUtils jwtUtils;
    private final IUserService userService;

    public AuthController(AuthenticationManager authManager, JwtUtils jwtUtils, IUserService userService) {
        this.authManager = authManager;
        this.jwtUtils = jwtUtils;
        this.userService = userService;
    }

    @Operation(summary = "Créer un compte", description = "Crée un nouvel utilisateur et retourne un token JWT.")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Compte créé, token JWT retourné"),
        @ApiResponse(responseCode = "400", description = "Champs invalides (format, longueur, pattern mot de passe)"),
        @ApiResponse(responseCode = "409", description = "Email ou nom d'utilisateur déjà utilisé")
    })
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        User user = userService.register(request);
        String token = jwtUtils.generateToken(user.getEmail());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new AuthResponse(token, userService.toDto(user)));
    }

    @Operation(summary = "Se connecter", description = "Authentifie un utilisateur par email ou nom d'utilisateur et retourne un token JWT.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Authentification réussie, token JWT retourné"),
        @ApiResponse(responseCode = "400", description = "Champs manquants"),
        @ApiResponse(responseCode = "401", description = "Identifiants incorrects")
    })
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        try {
            Authentication auth = authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getIdentifier(), request.getPassword())
            );
            User user = (User) auth.getPrincipal();
            String token = jwtUtils.generateToken(user.getEmail());
            return ResponseEntity.ok(new AuthResponse(token, userService.toDto(user)));
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Identifiants incorrects");
        }
    }
}
