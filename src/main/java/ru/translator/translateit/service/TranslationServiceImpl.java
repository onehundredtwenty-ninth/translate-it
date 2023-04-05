package ru.translator.translateit.service;

import java.time.LocalDateTime;
import java.util.Arrays;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.translator.translateit.client.MyMemoryClient;
import ru.translator.translateit.dto.TranslationRequestDto;
import ru.translator.translateit.dto.TranslationResponseDto;
import ru.translator.translateit.mapper.TranslationRequestMapper;
import ru.translator.translateit.repository.TranslationRequestRepository;

@Service
@Transactional
@RequiredArgsConstructor
public class TranslationServiceImpl implements TranslationService {

  private final TranslationRequestRepository repository;
  private final MyMemoryClient translatorClient;

  @Override
  public TranslationResponseDto translate(TranslationRequestDto translationRequestDto, String ip,
      LocalDateTime requestDateTime) {
    var entity = TranslationRequestMapper.toEntity(translationRequestDto, ip, requestDateTime);
    repository.save(entity);

    var sourceWords = translationRequestDto.getStringToTranslate().split("\\W+");
    var translationParams = translationRequestDto.getTranslationParams().replace("/", "|");

    var translatedString = new StringBuilder();
    Arrays.stream(sourceWords).forEach(s -> {
      var response = translatorClient.sentRequestToTranslator(s, translationParams);
      translatedString.append(response.getResponseData().getTranslatedText());
    });

    return new TranslationResponseDto(translatedString.toString());
  }
}
