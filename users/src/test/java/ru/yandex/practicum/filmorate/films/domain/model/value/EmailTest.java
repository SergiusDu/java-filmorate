package ru.yandex.practicum.filmorate.films.domain.model.value;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import ru.yandex.practicum.filmorate.common.exception.InvalidUserDataException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class EmailTest {

  @Test
  void shouldCreateEmailAndConvertToLowerCase_whenEmailIsValid() {
    String rawEmail = "Test.User+Alias@Example.COM";
    String expectedEmail = "test.user+alias@example.com";

    Email email = new Email(rawEmail);

    assertThat(email.email()).isEqualTo(expectedEmail);
  }

  @ParameterizedTest
  @NullAndEmptySource
  @ValueSource(strings = {"  ",
                          "plainaddress",
                          "test@example@com",
                          "@example.com",
                          "test@",
                          "test@.com",
                          ".test@example.com",
                          "test" + ".@example" + ".com",
                          "test..test@example.com",
                          "test@example-.com"
  })
  void shouldThrowException_whenEmailIsInvalid(String invalidEmail) {
    assertThrows(InvalidUserDataException.class,
                 () -> new Email(invalidEmail));
  }
}