package ru.translator.translateit.service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.stream.Collectors;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
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

@Service
@Transactional
@RequiredArgsConstructor
public class TranslationServiceImpl implements TranslationService {

  private final TranslationRequestRepository translationRequestRepository;
  private final TranslationResponseRepository translationResponseRepository;
  private final TranslationHistoryRepository translationHistoryRepository;
  private final MyMemoryClient translatorClient;

  @Override
  public TranslationResponseDto translate(TranslationRequestDto translationRequestDto, String ip,
      LocalDateTime requestDateTime) {
    var entity = TranslationRequestMapper.toEntity(translationRequestDto, ip, requestDateTime);
    translationRequestRepository.save(entity);

    var sourceWords = Arrays.stream(translationRequestDto.getStringToTranslate().split("[\\p{IsPunctuation} ]"))
        .filter(s -> !s.isBlank()).collect(Collectors.toList());
    var translationParams = translationRequestDto.getTranslationParams();

    var translatedWords = sourceWords.stream().map(s -> {
      var response = translatorClient.sentRequestToTranslator(s, translationParams);

      var translatedText = response.getResponseData().getTranslatedText().replaceAll("[\\p{IsPunctuation} ]", "");
      var historyEntity = new TranslationHistoryEntity(entity, s, translatedText);
      translationHistoryRepository.save(historyEntity);

      return translatedText;
    }).collect(Collectors.toList());

    var responseEntity = new TranslationResponseEntity(entity, String.join(" ", translatedWords));
    translationResponseRepository.save(responseEntity);

    return new TranslationResponseDto(responseEntity.getTranslatedString());
  }
}
