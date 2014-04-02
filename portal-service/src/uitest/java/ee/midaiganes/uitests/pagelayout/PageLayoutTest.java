package ee.midaiganes.uitests.pagelayout;

import java.util.Date;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;

import ee.midaiganes.uitests.UiTestUtil;
import ee.midaiganes.uitests.layout.LayoutUtil;
import ee.midaiganes.uitests.login.LoginUtil;

public class PageLayoutTest {

    @Test
    public void canChangePageLayout() {
        WebDriver driver = UiTestUtil.getDriver();
        LoginUtil.loginWithAdmin(driver);
        LayoutUtil.openLayoutModal(driver);
        String url = "/testpagelayout-" + new Date().getTime();
        LayoutUtil.addLayout(driver, url);
        driver.navigate().refresh();
        driver.findElement(By.linkText(url)).click();
        Assert.assertEquals(driver.findElements(By.className("layout-hole")).size(), 1, "Expected page layout with 1 box");
    }
}
