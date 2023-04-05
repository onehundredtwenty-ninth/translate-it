package ru.translator.translateit.service;

import java.time.LocalDateTime;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.translator.translateit.dto.TranslationRequestDto;
import ru.translator.translateit.dto.TranslationResponseDto;
import ru.translator.translateit.repository.TranslationRequestRepository;

@Service
@Transactional
@RequiredArgsConstructor
public class TranslationServiceImpl implements TranslationService {

  private final TranslationRequestRepository repository;

  @Override
  public TranslationResponseDto translate(TranslationRequestDto translationRequestDto, String ip,
      LocalDateTime requestDateTime) {
    return null;
  }
}
