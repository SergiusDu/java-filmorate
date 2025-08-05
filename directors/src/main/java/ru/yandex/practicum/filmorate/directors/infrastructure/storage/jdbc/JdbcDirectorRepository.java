package ru.yandex.practicum.filmorate.directors.infrastructure.storage.jdbc;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.common.enums.SortBy;
import ru.yandex.practicum.filmorate.directors.domain.factory.DirectorFactory;
import ru.yandex.practicum.filmorate.directors.domain.model.Director;
import ru.yandex.practicum.filmorate.directors.domain.port.CreateDirectorCommand;
import ru.yandex.practicum.filmorate.directors.domain.port.DirectorRepository;
import ru.yandex.practicum.filmorate.directors.domain.port.UpdateDirectorCommand;

import java.util.*;

@Slf4j
@Repository
public class JdbcDirectorRepository
    implements DirectorRepository {
  private static final int MAX_IDS_IN_CLAUSE = 1000;

  private static final String TABLE_DIRECTORS = "directors";
  private static final String COLUMN_ID = "director_id";
  private static final String COLUMN_NAME = "name";

  private static final String PARAM_DIRECTOR_IDS = "director_ids";

  private static final String FIND_ALL_SQL = """
                                             SELECT director_id, name
                                             FROM directors
                                             """;

  private static final String FIND_BY_ID_SQL = """
                                               SELECT director_id, name
                                               FROM directors
                                               WHERE director_id = :director_id
                                               """;

  private static final String FIND_ALL_BY_IDS_SQL = """
                                                    SELECT director_id, name
                                                    FROM directors
                                                    WHERE director_id IN (:director_ids)
                                                    """;

  private static final String UPDATE_DIRECTOR_SQL = """
                                                    UPDATE directors
                                                    SET name = :name
                                                    WHERE director_id = :director_id
                                                    """;

  private static final String DELETE_DIRECTOR_SQL = """
                                                    DELETE FROM directors
                                                    WHERE director_id = :director_id
                                                    """;
  private static final String ENSURE_DIRECTOR_EXIST_SQL = """
                                                          SELECT COUNT(1) FROM directors WHERE director_id = :director_id
                                                          """;
  private static final String FIND_FILMS_BY_DIRECTOR_SORT_BY_YEAR_SQL = """
                                                                        SELECT fd.film_id
                                                                        FROM film_directors AS fd
                                                                        JOIN films AS f ON fd.film_id = f.film_id
                                                                        WHERE fd.director_id = :director_id
                                                                        ORDER BY f.release_date
                                                                        """;
  private static final String FIND_FILMS_BY_DIRECTOR_SORT_BY_LIKES_SQL = """
                                                                         SELECT fd.film_id
                                                                         FROM film_directors AS fd
                                                                         LEFT JOIN likes AS l ON fd.film_id = l.film_id
                                                                         WHERE fd.director_id = :director_id
                                                                         GROUP BY fd.film_id
                                                                         ORDER BY COUNT(l.user_id) DESC
                                                                         """;
  private static final String LINK_FILM_TO_DIRECTORS_SQL = """
                                                           INSERT INTO film_directors (film_id, director_id)
                                                           VALUES (:film_id, :director_id)
                                                           """;

  private static final String DELETE_FILM_DIRECTORS_BY_FILM_ID_SQL = """
                                                                     DELETE FROM film_directors
                                                                     WHERE film_id = :film_id
                                                                     """;

  private static final String FIND_DIRECTORS_FOR_FILM_IDS_SQL = """
                                                                SELECT fd.film_id, d.director_id, d.name
                                                                FROM film_directors fd
                                                                JOIN directors d ON fd.director_id = d.director_id
                                                                WHERE fd.film_id IN (:film_ids)
                                                                ORDER BY d.director_id
                                                                """;


  private static final RowMapper<Director> rowMapper = (rs, rowNum) -> new Director(rs.getLong(COLUMN_ID),
                                                                                    rs.getString(COLUMN_NAME));
  private final NamedParameterJdbcTemplate jdbcTemplate;
  private final DirectorFactory directorFactory;
  private final SimpleJdbcInsert directorInserter;

  public JdbcDirectorRepository(NamedParameterJdbcTemplate jdbcTemplate, DirectorFactory directorFactory) {
    this.jdbcTemplate = jdbcTemplate;
    this.directorFactory = directorFactory;
    this.directorInserter = new SimpleJdbcInsert(jdbcTemplate.getJdbcTemplate()).withTableName(TABLE_DIRECTORS)
                                .usingGeneratedKeyColumns(COLUMN_ID);
  }

  @Override
  public Director save(CreateDirectorCommand command) {
    Map<String, Object> params = Map.of("name", command.name());
    long directorId = directorInserter.executeAndReturnKey(params)
                                      .longValue();
    return directorFactory.create(directorId, command);
  }

  @Override
  public Director update(UpdateDirectorCommand command) {
    MapSqlParameterSource parameters = new MapSqlParameterSource();
    parameters.addValue(COLUMN_ID, command.id());
    parameters.addValue(COLUMN_NAME, command.name());

    jdbcTemplate.update(UPDATE_DIRECTOR_SQL, parameters);

    return this.findById(command.id())
               .orElseThrow(() -> new IllegalStateException(
                   "Director with id " + command.id() + " was not found after update."));
  }

  @Override
  public boolean deleteById(long id) {
    int affectedRows = jdbcTemplate.update(DELETE_DIRECTOR_SQL, new MapSqlParameterSource(COLUMN_ID, id));
    return affectedRows == 1;
  }

  @Override
  public List<Director> findAll() {
    return jdbcTemplate.query(FIND_ALL_SQL, rowMapper);
  }

  @Override
  public Optional<Director> findById(long id) {
    return jdbcTemplate.query(FIND_BY_ID_SQL, new MapSqlParameterSource(COLUMN_ID, id), rowMapper)
                       .stream()
                       .findFirst();
  }


  @Override
  public List<Director> findByIds(Set<Long> ids) {
    if (ids == null || ids.isEmpty())
      return List.of();

    if (ids.size() > MAX_IDS_IN_CLAUSE) {
      log.warn("Requested number of IDs ({}) exceeds the recommended limit of {}.", ids.size(), MAX_IDS_IN_CLAUSE);
      throw new IllegalArgumentException("Too many IDs requested at once. Max allowed: " + MAX_IDS_IN_CLAUSE);
    }

    return jdbcTemplate.query(FIND_ALL_BY_IDS_SQL, new MapSqlParameterSource(PARAM_DIRECTOR_IDS, ids), rowMapper);
  }

  @Override
  public boolean existsById(long id) {
    MapSqlParameterSource params = new MapSqlParameterSource();
    params.addValue(COLUMN_ID, id);

    Integer count = jdbcTemplate.queryForObject(ENSURE_DIRECTOR_EXIST_SQL, params, Integer.class);

    return count != null && count > 0;
  }

  @Override
  public List<Long> findFilmIdsByDirectorId(long directorId, SortBy sortBy) {
    final String sql = (sortBy == SortBy.YEAR)
                       ? FIND_FILMS_BY_DIRECTOR_SORT_BY_YEAR_SQL
                       : FIND_FILMS_BY_DIRECTOR_SORT_BY_LIKES_SQL;
    MapSqlParameterSource params = new MapSqlParameterSource(COLUMN_ID, directorId);
    return jdbcTemplate.queryForList(sql, params, Long.class);
  }

  @Override
  public void linkFilmToDirectors(long filmId, Set<Long> directorIds) {
    if (directorIds == null || directorIds.isEmpty()) {
      return;
    }
    String sql = """
                 INSERT INTO film_directors (film_id, director_id)
                 VALUES(:film_id, :director_id)
                 """;

    List<MapSqlParameterSource> batchParams = directorIds.stream()
                                                         .map(directorId -> {
                                                           MapSqlParameterSource params = new MapSqlParameterSource();
                                                           params.addValue("film_id", filmId);
                                                           params.addValue("director_id", directorId);
                                                           return params;
                                                         })
                                                         .toList();
    jdbcTemplate.batchUpdate(sql, batchParams.toArray(new MapSqlParameterSource[0]));
  }

  @Override
  public void updateFilmDirectors(long filmId, Set<Long> directorIds) {
    MapSqlParameterSource parameters = new MapSqlParameterSource();
    parameters.addValue("film_id", filmId);

    jdbcTemplate.update(DELETE_FILM_DIRECTORS_BY_FILM_ID_SQL, parameters);
    linkFilmToDirectors(filmId, directorIds);
  }


  @Override
  public Map<Long, List<Director>> findDirectorsForFilmIds(Set<Long> filmIds) {
    if (filmIds == null || filmIds.isEmpty()) {
      return Collections.emptyMap();
    }

    Map<String, Set<Long>> params = Collections.singletonMap("film_ids", filmIds);
    Map<Long, List<Director>> directorsByFilmId = new HashMap<>();

    jdbcTemplate.query(FIND_DIRECTORS_FOR_FILM_IDS_SQL, params, (rs) -> {
      long filmId = rs.getLong("film_id");
      Director director = rowMapper.mapRow(rs, 0);
      directorsByFilmId.computeIfAbsent(filmId, k -> new ArrayList<>())
                       .add(director);
    });

    return directorsByFilmId;
  }
}
