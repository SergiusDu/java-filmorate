package ru.yandex.practicum.filmorate.common.validation;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import ru.yandex.practicum.filmorate.common.exception.InvalidFilmDataException;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ValidationUtilsTest {

  @Nested
  class NotNullTests {
    @Test
    void shouldReturnTheObject_whenObjectIsNotNull() {
      String validObject = "test";
      String result = ValidationUtils.notNull(validObject,
                                              InvalidFilmDataException::new);
      assertThat(result).isEqualTo(validObject);
    }

    @Test
    void shouldThrowException_whenObjectIsNull() {
      var exception = assertThrows(InvalidFilmDataException.class,
                                   () -> ValidationUtils.notNull(null,
                                                                 InvalidFilmDataException::new));
      assertThat(exception.getMessage()).isEqualTo("Value must not be null");
    }
  }

  @Nested
  class NotBlankTests {
    @Test
    void shouldReturnTheString_whenStringIsValid() {
      String validString = "valid";
      String result = ValidationUtils.notBlank(validString,
                                               InvalidFilmDataException::new);
      assertThat(result).isEqualTo(validString);
    }

    @Test
    void shouldReturnTheString_whenStringIsCyrillic() {
      String validString = "valid-line";
      String result = ValidationUtils.notBlank(validString,
                                               InvalidFilmDataException::new);
      assertThat(result).isEqualTo(validString);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"  ",
                            "\t",
                            "\n"
    })
    void shouldThrowException_forNullEmptyOrBlankStrings(String invalidString) {
      var exception = assertThrows(InvalidFilmDataException.class,
                                   () -> ValidationUtils.notBlank(invalidString,
                                                                  InvalidFilmDataException::new));
      assertThat(exception.getMessage()).isEqualTo("String must not be null or blank");
    }
  }

  @Nested
  class PositiveDurationTests {
    @Test
    void shouldReturnDuration_whenDurationIsPositive() {
      Duration positiveDuration = Duration.ofSeconds(1);
      Duration result = ValidationUtils.positive(positiveDuration,
                                                 InvalidFilmDataException::new);
      assertThat(result).isEqualTo(positiveDuration);
    }

    @Test
    void shouldThrowException_whenDurationIsNull() {
      var exception = assertThrows(InvalidFilmDataException.class,
                                   () -> ValidationUtils.positive(null,
                                                                  InvalidFilmDataException::new));
      assertThat(exception.getMessage()).isEqualTo("Duration must be positive");
    }

    @Test
    void shouldThrowException_whenDurationIsZero() {
      var exception = assertThrows(InvalidFilmDataException.class,
                                   () -> ValidationUtils.positive(Duration.ZERO,
                                                                  InvalidFilmDataException::new));
      assertThat(exception.getMessage()).isEqualTo("Duration must be positive");
    }

    @Test
    void shouldThrowException_whenDurationIsNegative() {
      var exception = assertThrows(InvalidFilmDataException.class,
                                   () -> ValidationUtils.positive(Duration.ofSeconds(-1),
                                                                  InvalidFilmDataException::new));
      assertThat(exception.getMessage()).isEqualTo("Duration must be positive");
    }
  }

  @Nested
  class EnsureLoginFormatTests {
    @Test
    void shouldReturnLogin_whenLoginIsValid() {
      String validLogin = "valid_login-123";
      String result = ValidationUtils.ensureLoginFormat(validLogin,
                                                        InvalidFilmDataException::new);
      assertThat(result).isEqualTo(validLogin);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"  "})
    void shouldThrowException_whenLoginIsBlank(String blankLogin) {
      assertThrows(InvalidFilmDataException.class,
                   () -> ValidationUtils.ensureLoginFormat(blankLogin,
                                                           InvalidFilmDataException::new));
    }

    @ParameterizedTest
    @ValueSource(strings = {"login space",
                            " login",
                            "login\t"
    })
    void shouldThrowException_whenLoginContainsWhitespace(String loginWithSpace) {
      var exception = assertThrows(InvalidFilmDataException.class,
                                   () -> ValidationUtils.ensureLoginFormat(loginWithSpace,
                                                                           InvalidFilmDataException::new));
      assertThat(exception.getMessage()).isEqualTo("Login must not contain spaces");
    }
  }

  @Nested
  class EnsureEmailFormatTests {
    @Test
    void shouldReturnLowercaseEmail_whenEmailIsValid() {
      String emailInCaps = "Test.User+1@Example.COM";
      String result = ValidationUtils.ensureEmailFormat(emailInCaps,
                                                        InvalidFilmDataException::new);
      assertThat(result).isEqualTo("test.user+1@example.com");
    }

    @Test
    void shouldPass_forValidComplexEmails() {
      String validEmail1 = "user.name+tag+sorting@example.org";
      String validEmail2 = "user_name@sub.domain.co.uk";

      assertThat(ValidationUtils.ensureEmailFormat(validEmail1,
                                                   InvalidFilmDataException::new)).isEqualTo(validEmail1);
      assertThat(ValidationUtils.ensureEmailFormat(validEmail2,
                                                   InvalidFilmDataException::new)).isEqualTo(validEmail2);
    }

    @Test
    void shouldPass_whenDomainLabelIsAtMaxLength() {
      String sixtyThreeChars = "a".repeat(63);
      String validEmail = "test@" + sixtyThreeChars + ".com";
      assertThat(ValidationUtils.ensureEmailFormat(validEmail,
                                                   InvalidFilmDataException::new)).isEqualTo(validEmail);
    }

    @Test
    void shouldPass_whenDomainIsNumeric() {
      String numericDomainEmail = "test@123.456";
      assertThat(ValidationUtils.ensureEmailFormat(numericDomainEmail,
                                                   InvalidFilmDataException::new)).isEqualTo(numericDomainEmail);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"  "})
    void shouldThrowException_whenEmailIsBlank(String blankEmail) {
      var exception = assertThrows(InvalidFilmDataException.class,
                                   () -> ValidationUtils.ensureEmailFormat(blankEmail,
                                                                           InvalidFilmDataException::new));
      assertThat(exception.getMessage()).isEqualTo("Email must not be null or blank.");
    }

    @Test
    void shouldThrowException_whenAtSymbolIsMissing() {
      assertThrows(InvalidFilmDataException.class,
                   () -> ValidationUtils.ensureEmailFormat("test.example.com",
                                                           InvalidFilmDataException::new));
    }

    @Test
    void shouldThrowException_whenThereAreMultipleAtSymbols() {
      assertThrows(InvalidFilmDataException.class,
                   () -> ValidationUtils.ensureEmailFormat("test@example@com",
                                                           InvalidFilmDataException::new));
    }

    @Test
    void shouldThrowException_whenLocalPartIsEmpty() {
      assertThrows(InvalidFilmDataException.class,
                   () -> ValidationUtils.ensureEmailFormat("@example.com",
                                                           InvalidFilmDataException::new));
    }

    @Test
    void shouldThrowException_whenDomainPartIsEmpty() {
      assertThrows(InvalidFilmDataException.class,
                   () -> ValidationUtils.ensureEmailFormat("test@",
                                                           InvalidFilmDataException::new));
    }

    @Test
    void shouldThrowException_whenDomainHasNoDots() {
      assertThrows(InvalidFilmDataException.class,
                   () -> ValidationUtils.ensureEmailFormat("test@example",
                                                           InvalidFilmDataException::new));
    }

    @Test
    void shouldThrowException_whenDomainLabelIsEmpty() {
      assertThrows(InvalidFilmDataException.class,
                   () -> ValidationUtils.ensureEmailFormat("test@example..com",
                                                           InvalidFilmDataException::new));
    }

    @Test
    void shouldThrowException_whenDomainLabelStartsWithHyphen() {
      assertThrows(InvalidFilmDataException.class,
                   () -> ValidationUtils.ensureEmailFormat("test@-example.com",
                                                           InvalidFilmDataException::new));
    }

    @Test
    void shouldThrowException_whenDomainLabelEndsWithHyphen() {
      assertThrows(InvalidFilmDataException.class,
                   () -> ValidationUtils.ensureEmailFormat("test@example-.com",
                                                           InvalidFilmDataException::new));
    }

    @Test
    void shouldThrowException_whenEmailIsTooLong() {
      String longString = "a".repeat(250);
      String longEmail = longString + "@example.com"; // Exceeds 254

      var exception = assertThrows(InvalidFilmDataException.class,
                                   () -> ValidationUtils.ensureEmailFormat(longEmail,
                                                                           InvalidFilmDataException::new));
      assertThat(exception.getMessage()).isEqualTo("Email length cannot exceed 254 characters.");
    }

    @Test
    void shouldThrowException_whenLocalPartIsTooLong() {
      String longLocalPart = "a".repeat(65);
      String email = longLocalPart + "@example.com";

      var exception = assertThrows(InvalidFilmDataException.class,
                                   () -> ValidationUtils.ensureEmailFormat(email,
                                                                           InvalidFilmDataException::new));
      assertThat(exception.getMessage()).isEqualTo("Email local part cannot exceed 64 characters.");
    }

    @Test
    void shouldThrowException_whenDomainLabelIsTooLong() {
      String longDomainLabel = "a".repeat(64);
      String email = "test@" + longDomainLabel + ".com";

      var exception = assertThrows(InvalidFilmDataException.class,
                                   () -> ValidationUtils.ensureEmailFormat(email,
                                                                           InvalidFilmDataException::new));
      assertThat(exception.getMessage()).isEqualTo("Invalid domain name format.");
    }
  }
}