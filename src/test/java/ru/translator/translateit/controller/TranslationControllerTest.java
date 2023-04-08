package ru.translator.translateit.controller;

import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.translator.translateit.commonhandler.ErrorResponse;
import ru.translator.translateit.dto.TranslationRequestDto;
import ru.translator.translateit.dto.TranslationResponseDto;
import ru.translator.translateit.service.TranslationService;

@WebMvcTest(controllers = TranslationController.class)
@DisplayName("Smoke для контроллера TranslationController")
class TranslationControllerTest {

  @Autowired
  private ObjectMapper mapper;

  @MockBean
  private TranslationService translationService;

  @Autowired
  private MockMvc mvc;

  @Test
  @DisplayName("Отправка корректного запроса")
  void translateTest() throws Exception {
    var responseDto = new TranslationResponseDto("word");
    var requestDto = new TranslationRequestDto("слово", "ru|en");
    when(translationService.translate(any(), anyString(), any())).thenReturn(responseDto);

    var response = mvc.perform(post("/translate")
            .content(mapper.writeValueAsString(requestDto))
            .characterEncoding(StandardCharsets.UTF_8)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andReturn().getResponse();

    var responseObject = mapper.readValue(response.getContentAsString(), TranslationResponseDto.class);
    assertSoftly(softAssertions ->
        softAssertions.assertThat(responseObject)
            .usingRecursiveComparison()
            .isEqualTo(responseDto));
  }

  @Test
  @DisplayName("Отправка запроса с некорректным значением в translationParams")
  void requestTranslationWithIIncorrectTranslationParamsTest() throws Exception {
    var requestDto = new TranslationRequestDto("слово", "ru/en");

    var response = mvc.perform(post("/translate")
            .content(mapper.writeValueAsString(requestDto))
            .characterEncoding(StandardCharsets.UTF_8)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().is4xxClientError())
        .andReturn().getResponse();

    var responseObject = mapper.readValue(response.getContentAsString(), ErrorResponse.class);
    assertSoftly(softAssertions -> {
      softAssertions.assertThat(responseObject.getReason())
          .contains("Incorrectly made request.");

      softAssertions.assertThat(responseObject.getMessage())
          .contains("message [must match \"[a-zA-Z]*\\|[a-zA-Z]*\"]]");

      softAssertions.assertThat(responseObject.getTimestamp().toLocalDate())
          .isEqualTo(LocalDateTime.now().toLocalDate());
    });
  }

  @Test
  @DisplayName("Отправка запроса с некорректным значением в stringToTranslate")
  void requestTranslationWithIEmptyStringToTranslateTest() throws Exception {
    var requestDto = new TranslationRequestDto("", "ru|en");

    var response = mvc.perform(post("/translate")
            .content(mapper.writeValueAsString(requestDto))
            .characterEncoding(StandardCharsets.UTF_8)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().is4xxClientError())
        .andReturn().getResponse();

    var responseObject = mapper.readValue(response.getContentAsString(), ErrorResponse.class);
    assertSoftly(softAssertions -> {
      softAssertions.assertThat(responseObject.getReason())
          .contains("Incorrectly made request.");

      softAssertions.assertThat(responseObject.getMessage())
          .contains("message [must not be blank]");

      softAssertions.assertThat(responseObject.getTimestamp().toLocalDate())
          .isEqualTo(LocalDateTime.now().toLocalDate());
    });
  }

  @Test
  @DisplayName("Ответ контроллера при IllegalArgumentException в сервисе")
  void requestTranslationWithServiceExceptionTest() throws Exception {
    var requestDto = new TranslationRequestDto("слово", "ru|en");
    when(translationService.translate(any(), anyString(), any()))
        .thenThrow(new IllegalArgumentException("test IllegalArgumentException"));

    var response = mvc.perform(post("/translate")
            .content(mapper.writeValueAsString(requestDto))
            .characterEncoding(StandardCharsets.UTF_8)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().is4xxClientError())
        .andReturn().getResponse();

    var responseObject = mapper.readValue(response.getContentAsString(), ErrorResponse.class);
    assertSoftly(softAssertions -> {
      softAssertions.assertThat(responseObject.getReason())
          .contains("Incorrectly made request.");

      softAssertions.assertThat(responseObject.getMessage())
          .isEqualTo("test IllegalArgumentException");

      softAssertions.assertThat(responseObject.getTimestamp().toLocalDate())
          .isEqualTo(LocalDateTime.now().toLocalDate());
    });
  }
}
