package ru.yandex.practicum.filmorate.users.domain.model.value;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import ru.yandex.practicum.filmorate.common.exception.InvalidUserDataException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class LoginTest {

  @Test
  void shouldCreateLogin_whenLoginIsValid() {
    // given
    String validLogin = "Valid-Login_123";

    // when
    Login login = new Login(validLogin);

    // then
    assertThat(login.login()).isEqualTo(validLogin);
  }

  @ParameterizedTest
  @NullAndEmptySource
  @ValueSource(strings = {"  ",
                          "login space",
                          " login",
                          "login\t",
                          "\nlogin"
  })
  void shouldThrowException_whenLoginIsInvalid(String invalidLogin) {
    // when & then
    assertThrows(InvalidUserDataException.class,
                 () -> new Login(invalidLogin));
  }
}