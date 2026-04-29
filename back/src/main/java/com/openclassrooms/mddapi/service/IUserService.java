package com.openclassrooms.mddapi.service;

import com.openclassrooms.mddapi.dto.RegisterRequest;
import com.openclassrooms.mddapi.dto.UpdateProfileRequest;
import com.openclassrooms.mddapi.dto.UserDto;
import com.openclassrooms.mddapi.model.User;

public interface IUserService {

    User register(RegisterRequest request);

    UserDto toDto(User user);

    UserDto updateProfile(String email, UpdateProfileRequest request);
}
