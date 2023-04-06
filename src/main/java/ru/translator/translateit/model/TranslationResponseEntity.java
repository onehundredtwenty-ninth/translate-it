package ru.translator.translateit.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "translation_response")
public class TranslationResponseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @Column(name = "translation_request_id", nullable = false)
  private Long translationRequestId;
  @Column(name = "translated_string", nullable = false)
  private String translatedString;

  public TranslationResponseEntity(long translationRequestId, String translatedString) {
    this.translationRequestId = translationRequestId;
    this.translatedString = translatedString;
  }
}
