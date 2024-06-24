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
    List<WebElement> tables = tableContents.findElements(By.cssSelector(".ui-table"));

    for (WebElement table : tables) {
      List<WebElement> headers = table.findElements(By.cssSelector(".ui-table__headerCell"));
      boolean firstRow = true;
      for (WebElement header : headers) {
        if(header.getText().contains("KLASYFIKACJA")) {
          System.out.print(String.format(firstRow ? "%2s " : "%12s ", "KLASYFIKACJA"));
        } else {
          System.out.print(String.format(firstRow ? "%2s " : "%12s ", header.getText()));
        }
        firstRow = false;
      }
      System.out.println();

      List<WebElement> rows = table.findElements(By.cssSelector(".ui-table__row"));
      for (WebElement row : rows) {
        System.out.print(String.format("%s ",row.findElement(By.cssSelector(".tableCellRank")).getText()));
        System.out.print(String.format("%12s ",row.findElement(By.cssSelector(".tableCellParticipant__name")).getText()));

        List<WebElement> values = row.findElements(By.cssSelector(".table__cell--value"));
        for(WebElement value: values) {
          System.out.print(String.format("%12s ", value.getText()));
        }

        List<WebElement> forms = row.findElements(By.cssSelector(".tableCellFormIcon"));
        StringBuilder builder = new StringBuilder();
        for(WebElement form: forms) {
          builder.append(form.getText() + " ");
        }
        System.out.print(String.format("%12s ", builder));

        System.out.println();
      }
      System.out.println();
    }

    driver.quit();
  }
}
