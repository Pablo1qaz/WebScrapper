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
        List<WebElement> rows = tableContents.findElements(By.cssSelector(".ui-table__body .ui-table__row"));

        if (rows.isEmpty()) {
            System.out.println("No rows found");
        }

        for (WebElement row : rows) {
            List<WebElement> columns = row.findElements(By.cssSelector(".table__cell"));
            if (columns.isEmpty()) {
                System.out.println("No columns found in row");
            }
            for (WebElement column : columns) {
                System.out.print(column.getText() + " ");
            }
            System.out.println();
        }

        driver.quit();
    }
}
