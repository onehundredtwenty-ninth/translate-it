package ru.translator.translateit.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MyMemoryResponseData {

  private String translatedText;
  private Integer match;
}
