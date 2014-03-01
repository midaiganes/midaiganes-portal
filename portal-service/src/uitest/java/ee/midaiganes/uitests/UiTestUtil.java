package ee.midaiganes.uitests;

import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.gargoylesoftware.htmlunit.BrowserVersion;

public class UiTestUtil {
    public static int TIMEOUT = 2;

    public static WebDriver getDriver() {
        HtmlUnitDriver driver = new HtmlUnitDriver(BrowserVersion.FIREFOX_17);
        driver.setJavascriptEnabled(true);
        driver.manage().timeouts().implicitlyWait(TIMEOUT, TimeUnit.SECONDS);
        return driver;
    }

    public static WebElement waitElementWithText(WebDriver driver, final By by, final String text) {
        return waitUntil(driver, new com.google.common.base.Function<WebDriver, WebElement>() {
            @Override
            public WebElement apply(WebDriver driver) {
                WebElement el = driver.findElement(by);
                if (text.equals(el.getText())) {
                    return el;
                }
                return null;
            }
        });
    }

    public static <A> A waitUntil(WebDriver driver, com.google.common.base.Function<WebDriver, A> func) {
        return new WebDriverWait(driver, UiTestUtil.TIMEOUT).until(func);
    }
}
