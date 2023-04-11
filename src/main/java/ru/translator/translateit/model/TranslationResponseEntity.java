package ru.translator.translateit.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "translation_response")
public class TranslationResponseEntity {

  @Id
  @Column(name = "translation_request_id", nullable = false)
  private Long translationRequestId;
  @Column(name = "translated_string", nullable = false)
  private String translatedString;
}
