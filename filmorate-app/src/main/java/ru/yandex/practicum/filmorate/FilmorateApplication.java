package ru.yandex.practicum.filmorate;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import ru.yandex.practicum.filmorate.common.config.AppValidationProperties;

@SpringBootApplication
@EnableConfigurationProperties(AppValidationProperties.class)
public class FilmorateApplication {
  public static void main(String[] args) {
    SpringApplication.run(FilmorateApplication.class,
                          args);
  }

}
