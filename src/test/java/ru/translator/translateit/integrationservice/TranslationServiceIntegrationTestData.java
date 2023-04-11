package ru.translator.translateit.integrationservice;

import lombok.Getter;
import lombok.Setter;
import ru.translator.translateit.dto.TranslationRequestDto;

@Getter
@Setter
public class TranslationServiceIntegrationTestData {

  private TranslationRequestDto translationRequestDto;
  private String ip;
  private String expectedTranslation;
}
