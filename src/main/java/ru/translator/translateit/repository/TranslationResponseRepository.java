package ru.translator.translateit.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.translator.translateit.model.TranslationRequestEntity;

public interface TranslationResponseRepository extends JpaRepository<TranslationRequestEntity, Long> {

}
