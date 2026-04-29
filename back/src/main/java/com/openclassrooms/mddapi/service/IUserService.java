package com.openclassrooms.mddapi.service;

import com.openclassrooms.mddapi.dto.RegisterRequest;
import com.openclassrooms.mddapi.dto.UpdateProfileRequest;
import com.openclassrooms.mddapi.dto.UserDto;
import com.openclassrooms.mddapi.model.User;

/**
 * Contrat de service pour la gestion des utilisateurs.
 */
public interface IUserService {

    /**
     * Inscrit un nouvel utilisateur et retourne l'entité persistée.
     *
     * @param request données d'inscription
     * @return {@link User} créé
     * @throws IllegalArgumentException si l'email ou le nom d'utilisateur est déjà pris
     */
    User register(RegisterRequest request);

    /**
     * Convertit une entité {@link User} en {@link UserDto}.
     *
     * @param user entité source
     * @return {@link UserDto} correspondant
     */
    UserDto toDto(User user);

    /**
     * Met à jour le profil de l'utilisateur identifié par son email.
     *
     * @param email   email actuel de l'utilisateur
     * @param request nouvelles valeurs (champs optionnels)
     * @return {@link UserDto} mis à jour
     */
    UserDto updateProfile(String email, UpdateProfileRequest request);
}
