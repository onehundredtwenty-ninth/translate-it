package ru.translator.translateit.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.translator.translateit.client.MyMemoryClient;
import ru.translator.translateit.dto.MyMemoryResponseData;
import ru.translator.translateit.dto.MyMemoryTranslateResponseDto;
import ru.translator.translateit.model.TranslationHistoryEntity;
import ru.translator.translateit.repository.TranslationHistoryRepository;

@ExtendWith(MockitoExtension.class)
class WordTranslationServiceTest {

  @Mock
  private TranslationHistoryRepository translationHistoryRepository;

  @Mock
  private MyMemoryClient translatorClient;

  @Test
  void wordTranslationTest() {
    var wordTranslationService = new WordTranslationServiceImpl(translationHistoryRepository, translatorClient);

    var responseData = new MyMemoryResponseData();
    responseData.setTranslatedText("мир");
    responseData.setMatch(1);
    var dto = new MyMemoryTranslateResponseDto();
    dto.setResponseData(responseData);

    Mockito.when(translatorClient.sentRequestToTranslator(anyString(), anyString())).thenReturn(dto);
    Mockito.when(translationHistoryRepository.save(any(TranslationHistoryEntity.class)))
        .thenAnswer(s -> {
          var arg = s.getArgument(0);
          var savedEntity = (TranslationHistoryEntity) arg;
          savedEntity.setId(1L);
          return savedEntity;
        });

    var serviceResponse = wordTranslationService.translateWord("word", "en|ru", 1L);
    var translatedWord = serviceResponse.join();
    Assertions.assertThat(translatedWord)
        .isEqualToIgnoringCase("мир");
  }
}
