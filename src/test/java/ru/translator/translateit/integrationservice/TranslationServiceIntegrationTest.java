package ru.translator.translateit.integrationservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.translator.translateit.dto.TranslationRequestDto;
import ru.translator.translateit.model.TranslationHistoryEntity;
import ru.translator.translateit.model.TranslationRequestEntity;
import ru.translator.translateit.model.TranslationResponseEntity;
import ru.translator.translateit.service.TranslationService;

@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DisplayName("Smoke интеграция сервисы")
class TranslationServiceIntegrationTest {

  private final EntityManager em;
  private final TranslationService translationService;

  @SneakyThrows
  static List<Arguments> commonTranslationTest() {
    var mapper = new ObjectMapper();
    var testDataPath = Path.of(
        System.getProperty("user.dir"),
        "src/test/java/ru/translator/translateit/integrationservice/TranslationServiceIntegrationTestData.json"
    ).toFile();
    var testData = mapper.readValue(testDataPath, TranslationServiceIntegrationTestData[].class);

    return Arrays.stream(testData)
        .map(s -> Arguments.arguments(s.getTranslationRequestDto(), s.getIp(), s.getExpectedTranslation()))
        .collect(Collectors.toList());
  }

  @ParameterizedTest
  @MethodSource
  @DisplayName("Перевод корректных запросов")
  void commonTranslationTest(TranslationRequestDto translationRequestDto, String ip, String expectedTranslation) {
    var requestDateTime = LocalDateTime.now();
    var responseDto = translationService.translate(translationRequestDto, ip, requestDateTime);
    Assertions.assertThat(responseDto.getTranslatedString())
        .isEqualToIgnoringCase(expectedTranslation);

    var requestEntity = em.createQuery(
            "select r from TranslationRequestEntity r where r.stringToTranslate = :stringToTranslate",
            TranslationRequestEntity.class)
        .setParameter("stringToTranslate", translationRequestDto.getStringToTranslate())
        .getSingleResult();

    var historyList = em.createQuery(
            "select h from TranslationHistoryEntity h where h.translationRequestId = :translationRequestId",
            TranslationHistoryEntity.class)
        .setParameter("translationRequestId", requestEntity.getId())
        .getResultList();

    var responseEntity = em.find(TranslationResponseEntity.class, requestEntity.getId());
    var expectedHistorySize = (int) Arrays
        .stream(translationRequestDto.getStringToTranslate().split("[\\p{IsPunctuation} ]"))
        .filter(s -> !s.isBlank()).count();
    var expectedSourceWords = historyList.stream().map(TranslationHistoryEntity::getSourceWord)
        .collect(Collectors.toList());

    SoftAssertions.assertSoftly(softAssertions -> {
      softAssertions.assertThat(requestEntity.getStringToTranslate())
          .isEqualTo(translationRequestDto.getStringToTranslate());

      softAssertions.assertThat(requestEntity.getTranslationParams())
          .isEqualTo(translationRequestDto.getTranslationParams());

      softAssertions.assertThat(requestEntity.getIp())
          .isEqualTo(ip);

      softAssertions.assertThat(requestEntity.getRequestDateTime().truncatedTo(ChronoUnit.SECONDS))
          .isEqualTo(requestDateTime.truncatedTo(ChronoUnit.SECONDS));

      softAssertions.assertThat(historyList)
          .hasSize(expectedHistorySize);

      softAssertions.assertThat(translationRequestDto.getStringToTranslate())
          .contains(expectedSourceWords);

      softAssertions.assertThat(responseEntity.getTranslatedString())
          .isEqualToIgnoringCase(expectedTranslation);
    });
  }
}
