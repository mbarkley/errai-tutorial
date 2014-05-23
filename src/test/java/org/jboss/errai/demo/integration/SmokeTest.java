package org.jboss.errai.demo.integration;

import static org.junit.Assert.*;

import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;

public class SmokeTest {

  private static final String URL = "http://localhost:8080/errai-tutorial";
  private static final int WAIT_TIME_SECONDS = 10;

  /*
   * This must be static since selenium makes a new test instance for each test.
   */
  private static int testId = 1;

  private String testName;
  private String testEmail;
  private String testComplaint;

  private static final String MOST_RECENT_COMPLAINT_XPATH = "//tbody//tr[last()]";

  private FirefoxDriver driver;

  @Before
  public void setUp() throws Exception {
    driver = new FirefoxDriver();

    driver.manage().timeouts().implicitlyWait(WAIT_TIME_SECONDS, TimeUnit.SECONDS);
    driver.manage().deleteAllCookies();

    testName = "Selenium Test " + testId;
    testEmail = "test" + testId + "@selenium.com";
    testComplaint = "This is my " + testId + " try and it still doesn't work!";
    testId += 1;

    driver.get(URL);
  }

  @After
  public void tearDown() throws Exception {
    driver.quit();
  }

  @Test
  public void submitComplaint() throws Exception {
    driver.findElement(By.id("name")).sendKeys(testName);
    driver.findElement(By.id("email")).sendKeys(testEmail);
    driver.findElement(By.id("text")).sendKeys(testComplaint);
    driver.findElement(By.id("submit")).click();

    waitForText("Thank you!", By.className("blackened"));
  }

  @Test
  public void viewComplaintOnAdminPage() throws Exception {
    submitComplaint();

    driver.findElement(By.id("admin")).click();
    waitForText(testName, By.xpath(MOST_RECENT_COMPLAINT_XPATH + "//td[@data-field='name']"));
    waitForText(testEmail, By.xpath(MOST_RECENT_COMPLAINT_XPATH + "//td[@data-field='email']"));
    waitForText(testComplaint, By.xpath(MOST_RECENT_COMPLAINT_XPATH + "//td[@data-field='text']"));
    assertNull(driver.findElement(By.xpath(MOST_RECENT_COMPLAINT_XPATH + "//input[@type='checkbox']"))
            .getAttribute("checked"));
    waitForMatch(new ElementTest() {

      @Override
      public void test(WebElement element) throws AssertionError {
        assertEquals("issue-open", element.getAttribute("class"));
      }
    }, By.xpath(MOST_RECENT_COMPLAINT_XPATH));
  }

  @Test
  public void checkComplaintOnAdminPage() throws Exception {
    viewComplaintOnAdminPage();

    driver.findElement(By.xpath(MOST_RECENT_COMPLAINT_XPATH + "//input[@type='checkbox']")).click();
    try {
      driver.findElement(By.className("issue-closed"));
      assertEquals("There should only be one closed issue.", 1, driver.findElements(By.className("issue-closed")).size());
    }
    catch (NoSuchElementException e) {
      fail("Clicking checkbox did not close issue.");
    }
  }

  private void waitForText(final String text, final By by) throws InterruptedException {
    waitForMatch(new ElementTest() {
      @Override
      public void test(final WebElement element) {
        assertEquals(text, element.getText());
      }
    }, by);
  }

  private void waitForMatch(final ElementTest test, final By by) throws InterruptedException {
    final long start = System.currentTimeMillis();
    WebElement element = null;

    while (System.currentTimeMillis() - start < WAIT_TIME_SECONDS * 1000) {
      try {
        element = driver.findElement(by);
      }
      catch (NoSuchElementException e) {
        fail("Could not find element: " + by);
      }

      try {
        test.test(element);
      }
      catch (AssertionError e) {
        Thread.sleep(1000);
        continue;
      }
    }

    test.test(element);
  }

  private static interface ElementTest {
    void test(WebElement element) throws AssertionError;
  }
}
