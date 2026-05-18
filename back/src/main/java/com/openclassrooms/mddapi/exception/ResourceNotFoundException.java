package com.openclassrooms.mddapi.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Levée lorsqu'une ressource demandée est introuvable en base de données.
 * Interceptée par {@link com.openclassrooms.mddapi.controller.GlobalExceptionHandler}
 * qui retourne une réponse HTTP 404 avec un message JSON.
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
