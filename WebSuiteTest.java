// Generated by Selenium IDE
import org.junit.Test;
import org.junit.Before;
import org.junit.After;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.core.IsNot.not;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Alert;
import org.openqa.selenium.Keys;
import java.util.*;
import java.net.MalformedURLException;
import java.net.URL;
public class DefaultSuiteTest {
  private WebDriver driver;
  private Map<String, Object> vars;
  JavascriptExecutor js;
  @Before
  public void setUp() {
    driver = new FirefoxDriver();
    js = (JavascriptExecutor) driver;
    vars = new HashMap<String, Object>();
  }
  @After
  public void tearDown() {
    driver.quit();
  }
  @Test
  public void fileNotFoundTest() {
    driver.get("http://localhost:8080/f.html");
    driver.manage().window().setSize(new Dimension(1550, 838));
    driver.findElement(By.cssSelector(".far")).click();
    driver.findElement(By.cssSelector("button")).click();
    assertThat(driver.getTitle(), is("404"));
    assertThat(driver.findElement(By.id("details")).getText(), is("The page you are looking for does not exist."));
    driver.findElement(By.linkText("GO HOME")).click();
    assertThat(driver.findElement(By.id("welcome")).getText(), is("Welcome to my home page"));
    assertThat(driver.getTitle(), is("HOME"));
    driver.close();
  }
  @Test
  public void maintenanceTest() {
    driver.get("http://localhost:8080/");
    driver.manage().window().setSize(new Dimension(1550, 838));
    assertThat(driver.getTitle(), is("HOME"));
    assertThat(driver.findElement(By.id("welcome")).getText(), is("Welcome to my home page"));
    driver.findElement(By.id("fname")).click();
    driver.findElement(By.id("fname")).click();
    driver.findElement(By.id("fname")).sendKeys("Andreea");
    driver.findElement(By.cssSelector("button")).click();
    assertThat(driver.getTitle(), is("MaintenanceUser"));
    assertThat(driver.findElement(By.cssSelector(".message")).getText(), is("Sorry, we\\\'re down for maintenance.\\\\nWe\\\'ll be back shortly.\\\\nGO HOME"));
    driver.findElement(By.linkText("GO HOME")).click();
    assertThat(driver.getTitle(), is("HOME"));
    driver.findElement(By.id("fname")).click();
    driver.findElement(By.id("fname")).sendKeys("admin");
    driver.findElement(By.cssSelector("button")).click();
    assertThat(driver.getTitle(), is("MaintenanceAdmin"));
    driver.findElement(By.linkText("GO HOME")).click();
    assertThat(driver.getTitle(), is("HOME"));
    driver.close();
  }
  @Test
  public void runningStateTest() {
    driver.get("http://localhost:8080/");
    driver.manage().window().setSize(new Dimension(1550, 838));
    assertThat(driver.findElement(By.id("welcome")).getText(), is("Welcome to my home page"));
    assertThat(driver.getTitle(), is("HOME"));
    driver.findElement(By.id("fname")).click();
    driver.findElement(By.id("fname")).click();
    driver.findElement(By.id("fname")).sendKeys("Andreea");
    driver.findElement(By.cssSelector("button")).click();
    assertThat(driver.getTitle(), is("Next Page"));
    assertThat(driver.findElement(By.cssSelector("h3")).getText(), is("Welcome !!"));
    driver.close();
  }
  @Test
  public void stopStateTest() {
    driver.get("http://localhost:8080/");
    driver.manage().window().setSize(new Dimension(1550, 838));
    assertThat(driver.getTitle(), is("STOP"));
    assertThat(driver.findElement(By.cssSelector("p")).getText(), is("Connection timeout"));
    driver.close();
  }
}