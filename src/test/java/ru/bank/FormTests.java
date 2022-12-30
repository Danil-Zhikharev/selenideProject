package ru.bank;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.WebDriverRunner;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;

import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.*;

public class FormTests {

  int days = 3;
  String date = LocalDate.now().plusDays(days).format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
  String dayOfCalendar = String.valueOf(LocalDate.now().plusDays(days).getDayOfMonth());
  String specialDay = LocalDate.now().plusDays(7).format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
  String specialDate = String.valueOf(LocalDate.now().plusDays(7).getDayOfMonth());

  List<WebElement> inputList =
      WebDriverRunner.getWebDriver().findElements(By.className("input__control"));

  @BeforeAll
  static void openUrl() {
    Configuration.baseUrl = "http://localhost:9999";
    open(Configuration.baseUrl);
  }

  @Test
  void happyPath() {
    inputList.get(0).sendKeys("Пенза");
    inputList.get(1).sendKeys(date);
    inputList.get(2).sendKeys("Иванов Иван");
    inputList.get(3).sendKeys("+79126178980");
    $x("//*[@data-test-id='agreement']").click();
    $(".button").click();
    $(".notification__content")
        .should(appear, Duration.ofMillis(15000))
        .shouldHave(text("Встреча успешно забронирована на " + date));
    clearAll();
    $x("//*[@data-test-id='agreement']").click();
  }

  void clearAll() {
    clearCity();
    clearDate();
    clearName();
    clearPhone();
  }

  void lookingFor() {
    $(".input__icon").hover().click();
    if ($x("//*[text()='" + specialDate + "']").isDisplayed()) {
      $x("//*[text()='" + specialDate + "']").hover().click();
    } else {
      $x("//*[@data-step=1]").click();
      $x("//*[text()='" + specialDate + "']").hover().click();
      $(inputList.get(0)).shouldHave(value(specialDay));
    }
  }

  @Test
  void specialDate() {
    inputList.get(0).sendKeys("но");
    $x("//*[contains(text(), 'Нижний')]").hover().click();
    clearDate();
    lookingFor();
    clearAll();
  }

  void clearCity() {
    $(inputList.get(0)).hover();
    $(inputList.get(0)).sendKeys(Keys.chord(Keys.CONTROL, "a"));
    $(inputList.get(0)).sendKeys(Keys.chord(Keys.DELETE));
  }

  @Test
  void cityValidations() {
    $(inputList.get(0)).setValue("Минск");
    $(".button").click();
    $x("//*[contains(text(), 'недоступна')]").shouldHave(visible);
    clearCity();
    $(inputList.get(0)).setValue("Moscow");
    $(".button").click();
    $x("//*[contains(text(), 'недоступна')]").shouldHave(visible);
    clearCity();
    $(inputList.get(0)).setValue(";%№;%№");
    $(".button").click();
    $x("//*[contains(text(), 'недоступна')]").shouldHave(visible);
    clearCity();
    $(inputList.get(0)).setValue("12345");
    $(".button").click();
    $x("//*[contains(text(), 'недоступна')]").shouldHave(visible);
    clearCity();
    $(inputList.get(0)).setValue("ЕКАТЕРИНБУРГ");
    $(".button").click();
    $x("//*[contains(text(), 'недоступна')]").shouldHave(not(visible));
    clearCity();
    $(inputList.get(0)).hover();
    $(inputList.get(0)).sendKeys("но");
    $x("//*[contains(text(), 'Нижний')]").hover().click();
    $(".button").click();
    $x("//*[contains(text(), 'недоступна')]").shouldHave(not(visible));
    clearCity();
    $(".button").click();
    $x("//*[contains(text(), 'обязательно')]").shouldHave(visible);
    clearAll();
  }

  void clearDate() {
    $(inputList.get(1)).hover();
    $(inputList.get(1)).sendKeys(Keys.chord(Keys.CONTROL, "a"));
    $(inputList.get(1)).sendKeys(Keys.chord(Keys.DELETE));
  }

  @Test
  void dateValidations() {
    $(inputList.get(0)).setValue("Москва");
    clearDate();
    $(inputList.get(1)).sendKeys(LocalDate.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
    $(".button").click();
    $x("//*[contains(text(), 'невозможен')]").shouldHave(visible);
    clearDate();
    $(inputList.get(1))
        .sendKeys(LocalDate.now().plusDays(1).format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
    $(".button").click();
    $x("//*[contains(text(), 'невозможен')]").shouldHave(visible);
    clearDate();
    $(inputList.get(1))
        .sendKeys(LocalDate.now().plusDays(2).format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
    $(".button").click();
    $x("//*[contains(text(), 'невозможен')]").shouldHave(visible);
    clearDate();
    $(inputList.get(1)).sendKeys("29.02.2023");
    $(".button").click();
    $x("//*[contains(text(), 'еверно')]").shouldHave(visible);
    clearDate();
    $(inputList.get(1)).sendKeys("00.00.0000");
    $(".button").click();
    $x("//*[contains(text(), 'еверно')]").shouldHave(visible);
    clearDate();
    $(inputList.get(1)).hover().click();
    $x("//*[text()='" + dayOfCalendar + "']").hover().click();
    $(inputList.get(1)).shouldHave(value(date));
    clearDate();
    $(".button").click();
    $x("//*[contains(text(), 'ыберите')]").shouldHave(visible);
    clearAll();
  }

  void clearName() {
    $("[name=name]").hover();
    $("[name=name]").sendKeys(Keys.chord(Keys.CONTROL, "a"));
    $("[name=name]").sendKeys(Keys.chord(Keys.DELETE));
  }

  @Test
  void nameValidations() {
    $(inputList.get(0)).setValue("Москва");
    $(inputList.get(1)).sendKeys(date);
    $("[name=name]").setValue("John Smith");
    $(".button").click();
    $x("//*[contains(text(), 'неверно')]").shouldHave(visible);
    clearName();
    $("[name=name]").setValue("Иван-Петр Сидоров-Петров");
    $(".button").click();
    $x("//*[contains(text(), 'неверно')]").shouldHave(not(visible));
    clearName();
    $("[name=name]").setValue("Иван Сергеевич Петров");
    $(".button").click();
    $x("//*[contains(text(), 'неверно')]").shouldHave(not(visible));
    clearName();
    $("[name=name]").setValue("Иван   Петров");
    $(".button").click();
    $x("//*[contains(text(), 'неверно')]").shouldHave(not(visible));
    clearName();
    $("[name=name]").setValue("ИВАН ПЕТРОВ");
    $(".button").click();
    $x("//*[contains(text(), 'неверно')]").shouldHave(not(visible));
    clearName();
    $("[name=name]").setValue("Иван Сидоров-Петров");
    $(".button").click();
    $x("//*[contains(text(), 'неверно')]").shouldHave(not(visible));
    clearName();
    $(".button").click();
    $x("//*[contains(text(), 'обязательно')]").shouldHave(visible);
    clearAll();
  }

  void clearPhone() {
    $("[name=phone]").hover();
    $("[name=phone]").sendKeys(Keys.chord(Keys.CONTROL, "a"));
    $("[name=phone]").sendKeys(Keys.chord(Keys.DELETE));
  }

  @Test
  void phoneValidations() {
    $(inputList.get(0)).setValue("Москва");
    $(inputList.get(1)).sendKeys(date);
    $("[name=name]").setValue("Иванов Иван");
    $("[name=phone]").setValue("+7 912 617 89 80");
    $(".button").click();
    $x("//*[contains(text(), 'неверно')]").shouldHave(visible);
    clearPhone();
    $("[name=phone]").setValue("+39126178980");
    $(".button").click();
    $x("//*[contains(text(), 'неверно')]").shouldHave(not(visible));
    clearPhone();
    $("[name=phone]").setValue("+3(912)6178980");
    $(".button").click();
    $x("//*[contains(text(), 'неверно')]").shouldHave(visible);
    clearPhone();
    $("[name=phone]").setValue("89126178980");
    $(".button").click();
    $x("//*[contains(text(), 'неверно')]").shouldHave(visible);
    clearPhone();
    $("[name=phone]").setValue("891261789800");
    $(".button").click();
    $x("//*[contains(text(), 'неверно')]").shouldHave(visible);
    clearPhone();
    $("[name=phone]").setValue("9126178980");
    $(".button").click();
    $x("//*[contains(text(), 'неверно')]").shouldHave(visible);
    clearPhone();
    $("[name=phone]").setValue("-79126178980");
    $(".button").click();
    $x("//*[contains(text(), 'неверно')]").shouldHave(visible);
    clearPhone();
    $("[name=phone]").setValue("+79126178980");
    $(".button").click();
    $x("//*[contains(text(), 'неверно')]").shouldHave(not(visible));
    clearPhone();
    $(".button").click();
    $x("//*[contains(text(), 'обязательно')]").shouldHave(visible);
    clearPhone();
    $(".button").click();
    $x("//*[contains(text(), 'обязательно')]").shouldHave(visible);
    clearAll();
  }

  @Test
  void checkBox() {
    $(inputList.get(0)).setValue("Москва");
    $(inputList.get(1)).sendKeys(date);
    $("[name=name]").setValue("Иванов Иван");
    $("[name=phone]").setValue("+79126178980");
    $(".button").click();
    $x("//*[@data-test-id='agreement']").shouldHave(cssClass("input_invalid"));
    clearAll();
  }

  @Test
  void emptyForm() {
    clearDate();
    $(".button").click();
    $x("//*[contains(text(), 'обязательно')]").shouldHave(visible);
  }

  @Test
  void unhappyPath() {
    $(inputList.get(0)).setValue("Moscow");
    $(inputList.get(1)).sendKeys("00.00.0000");
    $("[name=name]").setValue("John Smith");
    $("[name=phone]").setValue("+3(912)6178980");
    $(".button").click();
    $x("//*[contains(text(), 'недоступна')]").shouldHave(visible);
    clearAll();
  }
}
