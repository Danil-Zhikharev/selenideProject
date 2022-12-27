package ru.bank;


import com.codeborne.selenide.Configuration;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Keys;
import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.*;



public class FormTests {

  int days = 3;
  String date = LocalDate.now().plusDays(days).format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
  String dayOfCalendar = String.valueOf(LocalDate.now().plusDays(days).getDayOfMonth());



  @BeforeAll
  static void setUpAll() {
    WebDriverManager.chromedriver().setup();
  }

  @BeforeEach
  public void settings() {
//    Configuration.headless = true;
    Configuration.baseUrl = "http://localhost:9999";
    open(Configuration.baseUrl);
  }

  @Test
  void happyPath() {
    $x("//*[@data-test-id='city']//child::*[contains(@placeholder,'ород')]").setValue("Москва");
    $x("//*[contains(@placeholder, 'ата')]").setValue(String.valueOf(date));
    $("[name=name]").setValue("Иванов Иван");
    $("[name=phone]").setValue("+79126178980");
    $x("//*[@data-test-id='agreement']").click();
    $x("//*[contains(text(), 'абронировать')]").click();
    $(".notification__content")
        .should(appear, Duration.ofMillis(15000))
        .shouldHave(text("Встреча успешно забронирована на " + date));
  }

  void clearCity() {
    $x("//*[@data-test-id='city']//child::*[contains(@placeholder,'ород')]").hover();
    $x("//*[@data-test-id='city']//child::*[contains(@placeholder,'ород')]").sendKeys(Keys.chord(Keys.CONTROL, "a"));
    $x("//*[@data-test-id='city']//child::*[contains(@placeholder,'ород')]").sendKeys(Keys.chord(Keys.DELETE));
  }

  @Test
  void cityValidations() {
    $x("//*[@data-test-id='city']//child::*[contains(@placeholder,'ород')]").setValue("Минск");
    $x("//*[contains(text(), 'абронировать')]").click();
    $x("//*[contains(text(), 'недоступна')]").shouldHave(visible);
    clearCity();
    $x("//*[@data-test-id='city']//child::*[contains(@placeholder,'ород')]").setValue("Moscow");
    $x("//*[contains(text(), 'абронировать')]").click();
    $x("//*[contains(text(), 'недоступна')]").shouldHave(visible);
    clearCity();
    $x("//*[@data-test-id='city']//child::*[contains(@placeholder,'ород')]").setValue(";%№;%№");
    $x("//*[contains(text(), 'абронировать')]").click();
    $x("//*[contains(text(), 'недоступна')]").shouldHave(visible);
    clearCity();
    $x("//*[@data-test-id='city']//child::*[contains(@placeholder,'ород')]").setValue("12345");
    $x("//*[contains(text(), 'абронировать')]").click();
    $x("//*[contains(text(), 'недоступна')]").shouldHave(visible);
    clearCity();
    $x("//*[@data-test-id='city']//child::*[contains(@placeholder,'ород')]").setValue("ЕКАТЕРИНБУРГ");
    $x("//*[contains(text(), 'абронировать')]").click();
    $x("//*[contains(text(), 'недоступна')]").shouldHave(not(visible));
    clearCity();
    $x("//*[@data-test-id='city']//child::*[contains(@placeholder,'ород')]").hover();
    $x("//*[@data-test-id='city']//child::*[contains(@placeholder,'ород')]").sendKeys("нов");
    $x("//*[contains(text(), 'Нижний')]").hover().click();
    $x("//*[contains(text(), 'абронировать')]").click();
    $x("//*[contains(text(), 'недоступна')]").shouldHave(not(visible));
  }

  void clearDate() {
    $x("//*[contains(@placeholder, 'ата')]").hover();
    $x("//*[contains(@placeholder, 'ата')]").sendKeys(Keys.chord(Keys.CONTROL, "a"));
    $x("//*[contains(@placeholder, 'ата')]").sendKeys(Keys.chord(Keys.DELETE));
  }

  @Test
  void dateValidations () {
    $x("//*[@data-test-id='city']//child::*[contains(@placeholder,'ород')]").setValue("Москва");
    clearDate();
    $x("//*[contains(@placeholder, 'ата')]").sendKeys(LocalDate.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
    $x("//*[contains(text(), 'абронировать')]").click();
    $x("//*[contains(text(), 'невозможен')]").shouldHave(visible);
    clearDate();
    $x("//*[contains(@placeholder, 'ата')]").sendKeys(LocalDate.now().plusDays(1).format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
    $x("//*[contains(text(), 'абронировать')]").click();
    $x("//*[contains(text(), 'невозможен')]").shouldHave(visible);
    clearDate();
    $x("//*[contains(@placeholder, 'ата')]").sendKeys(LocalDate.now().plusDays(2).format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
    $x("//*[contains(text(), 'абронировать')]").click();
    $x("//*[contains(text(), 'невозможен')]").shouldHave(visible);
    clearDate();
    $x("//*[contains(@placeholder, 'ата')]").sendKeys("29.02.2023");
    $x("//*[contains(text(), 'абронировать')]").click();
    $x("//*[contains(text(), 'еверно')]").shouldHave(visible);
    clearDate();
    $x("//*[contains(@placeholder, 'ата')]").sendKeys("00.00.0000");
    $x("//*[contains(text(), 'абронировать')]").click();
    $x("//*[contains(text(), 'еверно')]").shouldHave(visible);
    clearDate();
    $x("//*[contains(@placeholder, 'ата')]/following-sibling::*/child::button").hover().click();
    $x("//*[contains(text(),'"+dayOfCalendar+"')]").hover().click();
    $x("//*[contains(@placeholder, 'ата')]").shouldHave(value(date));
  }
}
