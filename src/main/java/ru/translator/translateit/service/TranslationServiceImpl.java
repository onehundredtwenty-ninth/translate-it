package ru.translator.translateit.service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.translator.translateit.dto.TranslationRequestDto;
import ru.translator.translateit.dto.TranslationResponseDto;
import ru.translator.translateit.mapper.TranslationRequestMapper;
import ru.translator.translateit.model.TranslationResponseEntity;
import ru.translator.translateit.repository.TranslationRequestRepository;
import ru.translator.translateit.repository.TranslationResponseRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class TranslationServiceImpl implements TranslationService {

  private final TranslationRequestRepository translationRequestRepository;
  private final TranslationResponseRepository translationResponseRepository;
  private final WordTranslationService wordTranslationService;

  @Override
  public TranslationResponseDto translate(TranslationRequestDto translationRequestDto, String ip,
      LocalDateTime requestDateTime) {
    var translationRequestEntity = TranslationRequestMapper.toEntity(translationRequestDto, ip, requestDateTime);
    translationRequestRepository.save(translationRequestEntity);

    var sourceWords = Arrays.stream(translationRequestDto.getStringToTranslate().split("[\\p{IsPunctuation} ]"))
        .filter(s -> !s.isBlank()).collect(Collectors.toList());
    var translationParams = translationRequestDto.getTranslationParams();

    var translatedWords = sourceWords.parallelStream()
        .map(s -> wordTranslationService.translateWord(s, translationParams, translationRequestEntity.getId()).join())
        .collect(Collectors.toList());

    var responseEntity = new TranslationResponseEntity(translationRequestEntity.getId(),
        String.join(" ", translatedWords));
    translationResponseRepository.save(responseEntity);

    return new TranslationResponseDto(responseEntity.getTranslatedString());
  }
}
