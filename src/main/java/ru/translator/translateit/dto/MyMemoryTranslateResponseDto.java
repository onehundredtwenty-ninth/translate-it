package ru.translator.translateit.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class MyMemoryTranslateResponseDto {

  private MyMemoryResponseData responseData;
}
