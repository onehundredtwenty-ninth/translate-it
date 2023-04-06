package ru.translator.translateit.repository;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDateTime;
import java.util.List;
import javax.persistence.PersistenceException;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.SoftAssertions;
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
import ru.translator.translateit.model.TranslationResponseEntity;

@DataJpaTest
class TranslationRequestRepositoryTest {

  @Autowired
  private TestEntityManager em;
  private static TranslationRequestEntity commonEntity;

  @BeforeAll
  static void createCommonEntity() {
    commonEntity = TranslationRequestEntity.builder()
        .stringToTranslate("Hello world")
        .translationParams("en|ru")
        .ip("127.0.0.1")
        .requestDateTime(LocalDateTime.now())
        .build();
  }

  @Test
  @DisplayName("Сохранение корректного реквеста")
  void saveCorrectTranslationRequestTest() {
    var request = TranslationRequestEntity.builder()
        .stringToTranslate(commonEntity.getStringToTranslate())
        .translationParams(commonEntity.getTranslationParams())
        .ip(commonEntity.getIp())
        .requestDateTime(commonEntity.getRequestDateTime())
        .build();

    Assertions.assertThat(request.getId()).isNull();
    em.persist(request);
    Assertions.assertThat(request.getId()).isNotNull();
  }

  @Test
  @DisplayName("Сохранение корректного реквеста")
  void saveCorrectTranslationRequestWithResponseTest() {
    var request = TranslationRequestEntity.builder()
        .stringToTranslate(commonEntity.getStringToTranslate())
        .translationParams(commonEntity.getTranslationParams())
        .ip(commonEntity.getIp())
        .requestDateTime(commonEntity.getRequestDateTime())
        .build();
    em.persist(request);

    var firstWordHistoryEntity = new TranslationHistoryEntity(request.getId(), "Hello",
        "Здравствуй");
    var secondWordHistoryEntity = new TranslationHistoryEntity(request.getId(), "world",
        "мир");
    var response = new TranslationResponseEntity(request.getId(), "Здравствуй мир");
    em.persist(firstWordHistoryEntity);
    em.persist(secondWordHistoryEntity);
    em.persist(response);
    em.refresh(request);

    Assertions.assertThat(request.getTranslationResponseEntity())
        .isNotNull();
    Assertions.assertThat(request.getTranslationHistoryEntities())
        .isNotNull();

    SoftAssertions.assertSoftly(softAssertions -> {
      softAssertions.assertThat(request.getTranslationResponseEntity().getTranslatedString())
          .isEqualTo("Здравствуй мир");

      softAssertions.assertThat(request.getTranslationHistoryEntities())
          .hasSize(2);

      softAssertions.assertThat(request.getTranslationHistoryEntities().get(0).getSourceWord())
          .isEqualTo("Hello");

      softAssertions.assertThat(request.getTranslationHistoryEntities().get(0).getTranslatedWord())
          .isEqualTo("Здравствуй");
    });
  }

  static List<TranslationRequestEntity> saveIncorrectTranslationRequestTest() {
    return List.of(
        TranslationRequestEntity.builder()
            .translationParams(commonEntity.getTranslationParams())
            .ip(commonEntity.getIp())
            .requestDateTime(commonEntity.getRequestDateTime())
            .build(),
        TranslationRequestEntity.builder()
            .stringToTranslate(commonEntity.getStringToTranslate())
            .ip(commonEntity.getIp())
            .requestDateTime(commonEntity.getRequestDateTime())
            .build(),
        TranslationRequestEntity.builder()
            .stringToTranslate(commonEntity.getStringToTranslate())
            .translationParams(commonEntity.getTranslationParams())
            .requestDateTime(commonEntity.getRequestDateTime())
            .build(),
        TranslationRequestEntity.builder()
            .stringToTranslate(commonEntity.getStringToTranslate())
            .translationParams(commonEntity.getTranslationParams())
            .ip(commonEntity.getIp())
            .build(),
        TranslationRequestEntity.builder()
            .stringToTranslate("")
            .translationParams(commonEntity.getTranslationParams())
            .ip(commonEntity.getIp())
            .requestDateTime(commonEntity.getRequestDateTime())
            .build(),
        TranslationRequestEntity.builder()
            .stringToTranslate(commonEntity.getStringToTranslate())
            .translationParams("")
            .ip(commonEntity.getIp())
            .requestDateTime(commonEntity.getRequestDateTime())
            .build()
    );
  }

  @ParameterizedTest
  @DisplayName("Сохранение реквеста с нарушением Constraint-а")
  @MethodSource
  void saveIncorrectTranslationRequestTest(TranslationRequestEntity entity) {
    assertThatThrownBy(() -> em.persist(entity))
        .isInstanceOf(PersistenceException.class)
        .hasMessage("org.hibernate.exception.ConstraintViolationException: could not execute statement");
  }

  @Test
  @DisplayName("Сохранение реквеста с превышением длинны для поля ip")
  void saveTranslationRequestWithIncorrectIpTest() {
    var entity = TranslationRequestEntity.builder()
        .stringToTranslate(commonEntity.getStringToTranslate())
        .translationParams(commonEntity.getTranslationParams())
        .ip("127.127.127.127.127")
        .requestDateTime(commonEntity.getRequestDateTime())
        .build();

    assertThatThrownBy(() -> em.persist(entity))
        .isInstanceOf(PersistenceException.class)
        .hasMessage("org.hibernate.exception.DataException: could not execute statement");
  }
}
