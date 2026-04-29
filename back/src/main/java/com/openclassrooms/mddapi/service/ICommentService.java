package com.openclassrooms.mddapi.service;

import java.util.List;

import com.openclassrooms.mddapi.dto.CommentDto;
import com.openclassrooms.mddapi.dto.CreateCommentRequest;

/**
 * Contrat de service pour la gestion des commentaires.
 */
public interface ICommentService {

    /**
     * Retourne les commentaires d'un article triés par date croissante.
     *
     * @param postId identifiant de l'article
     * @return liste de {@link CommentDto}
     */
    List<CommentDto> getByPost(Long postId);

    /**
     * Crée un nouveau commentaire sur un article.
     *
     * @param postId      identifiant de l'article
     * @param request     contenu du commentaire
     * @param authorEmail email de l'auteur authentifié
     * @return {@link CommentDto} du commentaire créé
     * @throws com.openclassrooms.mddapi.exception.ResourceNotFoundException si l'article ou l'auteur n'existe pas
     */
    CommentDto create(Long postId, CreateCommentRequest request, String authorEmail);
}
