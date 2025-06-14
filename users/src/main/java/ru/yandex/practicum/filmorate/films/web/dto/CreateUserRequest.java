package ru.yandex.practicum.filmorate.films.web.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;

import java.time.LocalDate;

/**
 Data transfer object for creating a new user. Contains validated user information needed for registration. */
public record CreateUserRequest(@NotBlank(message = "Email cannot be empty")
                                @Email(message = "Invalid email format")
                                String email,

                                @NotBlank(message = "Login cannot be empty")
                                @Pattern(regexp = "^\\S+$", message = "Login cannot contain whitespace")
                                String login,

                                String name,

                                @Past(message = "Birthday cannot be in the future")
                                LocalDate birthday) {}