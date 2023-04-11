package ru.translator.translateit.service;

import java.util.concurrent.CompletableFuture;

public interface WordTranslationService {

  CompletableFuture<String> translateWord(String word, String translationParams, long requestId);
}
