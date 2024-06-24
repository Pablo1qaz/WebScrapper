package com.poleszczak;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Scraper {

  private static final Logger logger = LogManager.getLogger(Scraper.class);
  private static final ExecutorService executor = Executors.newFixedThreadPool(3);

  public static void main(String[] args) {
    logger.info("Intializing program");

    ChromeOptions options = new ChromeOptions();
    options.addArguments("--headless");
    logger.debug("Chrome options configured");

    try {
      WebDriver driver = new ChromeDriver(options);
      driver.get("https://www.flashscore.pl/tabela/ABkrguJ9/EcpQtcVi/#/EcpQtcVi/table");
      driver.manage().timeouts().pageLoadTimeout(5, TimeUnit.SECONDS);
      logger.debug("Chrome Web Driver configured");

      WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5L));
      WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("tournament-table-tabs-and-content")));
      logger.trace("Page loaded");

      WebElement tableContents = driver.findElement(By.id("tournament-table-tabs-and-content"));
      List<WebElement> tables = tableContents.findElements(By.cssSelector(".ui-table"));

      for (WebElement table : tables) {
        logger.debug("Processing elements");
        List<WebElement> headers = table.findElements(By.cssSelector(".ui-table__headerCell"));
        boolean firstRow = true;
        for (WebElement header : headers) {
          if (header.getText().contains("KLASYFIKACJA")) {
            System.out.print(String.format(firstRow ? "%2s " : "%12s ", "KLASYFIKACJA"));
          } else {
            System.out.print(String.format(firstRow ? "%2s " : "%12s ", header.getText()));
          }
          firstRow = false;
        }
        System.out.println();

        List<WebElement> rows = table.findElements(By.cssSelector(".ui-table__row"));
        for (WebElement row : rows) {
          System.out.print(String.format("%s ", row.findElement(By.cssSelector(".tableCellRank")).getText()));
          System.out.print(String.format("%12s ", row.findElement(By.cssSelector(".tableCellParticipant__name")).getText()));

          List<WebElement> values = row.findElements(By.cssSelector(".table__cell--value"));
          for (WebElement value : values) {
            System.out.print(String.format("%12s ", value.getText()));
          }

          List<WebElement> forms = row.findElements(By.cssSelector(".tableCellFormIcon"));
          StringBuilder builder = new StringBuilder();
          for (WebElement form : forms) {
            builder.append(form.getText() + " ");
          }
          System.out.print(String.format("%12s ", builder));

          System.out.println();
        }
        System.out.println();
      }

      executor.shutdown();
      driver.quit();
      logger.debug("Driver gracefully shutdown");
      logger.info("Program successfully ended");
    } catch (TimeoutException e) {
      logger.error("Timout exception: {}", e.getMessage());
    } catch (NoSuchElementException e) {
      logger.error("Element not found: {}", e.getMessage());
    } catch (Exception e) {
      logger.error("Undefined exception: {}", e.getMessage());
    }
  }
}
