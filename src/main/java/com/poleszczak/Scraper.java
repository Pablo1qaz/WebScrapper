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

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class Scraper {

  private static final Logger logger = LogManager.getLogger(Scraper.class);
  private JFrame frame;
  private JComboBox<String> categoryComboBox;
  private JTable dataTable;
  private static Map<String, String[][]> data = new ConcurrentHashMap<>();

  public static void main(String[] args) {
    logger.info("Intializing program");

    SwingUtilities.invokeLater(() -> {
      new Scraper().createAndShowGUI();
    });
  }

  private void createAndShowGUI() {
    frame = new JFrame("WebScraper - EURO 2024");
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    JPanel controlPanel = new JPanel();
    frame.add(controlPanel, BorderLayout.NORTH);

    retrieveData();
    logger.info("Data processed");

    String[] categories = Scraper.data.keySet().stream().sorted().collect(Collectors.toList()).toArray(new String[data.size()]);
    categoryComboBox = new JComboBox<>(categories);
    controlPanel.add(new JLabel("Select Group:"));
    controlPanel.add(categoryComboBox);

    String[] columnNames = {"#", "Reprezentacja", "Mecze", "Zwycięstwa", "Remisy", "Porażki", "Bramki", "RB", "Punkty", "Forma"};

    DefaultTableModel model = new DefaultTableModel(data.get(0), columnNames);
    dataTable = new JTable(model);

    JScrollPane scrollPane = new JScrollPane(dataTable);
    frame.add(scrollPane, BorderLayout.CENTER);

    categoryComboBox.addActionListener(e -> updateTableData());
    updateTableData();

    frame.setSize(800, 400);
    frame.setLocationRelativeTo(null);
    frame.setVisible(true);
  }

  private void updateTableData() {
    DefaultTableModel model = (DefaultTableModel) dataTable.getModel();
    model.setRowCount(0);

    String selectedGroup = (String) categoryComboBox.getSelectedItem();

    for (Object[] rowData : data.get(selectedGroup)) {
      model.addRow(rowData);
    }
  }

  private void retrieveData() {
    Map<String, String[][]> tmp = new ConcurrentHashMap<>();
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
        String name = null;
        List<WebElement> headers = table.findElements(By.cssSelector(".table__headerCell--participant"));
        for (WebElement header : headers) {
          if (header.getText().contains("KLASYFIKACJA")) {
            name = "KLASYFIKACJA DODATKOWA";
          } else {
            name = header.getText();
          }
        }

        List<WebElement> rows = table.findElements(By.cssSelector(".ui-table__row"));
        int i = 0, j = 0;
        String[][] col = new String[6][10];
        for (WebElement row : rows) {
          col[i][j] = row.findElement(By.cssSelector(".tableCellRank")).getText();
          col[i][++j] = row.findElement(By.cssSelector(".tableCellParticipant__name")).getText();

          List<WebElement> values = row.findElements(By.cssSelector(".table__cell--value"));
          for (WebElement value : values) {
            col[i][++j] = value.getText();
          }

          List<WebElement> forms = row.findElements(By.cssSelector(".tableCellFormIcon"));
          StringBuilder builder = new StringBuilder();
          for (WebElement form : forms) {
            builder.append(form.getText() + " ");
          }
          col[i][++j] = builder.toString();

          i++;
          j = 0;
        }
        System.out.println();

        data.put(name, col);
      }

      driver.quit();
      logger.debug("Chrome driver gracefully shutdown");
    } catch (TimeoutException e) {
      logger.error("Timout exception: {}", e.getMessage());
      System.exit(1);
    } catch (NoSuchElementException e) {
      logger.error("Element not found: {}", e.getMessage());
      System.exit(1);
    } catch (Exception e) {
      logger.error("Undefined exception: {}", e.getMessage());
      System.exit(1);
    }
  }
}
