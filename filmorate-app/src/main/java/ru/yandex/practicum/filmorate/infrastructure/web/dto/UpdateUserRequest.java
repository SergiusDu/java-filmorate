package ru.yandex.practicum.filmorate.infrastructure.web.dto;

import jakarta.validation.constraints.*;

import java.time.LocalDate;

public record UpdateUserRequest(@NotNull(message = "Id cannot be null")
                                Long id,

                                @NotBlank(message = "Email cannot be empty")
                                @Email(message = "Invalid email format")
                                String email,

                                @NotBlank(message = "Login cannot be empty")
                                @Pattern(regexp = "^\\S+$", message = "Login cannot contain whitespace")
                                String login,

                                String name,

                                @Past(message = "Birthday cannot be in the future")
                                LocalDate birthday) {}
