package ee.midaiganes.uitests.layout;

import java.util.Date;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.testng.Assert;

import ee.midaiganes.uitests.UiTestUtil;
import ee.midaiganes.uitests.login.LoginUtil;

public class LayoutTest {

    @Test
    public void canOpenAddLayoutModal() {
        WebDriver driver = UiTestUtil.getDriver();
        LoginUtil.loginWithAdmin(driver);
        openLayoutModal(driver);
    }

    @Test
    public void canAddAndDeleteLayoutInLayoutModal() {
        WebDriver driver = UiTestUtil.getDriver();
        LoginUtil.loginWithAdmin(driver);
        openLayoutModal(driver);
        String url = "/test-" + (new Date().getTime());
        addLayout(driver, url);
        deleteLastLayoutInLayoutModal(driver);
    }

    @Test
    public void canAddEditTitleAndDeleteLayoutInLayoutModal() {
        WebDriver driver = UiTestUtil.getDriver();
        LoginUtil.loginWithAdmin(driver);
        openLayoutModal(driver);
        String suffix = "test-" + (new Date().getTime());
        String url = "/" + suffix;
        addLayout(driver, url);
        driver.findElement(By.cssSelector("#add-layout-form ul li:last-child span a")).click();
        driver.findElement(By.id("layoutTitlesen_US")).sendKeys(suffix);
        driver.findElement(By.cssSelector("#editLayoutModel button")).click();
        driver.findElement(By.id("add-layout-form"));
        driver.navigate().refresh();
        driver.findElement(By.linkText(suffix)).click();
        UiTestUtil.waitElementWithText(driver, By.cssSelector(".navigation .active"), suffix);
        openLayoutModal(driver);
        deleteLastLayoutInLayoutModal(driver);
    }

    private void deleteLastLayoutInLayoutModal(WebDriver driver) {
        WebElement deleteLink = driver.findElement(By.cssSelector("#add-layout-form ul li:last-child span + a"));
        Assert.assertEquals(deleteLink.getText(), "delete");
        deleteLink.click();
        UiTestUtil.waitUntil(driver, ExpectedConditions.stalenessOf(deleteLink));
    }

    private void addLayout(WebDriver driver, String url) {
        driver.findElement(By.id("url")).sendKeys(url);
        WebElement addLayoutButton = driver.findElement(By.cssSelector("#add-layout-form button"));
        Assert.assertEquals(addLayoutButton.getText(), "Add layout");
        addLayoutButton.click();
        UiTestUtil.waitElementWithText(driver, By.cssSelector("#add-layout-form ul li:last-child span a"), url);
    }

    private void openLayoutModal(WebDriver driver) {
        WebElement dockbarMenu = driver.findElement(By.cssSelector("#dockbar > ul > li"));
        new Actions(driver).moveToElement(dockbarMenu).perform();
        new Actions(driver).moveToElement(driver.findElement(By.cssSelector("#dockbar ul li ul li:first-child a"))).click().perform();
        driver.findElement(By.id("add-layout-form"));
    }
}
