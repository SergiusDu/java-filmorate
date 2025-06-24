package ru.yandex.practicum.filmorate;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableAsync;
import ru.yandex.practicum.filmorate.common.config.AppValidationProperties;


@SpringBootApplication
@EnableConfigurationProperties(AppValidationProperties.class)
@EnableAsync
public class FilmorateApplication {
  public static void main(String[] args) {
    SpringApplication.run(FilmorateApplication.class,
                          args);
  }
}
