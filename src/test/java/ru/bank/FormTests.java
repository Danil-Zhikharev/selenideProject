package ru.bank;

import com.codeborne.selenide.Configuration;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.*;

public class FormTests {
  WebDriver driver;

  int days = 3;
  String date = LocalDate.now().plusDays(days).format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
  String dayOfCalendar = String.valueOf(LocalDate.now().plusDays(days).getDayOfMonth());
  String specialDay = LocalDate.now().plusDays(7).format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
  String specialDate = String.valueOf(LocalDate.now().plusDays(7).getDayOfMonth());

  @BeforeAll
  static void setUpAll() {
    WebDriverManager.chromedriver().setup();
  }

  @BeforeEach
  public void settings() {
    Configuration.headless = true;
    Configuration.baseUrl = "http://localhost:9999";
    open(Configuration.baseUrl);
  }

  @BeforeEach
  public void setUp() {
    ChromeOptions options = new ChromeOptions();
    options.addArguments("--disable-dev-shm-usage");
    options.addArguments("--no-sandbox");
    options.addArguments("--headless");
    driver = new ChromeDriver(options);
  }

  @AfterAll
  public void teardown() {
    driver.quit();
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
    clearCity();
    clearName();
    clearDate();
    clearPhone();
  }

  void lookingFor() {
    $x("//*[contains(@placeholder, 'ата')]/following-sibling::*/child::button").hover().click();
    if ($x("//*[text()='" + specialDate + "']").isDisplayed()) {
      $x("//*[text()='" + specialDate + "']").hover().click();
    } else {
      $x("//*[@data-step=1]").click();
      $x("//*[text()='" + specialDate + "']").hover().click();
      $x("//*[contains(@placeholder, 'ата')]").shouldHave(value(specialDay));
    }
  }

  @Test
  void specialDate() {
    $x("//*[@data-test-id='city']//child::*[contains(@placeholder,'ород')]").setValue("Москва");
    clearDate();
    lookingFor();
  }

  void clearCity() {
    $x("//*[@data-test-id='city']//child::*[contains(@placeholder,'ород')]").hover();
    $x("//*[@data-test-id='city']//child::*[contains(@placeholder,'ород')]")
        .sendKeys(Keys.chord(Keys.CONTROL, "a"));
    $x("//*[@data-test-id='city']//child::*[contains(@placeholder,'ород')]")
        .sendKeys(Keys.chord(Keys.DELETE));
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
    $x("//*[@data-test-id='city']//child::*[contains(@placeholder,'ород')]")
        .setValue("ЕКАТЕРИНБУРГ");
    $x("//*[contains(text(), 'абронировать')]").click();
    $x("//*[contains(text(), 'недоступна')]").shouldHave(not(visible));
    clearCity();
    $x("//*[@data-test-id='city']//child::*[contains(@placeholder,'ород')]").hover();
    $x("//*[@data-test-id='city']//child::*[contains(@placeholder,'ород')]").sendKeys("нов");
    $x("//*[contains(text(), 'Нижний')]").hover().click();
    $x("//*[contains(text(), 'абронировать')]").click();
    $x("//*[contains(text(), 'недоступна')]").shouldHave(not(visible));
    clearCity();
    $x("//*[contains(text(), 'абронировать')]").click();
    $x("//*[contains(text(), 'обязательно')]").shouldHave(visible);
  }

  void clearDate() {
    $x("//*[contains(@placeholder, 'ата')]").hover();
    $x("//*[contains(@placeholder, 'ата')]").sendKeys(Keys.chord(Keys.CONTROL, "a"));
    $x("//*[contains(@placeholder, 'ата')]").sendKeys(Keys.chord(Keys.DELETE));
  }

  @Test
  void dateValidations() {
    $x("//*[@data-test-id='city']//child::*[contains(@placeholder,'ород')]").setValue("Москва");
    clearDate();
    $x("//*[contains(@placeholder, 'ата')]")
        .sendKeys(LocalDate.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
    $x("//*[contains(text(), 'абронировать')]").click();
    $x("//*[contains(text(), 'невозможен')]").shouldHave(visible);
    clearDate();
    $x("//*[contains(@placeholder, 'ата')]")
        .sendKeys(LocalDate.now().plusDays(1).format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
    $x("//*[contains(text(), 'абронировать')]").click();
    $x("//*[contains(text(), 'невозможен')]").shouldHave(visible);
    clearDate();
    $x("//*[contains(@placeholder, 'ата')]")
        .sendKeys(LocalDate.now().plusDays(2).format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
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
    $x("//*[text()='" + dayOfCalendar + "']").hover().click();
    $x("//*[contains(@placeholder, 'ата')]").shouldHave(value(date));
    clearDate();
    $x("//*[contains(text(), 'абронировать')]").click();
    $x("//*[contains(text(), 'ыберите')]").shouldHave(visible);
  }

  void clearName() {
    $("[name=name]").hover();
    $("[name=name]").sendKeys(Keys.chord(Keys.CONTROL, "a"));
    $("[name=name]").sendKeys(Keys.chord(Keys.DELETE));
  }

  @Test
  void nameValidations() {
    $x("//*[@data-test-id='city']//child::*[contains(@placeholder,'ород')]").setValue("Москва");
    $x("//*[contains(@placeholder, 'ата')]").setValue(String.valueOf(date));
    $("[name=name]").setValue("Jone Smith");
    $x("//*[contains(text(), 'абронировать')]").click();
    $x("//*[contains(text(), 'неверно')]").shouldHave(visible);
    clearName();
    $("[name=name]").setValue("Иван-Петр Сидоров-Петров");
    $x("//*[contains(text(), 'абронировать')]").click();
    $x("//*[contains(text(), 'неверно')]").shouldHave(not(visible));
    clearName();
    $("[name=name]").setValue("Иван Сергеевич Петров");
    $x("//*[contains(text(), 'абронировать')]").click();
    $x("//*[contains(text(), 'неверно')]").shouldHave(not(visible));
    clearName();
    $("[name=name]").setValue("Иван   Петров");
    $x("//*[contains(text(), 'абронировать')]").click();
    $x("//*[contains(text(), 'неверно')]").shouldHave(not(visible));
    clearName();
    $("[name=name]").setValue("ИВАН ПЕТРОВ");
    $x("//*[contains(text(), 'абронировать')]").click();
    $x("//*[contains(text(), 'неверно')]").shouldHave(not(visible));
    clearName();
    $("[name=name]").setValue("Иван Сидоров-Петров");
    $x("//*[contains(text(), 'абронировать')]").click();
    $x("//*[contains(text(), 'неверно')]").shouldHave(not(visible));
    clearName();
    $x("//*[contains(text(), 'абронировать')]").click();
    $x("//*[contains(text(), 'обязательно')]").shouldHave(visible);
  }

  void clearPhone() {
    $("[name=phone]").hover();
    $("[name=phone]").sendKeys(Keys.chord(Keys.CONTROL, "a"));
    $("[name=phone]").sendKeys(Keys.chord(Keys.DELETE));
  }

  @Test
  void phoneValidations() {
    $x("//*[@data-test-id='city']//child::*[contains(@placeholder,'ород')]").setValue("Москва");
    $x("//*[contains(@placeholder, 'ата')]").setValue(String.valueOf(date));
    $("[name=name]").setValue("Иванов Иван");
    $("[name=phone]").setValue("+7 912 617 89 80");
    $x("//*[contains(text(), 'абронировать')]").click();
    $x("//*[contains(text(), 'неверно')]").shouldHave(visible);
    clearPhone();
    $("[name=phone]").setValue("+39126178980");
    $x("//*[contains(text(), 'абронировать')]").click();
    $x("//*[contains(text(), 'неверно')]").shouldHave(not(visible));
    clearPhone();
    $("[name=phone]").setValue("+3(912)6178980");
    $x("//*[contains(text(), 'абронировать')]").click();
    $x("//*[contains(text(), 'неверно')]").shouldHave(visible);
    clearPhone();
    $("[name=phone]").setValue("89126178980");
    $x("//*[contains(text(), 'абронировать')]").click();
    $x("//*[contains(text(), 'неверно')]").shouldHave(visible);
    clearPhone();
    $("[name=phone]").setValue("891261789800");
    $x("//*[contains(text(), 'абронировать')]").click();
    $x("//*[contains(text(), 'неверно')]").shouldHave(visible);
    clearPhone();
    $("[name=phone]").setValue("9126178980");
    $x("//*[contains(text(), 'абронировать')]").click();
    $x("//*[contains(text(), 'неверно')]").shouldHave(visible);
    clearPhone();
    $("[name=phone]").setValue("-79126178980");
    $x("//*[contains(text(), 'абронировать')]").click();
    $x("//*[contains(text(), 'неверно')]").shouldHave(visible);
    clearPhone();
    $("[name=phone]").setValue("+79126178980");
    $x("//*[contains(text(), 'абронировать')]").click();
    $x("//*[contains(text(), 'неверно')]").shouldHave(not(visible));
    clearPhone();
    $x("//*[contains(text(), 'абронировать')]").click();
    $x("//*[contains(text(), 'обязательно')]").shouldHave(visible);
    clearPhone();
    $x("//*[contains(text(), 'абронировать')]").click();
    $x("//*[contains(text(), 'обязательно')]").shouldHave(visible);
  }

  @Test
  void checkBox() {
    $x("//*[@data-test-id='city']//child::*[contains(@placeholder,'ород')]").setValue("Москва");
    $x("//*[contains(@placeholder, 'ата')]").setValue(String.valueOf(date));
    $("[name=name]").setValue("Иванов Иван");
    $("[name=phone]").setValue("+79126178980");
    $x("//*[contains(text(), 'абронировать')]").click();
    $x("//*[@data-test-id='agreement']").shouldHave(cssClass("input_invalid"));
  }

  @Test
  void emptyForm() {
    clearDate();
    $x("//*[contains(text(), 'абронировать')]").click();
    $x("//*[contains(text(), 'обязательно')]").shouldHave(visible);
  }

  @Test
  void unhappyPath() {
    $x("//*[@data-test-id='city']//child::*[contains(@placeholder,'ород')]").setValue("Moscow");
    $x("//*[contains(@placeholder, 'ата')]").sendKeys("00.00.0000");
    $("[name=name]").setValue("Jone Smith");
    $("[name=phone]").setValue("+3(912)6178980");
    $x("//*[contains(text(), 'абронировать')]").click();
    $x("//*[contains(text(), 'недоступна')]").shouldHave(visible);
  }
}
