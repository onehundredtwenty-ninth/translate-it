package ru.translator.translateit.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;

import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.translator.translateit.dto.TranslationRequestDto;
import ru.translator.translateit.model.TranslationRequestEntity;
import ru.translator.translateit.model.TranslationResponseEntity;
import ru.translator.translateit.repository.TranslationRequestRepository;
import ru.translator.translateit.repository.TranslationResponseRepository;

@ExtendWith(MockitoExtension.class)
class TranslationServiceTest {

  @Mock
  private TranslationRequestRepository translationRequestRepository;

  @Mock
  private TranslationResponseRepository translationResponseRepository;

  @Mock
  private WordTranslationService wordTranslationService;

  @Test
  void translationTest() {
    var translationService = new TranslationServiceImpl(translationRequestRepository, translationResponseRepository,
        wordTranslationService);

    var requestDto = new TranslationRequestDto("слово", "ru|en");
    var ip = "127.0.0.1";
    var requestDateTime = LocalDateTime.now();

    Mockito.when(translationRequestRepository.save(any(TranslationRequestEntity.class)))
        .thenAnswer(s -> {
          var arg = s.getArgument(0);
          var savedEntity = (TranslationRequestEntity) arg;
          savedEntity.setId(1L);
          return savedEntity;
        });
    Mockito.when(wordTranslationService.translateWord(anyString(), anyString(), anyLong()))
        .thenReturn(CompletableFuture.completedFuture("word"));
    Mockito.when(translationResponseRepository.save(any(TranslationResponseEntity.class)))
        .thenAnswer(s -> s.getArgument(0));

    var response = translationService.translate(requestDto, ip, requestDateTime);
    Assertions.assertThat(response.getTranslatedString())
        .isEqualToIgnoringCase("word");
  }
}
