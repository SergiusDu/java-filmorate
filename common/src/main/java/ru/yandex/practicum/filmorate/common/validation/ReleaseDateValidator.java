package ru.yandex.practicum.filmorate.common.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.common.config.AppValidationProperties;

import java.time.LocalDate;

@Component
public class ReleaseDateValidator
    implements ConstraintValidator<ValidReleaseDate, LocalDate> {
  private final LocalDate earliestDate;

  public ReleaseDateValidator(AppValidationProperties validationProperties) {
    this.earliestDate = validationProperties.film()
                                            .releaseDate()
                                            .earliest();
  }

  @Override
  public boolean isValid(LocalDate value, ConstraintValidatorContext context) {
    return value != null && !value.isBefore(earliestDate);
  }
}
