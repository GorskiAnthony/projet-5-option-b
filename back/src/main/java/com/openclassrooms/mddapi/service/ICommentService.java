package com.openclassrooms.mddapi.service;

import java.util.List;

import com.openclassrooms.mddapi.dto.CommentDto;

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
}
