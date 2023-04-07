package ru.translator.translateit.integrationservice;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.SoftAssertions;
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
class TranslationServiceIntegrationTest {

  private final EntityManager em;
  private final TranslationService translationService;
  private static final String IP = "127.0.0.1";
  private static final LocalDateTime REQUEST_DATE_TIME = LocalDateTime.now();

  static List<Arguments> commonTranslationTest() {
    return List.of(
        Arguments.arguments(
            new TranslationRequestDto("Returns a possibly parallel Stream with this collection as its.",
                "en|ru"),
            "Прибыль 6 можетбыть параллельный Трансляция при настоящим образца как свой"
        ),
        Arguments.arguments(
            new TranslationRequestDto("слово", "ru|en"),
            "word"
        ),
        Arguments.arguments(
            new TranslationRequestDto("слово, слово", "ru|en"),
            "word word"
        )
    );
  }

  @ParameterizedTest
  @MethodSource
  void commonTranslationTest(TranslationRequestDto translationRequestDto, String expectedTranslation) {
    var responseDto = translationService.translate(translationRequestDto, IP, REQUEST_DATE_TIME);
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

    SoftAssertions.assertSoftly(softAssertions -> {
      softAssertions.assertThat(requestEntity.getStringToTranslate())
          .isEqualTo(translationRequestDto.getStringToTranslate());

      softAssertions.assertThat(requestEntity.getTranslationParams())
          .isEqualTo(translationRequestDto.getTranslationParams());

      softAssertions.assertThat(requestEntity.getIp())
          .isEqualTo(IP);

      softAssertions.assertThat(requestEntity.getRequestDateTime().truncatedTo(ChronoUnit.SECONDS))
          .isEqualTo(REQUEST_DATE_TIME.truncatedTo(ChronoUnit.SECONDS));

      softAssertions.assertThat(historyList)
          .hasSize((int) Arrays.stream(translationRequestDto.getStringToTranslate().split("[\\p{IsPunctuation} ]"))
              .filter(s -> !s.isBlank()).count());

      softAssertions.assertThat(translationRequestDto.getStringToTranslate())
          .contains(historyList.stream().map(TranslationHistoryEntity::getSourceWord)
              .collect(Collectors.toList()));

      softAssertions.assertThat(responseEntity.getTranslatedString())
          .isEqualToIgnoringCase(expectedTranslation);
    });
  }
}
