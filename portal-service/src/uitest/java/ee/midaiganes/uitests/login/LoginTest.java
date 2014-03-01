package ee.midaiganes.uitests.login;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;

import ee.midaiganes.uitests.UiTestUtil;

public class LoginTest {

    @Test
    public void loginWithAdmin() {
        WebDriver driver = UiTestUtil.getDriver();
        LoginUtil.loginWithAdmin(driver);
        Assert.assertEquals(driver.findElement(By.cssSelector("header a")).getText(), "log out");
    }

    public void canSeeErrorMessageIfPasswordIsIncorrect() {
        WebDriver driver = UiTestUtil.getDriver();
        LoginUtil.login(driver, "test", "wrong password");
        Assert.assertEquals(driver.findElement(By.cssSelector(".message.error")).getText(), "Invalid username or password.");
    }
}
