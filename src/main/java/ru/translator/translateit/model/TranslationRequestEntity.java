package ru.translator.translateit.model;

import java.sql.Timestamp;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "translation_request")
public class TranslationRequestEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @Column(name = "string_to_translate", nullable = false)
  private String stringToTranslate;
  @Column(nullable = false)
  private Timestamp requestDateTime;
  @Column(name = "translation_params", nullable = false)
  private String translationParams;
  @Column(nullable = false)
  private String ip;
}
