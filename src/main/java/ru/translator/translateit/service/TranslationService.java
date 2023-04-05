package ru.translator.translateit.service;

import java.time.LocalDateTime;
import ru.translator.translateit.dto.TranslationRequestDto;
import ru.translator.translateit.dto.TranslationResponseDto;

public interface TranslationService {

  TranslationResponseDto translate(TranslationRequestDto translationRequestDto, String ip,
      LocalDateTime requestDateTime);
}
