package ru.translator.translateit.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "translation_history")
public class TranslationHistoryEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @ManyToOne
  @JoinColumn(name = "translation_request_id", referencedColumnName = "id", nullable = false)
  private TranslationRequestEntity translationRequestEntity;
  @Column(name = "source_word", nullable = false)
  private String sourceWord;
  @Column(name = "translated_word", nullable = false)
  private String translatedWord;

  public TranslationHistoryEntity(TranslationRequestEntity translationRequestEntity, String sourceWord,
      String translatedWord) {
    this.translationRequestEntity = translationRequestEntity;
    this.sourceWord = sourceWord;
    this.translatedWord = translatedWord;
  }
}
