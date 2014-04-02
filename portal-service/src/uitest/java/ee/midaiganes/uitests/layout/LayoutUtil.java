package ee.midaiganes.uitests.layout;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.testng.Assert;

import ee.midaiganes.uitests.UiTestUtil;

public class LayoutUtil {
    public static void openLayoutModal(WebDriver driver) {
        WebElement dockbarMenu = driver.findElement(By.cssSelector("#dockbar > ul > li"));
        new Actions(driver).moveToElement(dockbarMenu).perform();
        new Actions(driver).moveToElement(driver.findElement(By.cssSelector("#dockbar ul li ul li:first-child a"))).click().perform();
        driver.findElement(By.id("add-layout-form"));
    }

    public static void addLayout(WebDriver driver, String url) {
        driver.findElement(By.id("url")).sendKeys(url);
        WebElement addLayoutButton = driver.findElement(By.cssSelector("#add-layout-form button"));
        Assert.assertEquals(addLayoutButton.getText(), "Add layout");
        addLayoutButton.click();
        UiTestUtil.waitElementWithText(driver, By.cssSelector("#add-layout-form ul li:last-child span a"), url);
    }
}
