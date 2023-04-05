package ru.translator.translateit.controller;

import java.time.LocalDateTime;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.translator.translateit.dto.TranslationRequestDto;
import ru.translator.translateit.dto.TranslationResponseDto;
import ru.translator.translateit.service.TranslationService;

@RestController
@RequiredArgsConstructor
public class TranslationController {

  private final TranslationService translationService;

  @PostMapping("/translate")
  public TranslationResponseDto translate(@Valid @RequestBody TranslationRequestDto inputTranslationRequestDto,
      HttpServletRequest request) {
    return translationService.translate(inputTranslationRequestDto, request.getRemoteAddr(),
        LocalDateTime.now());
  }
}
