package ru.yandex.practicum.filmorate.users.domain.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import ru.yandex.practicum.filmorate.common.exception.InvalidUserDataException;
import ru.yandex.practicum.filmorate.users.domain.model.value.Email;
import ru.yandex.practicum.filmorate.users.domain.model.value.Login;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UserTest {

  private int validId;
  private Email validEmail;
  private Login validLogin;
  private String validName;
  private LocalDate validBirthday;

  @BeforeEach
  void setUp() {
    validId = 1;
    validEmail = new Email("test@example.com");
    validLogin = new Login("validlogin");
    validName = "John Doe";
    validBirthday = LocalDate.of(1990,
                                 1,
                                 1);
  }

  @Test
  void shouldCreateUser_whenAllDataIsValid() {
    // when
    User user = new User(validId,
                         validEmail,
                         validLogin,
                         validName,
                         validBirthday);

    // then
    assertThat(user.id()).isEqualTo(validId);
    assertThat(user.email()).isEqualTo(validEmail);
    assertThat(user.login()).isEqualTo(validLogin);
    assertThat(user.name()).isEqualTo(validName);
    assertThat(user.birthday()).isEqualTo(validBirthday);
  }

  @Test
  void shouldThrowException_whenEmailIsNull() {
    // when & then
    var exception = assertThrows(InvalidUserDataException.class,
                                 () -> new User(validId,
                                                null,
                                                validLogin,
                                                validName,
                                                validBirthday));
    assertThat(exception.getMessage()).isEqualTo("User email must not be null");
  }

  @Test
  void shouldThrowException_whenLoginIsNull() {
    // when & then
    var exception = assertThrows(InvalidUserDataException.class,
                                 () -> new User(validId,
                                                validEmail,
                                                null,
                                                validName,
                                                validBirthday));
    assertThat(exception.getMessage()).isEqualTo("User login must not be null");
  }

 /* @ParameterizedTest
  @NullAndEmptySource
  @ValueSource(strings = {"  ",
                          "\t",
                          "\n"
  })
  void shouldThrowException_whenNameIsBlank(String invalidName) {
    // when & then
    var exception = assertThrows(InvalidUserDataException.class,
                                 () -> new User(validId,
                                                validEmail,
                                                validLogin,
                                                invalidName,
                                                validBirthday));
    assertThat(exception.getMessage()).isEqualTo("User name must not be empty");
  }*/

  @Test
  void shouldThrowException_whenBirthdayIsNull() {
    // when & then
    var exception = assertThrows(InvalidUserDataException.class,
                                 () -> new User(validId,
                                                validEmail,
                                                validLogin,
                                                validName,
                                                null));
    assertThat(exception.getMessage()).isEqualTo("User birthday must not be null");
  }
}