package com.openclassrooms.mddapi.service;

import java.util.List;

import com.openclassrooms.mddapi.dto.CreatePostRequest;
import com.openclassrooms.mddapi.dto.PostDto;

/**
 * Contrat de service pour la gestion des articles.
 */
public interface IPostService {

    /**
     * Retourne les articles des topics auxquels l'utilisateur est abonné,
     * triés par date décroissante.
     *
     * @param userEmail email de l'utilisateur authentifié
     * @return liste de {@link PostDto} du fil d'actualité
     */
    List<PostDto> getFeed(String userEmail);

    /**
     * Retourne un article par son identifiant.
     *
     * @param id identifiant de l'article
     * @return {@link PostDto} correspondant
     * @throws com.openclassrooms.mddapi.exception.ResourceNotFoundException si l'article n'existe pas
     */
    PostDto getById(Long id);

    /**
     * Crée un nouvel article pour l'auteur authentifié.
     *
     * @param request     données de création
     * @param authorEmail email de l'auteur
     * @return {@link PostDto} de l'article créé
     */
    PostDto create(CreatePostRequest request, String authorEmail);
}
