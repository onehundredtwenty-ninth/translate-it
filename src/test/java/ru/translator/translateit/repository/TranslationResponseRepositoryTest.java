package ru.translator.translateit.repository;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDateTime;
import java.util.List;
import javax.persistence.PersistenceException;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.translator.translateit.model.TranslationRequestEntity;
import ru.translator.translateit.model.TranslationResponseEntity;

@DataJpaTest
class TranslationResponseRepositoryTest {

  @Autowired
  private TestEntityManager em;
  private static TranslationRequestEntity commonRequestEntity;
  private static TranslationResponseEntity commonResponseEntity;

  @BeforeAll
  static void createCommonEntity() {
    commonRequestEntity = TranslationRequestEntity.builder()
        .stringToTranslate("Hello world")
        .translationParams("en|ru")
        .ip("127.0.0.1")
        .requestDateTime(LocalDateTime.now())
        .build();
    commonResponseEntity = new TranslationResponseEntity(1L, "Здравствуй мир");
  }

  @Test
  @DisplayName("Сохранение корректного респонса")
  void saveCorrectTranslationRequestTest() {
    var request = TranslationRequestEntity.builder()
        .stringToTranslate(commonRequestEntity.getStringToTranslate())
        .translationParams(commonRequestEntity.getTranslationParams())
        .ip(commonRequestEntity.getIp())
        .requestDateTime(commonRequestEntity.getRequestDateTime())
        .build();
    em.persist(request);

    var response = new TranslationResponseEntity(request.getId(), commonResponseEntity.getTranslatedString());
    em.persist(response);
    var refreshedResponse = em.persistFlushFind(response);

    SoftAssertions.assertSoftly(softAssertions -> {
      softAssertions.assertThat(refreshedResponse.getTranslationRequestId())
          .isEqualTo(request.getId());

      softAssertions.assertThat(refreshedResponse.getTranslatedString())
          .isEqualTo(commonResponseEntity.getTranslatedString());
    });
  }

  static List<TranslationResponseEntity> saveIncorrectTranslationResponseTest() {
    return List.of(
        new TranslationResponseEntity(commonResponseEntity.getTranslationRequestId(), null),
        new TranslationResponseEntity(commonResponseEntity.getTranslationRequestId(), ""),
        new TranslationResponseEntity(1000L, commonResponseEntity.getTranslatedString())
    );
  }

  @ParameterizedTest
  @DisplayName("Сохранение респонса с нарушением Constraint-а")
  @MethodSource
  void saveIncorrectTranslationResponseTest(TranslationResponseEntity entity) {
    assertThatThrownBy(() -> em.persistAndFlush(entity))
        .isInstanceOf(PersistenceException.class)
        .hasMessage("org.hibernate.exception.ConstraintViolationException: could not execute statement");
  }
}
