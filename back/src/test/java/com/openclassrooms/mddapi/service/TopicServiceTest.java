package com.openclassrooms.mddapi.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.openclassrooms.mddapi.dto.TopicDto;
import com.openclassrooms.mddapi.model.Topic;
import com.openclassrooms.mddapi.model.User;
import com.openclassrooms.mddapi.repository.TopicRepository;
import com.openclassrooms.mddapi.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
@DisplayName("TopicService — tests unitaires")
class TopicServiceTest {

    @Mock TopicRepository topicRepository;
    @Mock UserRepository userRepository;
    @InjectMocks TopicService topicService;

    private Topic angular;
    private Topic java;
    private User user;

    @BeforeEach
    void setUp() {
        angular = new Topic();
        angular.setId(1L);
        angular.setName("Angular");

        java = new Topic();
        java.setId(2L);
        java.setName("Java");

        user = new User();
        user.setId(1L);
        user.setEmail("alice@example.com");
        user.setSubscriptions(new HashSet<>(Set.of(angular)));
    }

    @Test
    @DisplayName("getTopics — doit retourner tous les topics avec le statut d'abonnement correct")
    void getTopics_shouldReturnAllTopicsWithCorrectSubscriptionStatus() {
        // Arrange
        when(userRepository.findByEmail("alice@example.com")).thenReturn(Optional.of(user));
        when(topicRepository.findAll()).thenReturn(List.of(angular, java));

        // Act
        List<TopicDto> result = topicService.getTopics("alice@example.com");

        // Assert
        assertThat(result).hasSize(2);
        assertThat(result).anySatisfy(t -> {
            assertThat(t.getName()).isEqualTo("Angular");
            assertThat(t.isSubscribed()).isTrue();
        });
        assertThat(result).anySatisfy(t -> {
            assertThat(t.getName()).isEqualTo("Java");
            assertThat(t.isSubscribed()).isFalse();
        });
    }

    @Test
    @DisplayName("subscribe — doit ajouter le topic aux abonnements de l'utilisateur")
    void subscribe_shouldAddTopicToUserSubscriptions() {
        // Arrange
        when(userRepository.findByEmail("alice@example.com")).thenReturn(Optional.of(user));
        when(topicRepository.findById(2L)).thenReturn(Optional.of(java));
        when(userRepository.save(user)).thenReturn(user);

        // Act
        topicService.subscribe(2L, "alice@example.com");

        // Assert
        assertThat(user.getSubscriptions()).contains(java);
        verify(userRepository).save(user);
    }

    @Test
    @DisplayName("unsubscribe — doit retirer le topic des abonnements de l'utilisateur")
    void unsubscribe_shouldRemoveTopicFromUserSubscriptions() {
        // Arrange
        when(userRepository.findByEmail("alice@example.com")).thenReturn(Optional.of(user));
        when(topicRepository.findById(1L)).thenReturn(Optional.of(angular));
        when(userRepository.save(user)).thenReturn(user);

        // Act
        topicService.unsubscribe(1L, "alice@example.com");

        // Assert
        assertThat(user.getSubscriptions()).doesNotContain(angular);
        verify(userRepository).save(user);
    }

    @Test
    @DisplayName("subscribe — ne doit pas modifier l'abonnement si le topic est déjà souscrit")
    void subscribe_shouldBeIdempotent_whenTopicAlreadySubscribed() {
        // Arrange
        when(userRepository.findByEmail("alice@example.com")).thenReturn(Optional.of(user));
        when(topicRepository.findById(1L)).thenReturn(Optional.of(angular));
        when(userRepository.save(user)).thenReturn(user);

        // Act
        topicService.subscribe(1L, "alice@example.com");

        // Assert
        assertThat(user.getSubscriptions()).containsExactly(angular);
    }
}
