package ru.translator.translateit.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
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
  @OneToOne
  @JoinColumn(name = "translation_request_id", referencedColumnName = "id", nullable = false)
  private TranslationRequestEntity translationRequestEntity;
  @Column(name = "translated_string", nullable = false)
  private String translatedString;

  public TranslationResponseEntity(TranslationRequestEntity translationRequestEntity, String translatedString) {
    this.translationRequestEntity = translationRequestEntity;
    this.translatedString = translatedString;
  }
}
