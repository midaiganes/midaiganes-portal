package ee.midaiganes.uitests;

import java.util.concurrent.TimeUnit;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.gargoylesoftware.htmlunit.BrowserVersion;

public class UiTestUtil {
    public static int TIMEOUT = 2;

    @Nonnull
    public static WebDriver getDriver() {
        HtmlUnitDriver driver = new HtmlUnitDriver(BrowserVersion.FIREFOX_38);
        driver.setJavascriptEnabled(true);
        driver.manage().timeouts().implicitlyWait(TIMEOUT, TimeUnit.SECONDS);
        return driver;
    }

    public static WebElement waitElementWithText(@Nonnull WebDriver driver, @Nonnull By by, String text) {
        return waitUntil(driver, new WaitElementWithText(text, by));
    }

    private static final class WaitElementWithText implements com.google.common.base.Function<WebDriver, WebElement> {
        private final String text;
        private final By by;

        public WaitElementWithText(String text, By by) {
            this.text = text;
            this.by = by;
        }

        @Nullable
        @Override
        public WebElement apply(@Nullable WebDriver driver) {
            if (driver != null) {
                WebElement el = driver.findElement(by);
                if (text.equals(el.getText())) {
                    return el;
                }
            }
            return null;
        }

        @Override
        public boolean equals(@Nullable Object o) {
            return o == this;
        }

        @Override
        public int hashCode() {
            return 0;
        }
    }

    public static <A> A waitUntil(WebDriver driver, com.google.common.base.Function<WebDriver, A> func) {
        return new WebDriverWait(driver, UiTestUtil.TIMEOUT).until(func);
    }
}
