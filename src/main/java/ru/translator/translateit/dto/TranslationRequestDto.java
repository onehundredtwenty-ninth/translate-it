package ru.translator.translateit.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TranslationRequestDto {

  @NotBlank
  private String stringToTranslate;
  @NotBlank
  @Pattern(regexp = "[a-zA-Z]*\\|[a-zA-Z]*")
  private String translationParams;
}
