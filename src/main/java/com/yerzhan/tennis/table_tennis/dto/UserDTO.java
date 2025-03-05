package com.yerzhan.tennis.table_tennis.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Email;
import lombok.Data;

@Data
public class UserDTO {
    private Integer id;

    @NotBlank(message = "Имя пользователя не может быть пустым")
    @Size(min = 3, max = 50, message = "Имя пользователя должно быть от 3 до 50 символов")
    private String username;

    @NotBlank(message = "Email не может быть пустым")
    @Email(message = "Пожалуйста, введите корректный email")
    private String email;

    @NotBlank(message = "Пароль не может быть пустым")
    @Size(min = 4, max = 100, message = "Пароль должен быть от 4 до 100 символов")
    private String password;
} 