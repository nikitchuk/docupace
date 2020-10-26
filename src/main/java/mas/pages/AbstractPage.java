package mas.pages;

import mas.controls.Element;
import mas.controls.TextBox;
import mas.parent.GenericPage;
import mas.utils.runTime.EndToEndProperties;
import org.jsoup.Jsoup;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Action;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.function.Supplier;

public abstract class AbstractPage extends GenericPage {

    protected static final String SCRIPT_BODY_CONTAINS_TEXT = "var body=document.querySelector('body') || ''; return (body.innerText || body.textContent || '').indexOf(arguments[0])>=0";


    public AbstractPage(WebDriver driver) {
        super(driver);
        wait.waitForPageToLoad();
        wait.enableAjaxInterceptor();
        isPageContentValid();
    }

    // Implement logined criteria
    public boolean isLogged(WebDriver driver) {
        return !Jsoup.parse(driver.getPageSource()).select(".CHECK.LOGINED.CRITERIA").isEmpty();
    }

    protected void waitUntilElementDoesNotExist(By locator) {
        wait.turnOffImplicitTimeout();
        Wait<WebDriver> wait = new FluentWait<>(getWebDriver())
                .withTimeout(Duration.ofSeconds(EndToEndProperties.getInstance().ACTUAL_TIMEOUT))
                .pollingEvery(Duration.ofMillis(EndToEndProperties.getInstance().POLLING_CYCLE))
                .ignoring(NoSuchElementException.class);

        wait.until(ExpectedConditions.numberOfElementsToBe(locator, 0));
        super.wait.turnOnImplicitTimeout(EndToEndProperties.getInstance().ACTUAL_TIMEOUT);
    }

    /**
     * Serves as backup value for ACTUAL_TIMEOUT
     */
    public int DEFAULT_TIMEOUT = 60;

    /**
     * Fail if WebElement is not found within this amount of time [in seconds]
     */
    public int ACTUAL_TIMEOUT = DEFAULT_TIMEOUT;

    /**
     * Wait for document.readyState to be 'complete'
     */
   /* public void waitForPageToLoad() {
        waitForPageToLoad(ACTUAL_TIMEOUT);
    }
*/
    /**
     * Wait for document.readyState to be 'complete'
     *
     * @param timeout maximum time to wait
     */
    /*public void waitForPageToLoad(int timeout) {
        final Wait<WebDriver> wait = new WebDriverWait(driver, timeout, defaultPolling());
        wait.until(driver -> (Boolean) javaScript.call(DOCUMENT_READY));
    }*/

    /**
     * Server as a check for content of the page
     * It should protect in incorrect navigation
     *
     * @return true, if new added page has valid contain
     */
    public abstract boolean isPageContentValid();

    /**
     * Check if text is present within page's body tag
     *
     * @param text - searched text
     *             - note - there is some issue with searching in source text
     * @return info if text exists on page
     */
    @Override
    public boolean verifyIfTextPresent(String text) {
        return (boolean) javaScript.getExecutor().executeScript(SCRIPT_BODY_CONTAINS_TEXT, text);
    }

    public void waitForElementToBeVisible(WebElement webElement) {
        wait.waitUntilElementIsVisible(webElement, DEFAULT_TIMEOUT);
    }

    protected void scrollIntoElement(Element element) {
        javaScript.getExecutor().executeScript("arguments[0].scrollIntoView(true);", element);

    }


    /**
     * Check if text is present within page's desired tag
     *
     * @param text searched text
     * @param by   element locator
     * @return true if text is present somewhere inside the page element, false otherwise
     */
    public boolean verifyIfTextPresent(String text, By by) {
        WebElement tag = isElementPresent(by);
        return tag != null && tag.getText().toLowerCase().contains(text.toLowerCase());
    }

    public String getPageTitle() {
        return getWebDriver().getTitle();
    }

    public void doubleClick(WebElement webElement) {
        webElement.click();
        webElement.click();
    }

    protected FluentWait<WebDriver> createFluentWait() {
        return new WebDriverWait(getWebDriver(), EndToEndProperties.getInstance().ACTUAL_TIMEOUT)
                .pollingEvery(Duration.ofMillis(EndToEndProperties.getInstance().POLLING_CYCLE))
                .ignoring(StaleElementReferenceException.class);
    }

    /**
     * Switches to new window.
     * Waits for page to load, waits for AJAX requests to finish.
     * Applies given function.
     * ALWAYS: Switches to original window and waits for page to load, waits for AJAX requests to finish.
     *
     * @param <T> output type
     * @param s   function to apply in new window
     * @return result of applied function
     */
    protected <T> T performInNewWindow(Supplier<T> s) {
        return performInNewWindow(null, (Void v) -> s.get());
    }

    /**
     * Switches to new window.
     * Waits for page to load, waits for AJAX requests to finish.
     * Performs given action.
     * ALWAYS: Switches to original window and waits for page to load, waits for AJAX requests to finish.
     *
     * @param a action to perform in new window
     */
    protected void performInNewWindow(Action a) {
        performInNewWindow(null, (Void v) -> {
            a.perform();
            return null;
        });
    }

    /**
     * Switches to new window.
     * Waits for page to load, waits for AJAX requests to finish.
     * Applies given function.
     * ALWAYS: Switches to original window and waits for page to load, waits for AJAX requests to finish.
     *
     * @param <I>   input type
     * @param <O>   output type
     * @param input input value
     * @param f     function to apply in new window
     * @return result of applied function
     */
    protected <I, O> O performInNewWindow(I input, Function<I, O> f) {
        String originalWindowID = switchToNewWindow();
        try {
            wait.waitForPageToLoad();
            wait.waitForAjax();
            return f.apply(input);
        } finally {
            switchToWindow(originalWindowID);
            wait.waitForPageToLoad();
            wait.waitForAjax();
        }
    }

    @Override
    protected String switchToNewWindow() {
        createFluentWait()
                .withMessage("Waiting for new browser window to appear.")
                .until(wd -> wd.getWindowHandles().size() > 1);
        return super.switchToNewWindow();
    }

    protected void fillTextBoxWithReactDataRoot(TextBox textBox, String fillingValue) {
        textBox.sendKeys(fillingValue.substring(0, 3));
        wait.sleep(1);
        textBox.sendKeys(fillingValue.substring(3));
    }
}
