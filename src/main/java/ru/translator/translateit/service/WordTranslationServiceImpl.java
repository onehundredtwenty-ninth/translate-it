package ru.translator.translateit.service;

import java.util.concurrent.CompletableFuture;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import ru.translator.translateit.client.MyMemoryClient;
import ru.translator.translateit.model.TranslationHistoryEntity;
import ru.translator.translateit.repository.TranslationHistoryRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class WordTranslationServiceImpl implements WordTranslationService {

  private final TranslationHistoryRepository translationHistoryRepository;
  private final MyMemoryClient translatorClient;

  @Async(value = "translationTasksExecutor")
  public CompletableFuture<String> translateWord(String word, String translationParams, long requestId) {
    var response = translatorClient.sentRequestToTranslator(word, translationParams);

    var translatedText = response.getResponseData().getTranslatedText().replaceAll("[\\p{IsPunctuation} ]", "");
    var historyEntity = new TranslationHistoryEntity(requestId, word, translatedText);
    translationHistoryRepository.save(historyEntity);
    log.info("Saved history entity: " + historyEntity.getId());

    return CompletableFuture.completedFuture(translatedText);
  }
}
