package ru.translator.translateit.mapper;

import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.translator.translateit.dto.TranslationRequestDto;
import ru.translator.translateit.model.TranslationRequestEntity;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TranslationRequestMapper {

  public static TranslationRequestEntity toEntity(TranslationRequestDto dto, String ip,
      LocalDateTime requestDateTime) {
    return TranslationRequestEntity.builder()
        .stringToTranslate(dto.getStringToTranslate())
        .translationParams(dto.getTranslationParams())
        .ip(ip)
        .requestDateTime(requestDateTime)
        .build();
  }
}
