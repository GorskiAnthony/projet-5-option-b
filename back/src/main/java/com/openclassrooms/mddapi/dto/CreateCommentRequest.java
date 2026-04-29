package com.openclassrooms.mddapi.dto;

import jakarta.validation.constraints.NotBlank;

public class CreateCommentRequest {

    @NotBlank(message = "Le contenu est obligatoire")
    private String content;

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
}
