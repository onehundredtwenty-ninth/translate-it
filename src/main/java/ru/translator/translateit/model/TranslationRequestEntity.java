package ru.translator.translateit.model;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "translation_request")
public class TranslationRequestEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @Column(name = "string_to_translate", nullable = false)
  private String stringToTranslate;
  @Column(name = "request_dt", nullable = false)
  private LocalDateTime requestDateTime;
  @Column(name = "translation_params", nullable = false)
  private String translationParams;
  @Column(nullable = false)
  private String ip;
}
