package ru.translator.translateit.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.translator.translateit.model.TranslationHistoryEntity;

public interface TranslationHistoryRepository extends JpaRepository<TranslationHistoryEntity, Long> {

}
