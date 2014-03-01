package ee.midaiganes.uitests.login;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class LoginUtil {
    public static void login(WebDriver driver, String name, String password) {
        driver.get("http://localhost:8080/midaiganes/test");
        driver.findElement(By.id("username")).sendKeys(name);
        driver.findElement(By.id("password")).sendKeys(password);
        driver.findElement(By.tagName("button")).click();
    }

    public static void loginWithAdmin(WebDriver driver) {
        login(driver, "test", "test");
    }
}
