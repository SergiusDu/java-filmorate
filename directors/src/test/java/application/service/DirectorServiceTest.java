package application.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.yandex.practicum.filmorate.common.exception.ResourceNotFoundException;
import ru.yandex.practicum.filmorate.directors.application.service.DirectorService;
import ru.yandex.practicum.filmorate.directors.domain.model.Director;
import ru.yandex.practicum.filmorate.directors.domain.port.CreateDirectorCommand;
import ru.yandex.practicum.filmorate.directors.domain.port.DirectorRepository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Unit tests for DirectorService")
class DirectorServiceTest {

  @Mock
  private DirectorRepository mockDirectorRepository;

  @InjectMocks
  private DirectorService directorService;

  @Nested
  @DisplayName("Create Director Tests")
  class CreateDirectorTests {

    @Test
    @DisplayName("Should create a director by calling repository's save method")
    void shouldCreateDirector() {
      CreateDirectorCommand command = new CreateDirectorCommand("Guillermo del Toro");
      Director expectedDirector = new Director(1L, "Guillermo del Toro");

      when(mockDirectorRepository.save(command)).thenReturn(expectedDirector);

      Director actualDirector = directorService.createDirector(command);

      assertThat(actualDirector).isEqualTo(expectedDirector);
      verify(mockDirectorRepository).save(command);
    }

    @Test
    @DisplayName("Should return null if repository returns null on save")
    void shouldReturnNull_whenRepositoryReturnsNull() {
      CreateDirectorCommand command = new CreateDirectorCommand("Some Director");

      when(mockDirectorRepository.save(command)).thenReturn(null);

      Director actualDirector = directorService.createDirector(command);

      assertThat(actualDirector).isNull();
    }
  }


  @Nested
  @DisplayName("Find Director By ID Tests")
  class FindDirectorByIdTests {

    @Test
    @DisplayName("Should return a director when ID exists")
    void shouldFindDirectorById_whenExists() {
      long directorId = 1L;
      Director expectedDirector = new Director(directorId, "Bong Joon-ho");

      when(mockDirectorRepository.findById(directorId)).thenReturn(Optional.of(expectedDirector));

      Director actualDirector = directorService.findDirectorById(directorId);

      assertThat(actualDirector).isEqualTo(expectedDirector);
    }

    @Test
    @DisplayName("Should return empty Optional when ID does not exist")
    void shouldReturnEmpty_whenIdDoesNotExist() {
      long directorId = 99L;
      when(mockDirectorRepository.findById(directorId)).thenReturn(Optional.empty());

      assertThatThrownBy(() -> directorService.findDirectorById(directorId))
          .isInstanceOf(ResourceNotFoundException.class)
          .hasMessage("Director with ID " + directorId + " not found");
    }
  }

  @Nested
  @DisplayName("Find Directors By IDs Tests")
  class FindDirectorsByIdsTests {

    @Test
    @DisplayName("Should return list of directors for existing IDs")
    void shouldFindDirectorsByIds() {
      Set<Long> ids = Set.of(1L, 2L);
      List<Director> expectedDirectors = List.of(new Director(1L, "Greta Gerwig"), new Director(2L, "Sofia Coppola"));
      when(mockDirectorRepository.findByIds(ids)).thenReturn(expectedDirectors);

      List<Director> actualDirectors = directorService.findDirectorsByIds(ids);

      assertThat(actualDirectors)
          .hasSize(2)
          .containsAll(expectedDirectors);
      verify(mockDirectorRepository).findByIds(ids);
    }

    @Test
    @DisplayName("Should return an empty list when given an empty set of IDs")
    void shouldReturnEmptyList_whenIdsSetIsEmpty() {
      List<Director> actualDirectors = directorService.findDirectorsByIds(Collections.emptySet());

      assertThat(actualDirectors).isEmpty();
    }

    @Test
    @DisplayName("Should return an empty list when given a null set of IDs")
    void shouldReturnEmptyList_whenIdsSetIsNull() {
      List<Director> actualDirectors = directorService.findDirectorsByIds(null);

      assertThat(actualDirectors).isEmpty();
    }

    @Test
    @DisplayName("Should return a partial list if repository returns a partial list")
    void shouldReturnPartialList_whenRepositoryFindsPartial() {
      Set<Long> ids = Set.of(1L, 99L);
      List<Director> partialList = List.of(new Director(1L, "Jordan Peele"));

      when(mockDirectorRepository.findByIds(ids)).thenReturn(partialList);

      List<Director> actualDirectors = directorService.findDirectorsByIds(ids);

      assertThat(actualDirectors)
          .hasSize(1)
          .isEqualTo(partialList);
    }
  }

  @Nested
  @DisplayName("Delete Director Tests")
  class DeleteDirectorTests {


    @Test
    @DisplayName("Should propagate exception from repository on delete")
    void shouldPropagateExceptionOnDelete() {
      long directorId = 99L;
      doThrow(new ResourceNotFoundException("Director 99 not found to be deleted"))
          .when(mockDirectorRepository)
          .deleteById(directorId);

      assertThatThrownBy(() -> directorService.deleteDirectorById(directorId))
          .isInstanceOf(ResourceNotFoundException.class)
          .hasMessage("Director 99 not found to be deleted");
    }
  }
}