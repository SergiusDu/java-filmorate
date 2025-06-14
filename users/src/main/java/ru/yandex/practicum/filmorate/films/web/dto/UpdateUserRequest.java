package ru.yandex.practicum.filmorate.films.web.dto;

import jakarta.validation.constraints.*;

import java.time.LocalDate;
import java.util.UUID;

public record UpdateUserRequest(@NotNull(message = "Id cannot be null")
                                UUID id,

                                @NotBlank(message = "Email cannot be empty")
                                @Email(message = "Invalid email format")
                                String email,

                                @NotBlank(message = "Login cannot be empty")
                                @Pattern(regexp = "^\\S+$", message = "Login cannot contain whitespace")
                                String login,

                                String name,

                                @Past(message = "Birthday cannot be in the future")
                                LocalDate birthday) {}
