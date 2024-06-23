package com.poleszczak;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

public class Scraper {

  public static void main(String[] args) {
    ChromeOptions options = new ChromeOptions();
    options.addArguments("--headless");

    WebDriver driver = new ChromeDriver(options);
    driver.get("https://www.flashscore.pl/tabela/ABkrguJ9/EcpQtcVi/#/EcpQtcVi/table");

    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5L));
    WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("tournament-table-tabs-and-content")));

    WebElement tableContents = driver.findElement(By.id("tournament-table-tabs-and-content"));
    List<WebElement> tables = tableContents.findElements(By.className("tableWrapper"));

    for(WebElement table: tables) {
      //TODO wyciągać tutaj te elementy i później przemapować na jakiś model np. EuroTables
      System.out.println(table.getText());
    }
  }
}
