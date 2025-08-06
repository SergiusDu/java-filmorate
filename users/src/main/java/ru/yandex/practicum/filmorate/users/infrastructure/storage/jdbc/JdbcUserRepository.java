package ru.yandex.practicum.filmorate.users.infrastructure.storage.jdbc;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.common.exception.ResourceNotFoundException;
import ru.yandex.practicum.filmorate.users.domain.factory.UserFactory;
import ru.yandex.practicum.filmorate.users.domain.model.User;
import ru.yandex.practicum.filmorate.users.domain.model.value.Email;
import ru.yandex.practicum.filmorate.users.domain.model.value.Login;
import ru.yandex.practicum.filmorate.users.domain.port.CreateUserCommand;
import ru.yandex.practicum.filmorate.users.domain.port.UpdateUserCommand;
import ru.yandex.practicum.filmorate.users.domain.port.UserRepository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
@Profile("db")
@RequiredArgsConstructor
public class JdbcUserRepository
    implements UserRepository {

  private static final RowMapper<User> USER_ROW_MAPPER = (rs, rowNum) -> new User(rs.getLong("user_id"),
                                                                                  new Email(rs.getString("email")),
                                                                                  new Login(rs.getString("login")),
                                                                                  rs.getString("name"),
                                                                                  rs.getDate("birthday")
                                                                                    .toLocalDate());
  private final JdbcTemplate jdbcTemplate;
  private final UserFactory userFactory;

  @Override
  public User save(CreateUserCommand command) {
    SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate).withTableName("users")
                                            .usingGeneratedKeyColumns("user_id");

    Map<String, Object> params = Map.of("email", command.email(), "login", command.login(), "name", command.name(),
                                        "birthday", command.birthday());

    long userId = simpleJdbcInsert.executeAndReturnKey(params)
                                  .longValue();
    return userFactory.create(userId, command);
  }

  @Override
  public User update(UpdateUserCommand command) {
    String sql = "UPDATE users SET email = ?, login = ?, name = ?, birthday = ? WHERE user_id = ?";
    int rowsAffected = jdbcTemplate.update(sql, command.email(), command.login(), command.name(), command.birthday(),
                                           command.id());

    if (rowsAffected == 0) {
      throw new ResourceNotFoundException("User with id " + command.id() + " not found.");
    }
    return userFactory.update(command);
  }

  @Override
  public Optional<User> findById(long id) {
    String sql = "SELECT * FROM users WHERE user_id = ?";
    List<User> results = jdbcTemplate.query(sql, USER_ROW_MAPPER, id);
    return results.stream()
                  .findFirst();
  }

  @Override
  public List<User> findAll() {
    String sql = "SELECT * FROM users";
    return jdbcTemplate.query(sql, USER_ROW_MAPPER);
  }

  @Override
  public List<User> findByIds(Set<Long> ids) {
    if (ids == null || ids.isEmpty()) {
      return List.of();
    }
    String inSql = ids.stream()
                      .map(String::valueOf)
                      .collect(Collectors.joining(","));
    String sql = String.format("SELECT * FROM users WHERE user_id IN (%s)", inSql);
    return jdbcTemplate.query(sql, USER_ROW_MAPPER);
  }

  @Override
  public boolean deleteById(long userId) {
    String sql = "DELETE FROM users WHERE user_id = ?";
    return jdbcTemplate.update(sql, userId) > 0;
  }
}