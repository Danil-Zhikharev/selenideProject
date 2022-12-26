package ru.bank;

import com.codeborne.selenide.Configuration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import static com.codeborne.selenide.Condition.appear;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.*;

public class FormTests {

  String date = LocalDate.now().plusDays(3).format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));

  @BeforeEach
  void settings() {
//    Configuration.headless = true;
    Configuration.baseUrl = "http://localhost:9999";
    open(Configuration.baseUrl);
  }

  @Test
  void happyPath() {
    $x("//*[@data-test-id='city']//child::*[@placeholder='Город']").setValue("Москва");
    $x("//*[@placeholder='Дата встречи']").setValue(String.valueOf(date));
    $("[name=name]").setValue("Иванов Иван");
    $("[name=phone]").setValue("+79126178980");
    $x("//*[@data-test-id='agreement']").click();
    $x("//*[contains(text(), 'Забронировать')]").click();
    $(".notification__content").should(appear, Duration.ofMillis(15000)).shouldHave(text("Встреча успешно забронирована на " + date));
  }
}
