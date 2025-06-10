package ru.yandex.practicum.filmorate.infrastructure.config;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.validation.annotation.Validated;

/**
 Configuration properties for validation rules used in the application. Maps to properties with prefix "validation" in
 configuration files. */
@Validated
@ConfigurationProperties(prefix = "validation")
public record AppValidationProperties(@NotNull @Valid FilmValidationSettings film) {

  /**
   Settings for film validation rules.
   @param description Settings for film description validation
   */
  public record FilmValidationSettings(@NotNull DescriptionSettings description) {}

  /**
   Settings for film description validation rules.
   @param maxLength Maximum allowed length for film descriptions. Defaults to 200 characters.
   */
  public record DescriptionSettings(@DefaultValue("200") @Min(1) int maxLength) {}
}