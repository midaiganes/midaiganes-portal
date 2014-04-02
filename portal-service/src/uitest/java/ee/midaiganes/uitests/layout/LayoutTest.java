package ee.midaiganes.uitests.layout;

import java.util.Date;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.testng.Assert;

import ee.midaiganes.uitests.UiTestUtil;
import ee.midaiganes.uitests.login.LoginUtil;

public class LayoutTest {

    @Test
    public void canOpenAddLayoutModal() {
        WebDriver driver = UiTestUtil.getDriver();
        LoginUtil.loginWithAdmin(driver);
        LayoutUtil.openLayoutModal(driver);
    }

    @Test
    public void canAddAndDeleteLayoutInLayoutModal() {
        WebDriver driver = UiTestUtil.getDriver();
        LoginUtil.loginWithAdmin(driver);
        LayoutUtil.openLayoutModal(driver);
        String url = "/test-" + (new Date().getTime());
        LayoutUtil.addLayout(driver, url);
        deleteLastLayoutInLayoutModal(driver);
    }

    @Test
    public void canAddEditTitleAndDeleteLayoutInLayoutModal() {
        WebDriver driver = UiTestUtil.getDriver();
        LoginUtil.loginWithAdmin(driver);
        LayoutUtil.openLayoutModal(driver);
        String suffix = "test-" + (new Date().getTime());
        String url = "/" + suffix;
        LayoutUtil.addLayout(driver, url);
        driver.findElement(By.cssSelector("#add-layout-form ul li:last-child span a")).click();
        driver.findElement(By.id("layoutTitlesen_US")).sendKeys(suffix);
        driver.findElement(By.cssSelector("#editLayoutModel button")).click();
        driver.findElement(By.id("add-layout-form"));
        driver.navigate().refresh();
        driver.findElement(By.linkText(suffix)).click();
        UiTestUtil.waitElementWithText(driver, By.cssSelector(".navigation .active"), suffix);
        LayoutUtil.openLayoutModal(driver);
        deleteLastLayoutInLayoutModal(driver);
    }

    private void deleteLastLayoutInLayoutModal(WebDriver driver) {
        WebElement deleteLink = driver.findElement(By.cssSelector("#add-layout-form ul li:last-child span + a"));
        Assert.assertEquals(deleteLink.getText(), "delete");
        deleteLink.click();
        UiTestUtil.waitUntil(driver, ExpectedConditions.stalenessOf(deleteLink));
    }
}
