package ru.translator.translateit.dto;

import javax.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TranslationRequestDto {

  @NotBlank
  private String stringToTranslate;
  @NotBlank
  private String translationParams;
}
