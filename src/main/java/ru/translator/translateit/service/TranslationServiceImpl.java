package ru.translator.translateit.service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.translator.translateit.client.MyMemoryClient;
import ru.translator.translateit.dto.TranslationRequestDto;
import ru.translator.translateit.dto.TranslationResponseDto;
import ru.translator.translateit.mapper.TranslationRequestMapper;
import ru.translator.translateit.model.TranslationHistoryEntity;
import ru.translator.translateit.model.TranslationResponseEntity;
import ru.translator.translateit.repository.TranslationHistoryRepository;
import ru.translator.translateit.repository.TranslationRequestRepository;
import ru.translator.translateit.repository.TranslationResponseRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class TranslationServiceImpl implements TranslationService {

  private final TranslationRequestRepository translationRequestRepository;
  private final TranslationResponseRepository translationResponseRepository;
  private final TranslationHistoryRepository translationHistoryRepository;
  private final MyMemoryClient translatorClient;

  @Override
  public TranslationResponseDto translate(TranslationRequestDto translationRequestDto, String ip,
      LocalDateTime requestDateTime) {
    var translationRequestEntity = TranslationRequestMapper.toEntity(translationRequestDto, ip, requestDateTime);
    translationRequestRepository.save(translationRequestEntity);

    var sourceWords = Arrays.stream(translationRequestDto.getStringToTranslate().split("[\\p{IsPunctuation} ]"))
        .filter(s -> !s.isBlank()).collect(Collectors.toList());
    var translationParams = translationRequestDto.getTranslationParams();

    var forkJoinPool = new ForkJoinPool(10);
    var translatedWords = CompletableFuture.supplyAsync(() ->
            sourceWords.parallelStream()
                .map(s -> translateWord(s, translationParams, translationRequestEntity.getId()))
                .collect(Collectors.toList()),
        forkJoinPool
    ).join();
    forkJoinPool.shutdown();

    var responseEntity = new TranslationResponseEntity(translationRequestEntity.getId(),
        String.join(" ", translatedWords));
    translationResponseRepository.save(responseEntity);

    return new TranslationResponseDto(responseEntity.getTranslatedString());
  }

  private String translateWord(String word, String translationParams, long requestId) {
    var response = translatorClient.sentRequestToTranslator(word, translationParams);

    var translatedText = response.getResponseData().getTranslatedText().replaceAll("[\\p{IsPunctuation} ]", "");
    var historyEntity = new TranslationHistoryEntity(requestId, word, translatedText);
    translationHistoryRepository.save(historyEntity);
    log.info("Saved history entity: " + historyEntity.getId());

    return translatedText;
  }
}
