package ru.translator.translateit.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.translator.translateit.model.TranslationResponseEntity;

public interface TranslationResponseRepository extends JpaRepository<TranslationResponseEntity, Long> {

}
