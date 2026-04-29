package com.openclassrooms.mddapi.service;

import java.util.List;

import com.openclassrooms.mddapi.dto.TopicDto;

/**
 * Contrat de service pour la gestion des topics et des abonnements.
 */
public interface ITopicService {

    /**
     * Retourne tous les topics avec l'état d'abonnement de l'utilisateur.
     *
     * @param userEmail email de l'utilisateur authentifié
     * @return liste de {@link TopicDto}
     */
    List<TopicDto> getTopics(String userEmail);

    /**
     * Abonne l'utilisateur à un topic.
     *
     * @param topicId   identifiant du topic
     * @param userEmail email de l'utilisateur
     * @throws com.openclassrooms.mddapi.exception.ResourceNotFoundException si le topic ou l'utilisateur n'existe pas
     */
    void subscribe(Long topicId, String userEmail);

    /**
     * Désabonne l'utilisateur d'un topic.
     *
     * @param topicId   identifiant du topic
     * @param userEmail email de l'utilisateur
     * @throws com.openclassrooms.mddapi.exception.ResourceNotFoundException si le topic ou l'utilisateur n'existe pas
     */
    void unsubscribe(Long topicId, String userEmail);
}
