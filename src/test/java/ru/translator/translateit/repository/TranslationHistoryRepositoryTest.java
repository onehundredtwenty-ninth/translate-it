package ru.translator.translateit.repository;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDateTime;
import java.util.List;
import javax.persistence.PersistenceException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.translator.translateit.model.TranslationHistoryEntity;
import ru.translator.translateit.model.TranslationRequestEntity;

@DataJpaTest
@DisplayName("Smoke репозитория TranslationHistoryRepository")
class TranslationHistoryRepositoryTest {

  @Autowired
  private TestEntityManager em;
  private static TranslationRequestEntity commonRequestEntity;
  private static TranslationHistoryEntity commonHistoryEntity;

  @BeforeAll
  static void createCommonEntity() {
    commonRequestEntity = TranslationRequestEntity.builder()
        .stringToTranslate("Hello world")
        .translationParams("en|ru")
        .ip("127.0.0.1")
        .requestDateTime(LocalDateTime.now())
        .build();
    commonHistoryEntity = new TranslationHistoryEntity(1L, "word", "слово");
  }

  @Test
  @DisplayName("Сохранение корректного перевода слова")
  void saveCorrectTranslationHistoryTest() {
    var request = TranslationRequestEntity.builder()
        .stringToTranslate(commonRequestEntity.getStringToTranslate())
        .translationParams(commonRequestEntity.getTranslationParams())
        .ip(commonRequestEntity.getIp())
        .requestDateTime(commonRequestEntity.getRequestDateTime())
        .build();
    em.persist(request);

    var historyEntity = new TranslationHistoryEntity(request.getId(), commonHistoryEntity.getSourceWord(),
        commonHistoryEntity.getTranslatedWord());
    Assertions.assertThat(historyEntity.getId()).isNull();
    em.persist(historyEntity);
    Assertions.assertThat(historyEntity.getId()).isNotNull();
  }

  static List<TranslationHistoryEntity> saveIncorrectTranslationHistoryTest() {
    return List.of(
        new TranslationHistoryEntity(commonHistoryEntity.getTranslationRequestId(), commonHistoryEntity.getSourceWord(),
            null),
        new TranslationHistoryEntity(commonHistoryEntity.getTranslationRequestId(), null,
            commonHistoryEntity.getTranslatedWord()),
        new TranslationHistoryEntity(commonHistoryEntity.getTranslationRequestId(), commonHistoryEntity.getSourceWord(),
            ""),
        new TranslationHistoryEntity(commonHistoryEntity.getTranslationRequestId(), "",
            commonHistoryEntity.getTranslatedWord()),
        new TranslationHistoryEntity(10L, commonHistoryEntity.getSourceWord(),
            commonHistoryEntity.getTranslatedWord())
    );
  }

  @ParameterizedTest
  @DisplayName("Сохранение перевода слова с нарушением Constraint-а")
  @MethodSource
  void saveIncorrectTranslationHistoryTest(TranslationHistoryEntity entity) {
    assertThatThrownBy(() -> em.persist(entity))
        .isInstanceOf(PersistenceException.class)
        .hasMessage("org.hibernate.exception.ConstraintViolationException: could not execute statement");
  }
}
