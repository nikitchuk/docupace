package mas.parent;

import mas.controls.Element;
import mas.controls.internal.ElementFactory;
import mas.controls.internal.ElementFinder;
import mas.utils.runTime.EndToEndProperties;
import mas.utils.runTime.JavaScript;
import mas.utils.runTime.Waiting;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Set;


/**
 * Created by IntelliJ IDEA.
 * Support for creating pages through PageObjects pattern
 */
public abstract class GenericPage {

    protected static final Logger logger = LoggerFactory.getLogger(GenericPage.class);
    /**
     * WebDriver instance
     */
    private WebDriver webDriver;
    protected JavaScript javaScript;
    protected Waiting wait;

    /**
     * Default constructor for offspring's
     *
     * @param driver Default WebDriver
     * @throws IllegalArgumentException if wrong input parameters
     */
    public GenericPage(WebDriver driver) throws IllegalArgumentException {
        if (driver == null) {
            throw new IllegalArgumentException("WebDriver cannot be null");
        }
        this.webDriver = driver;
        this.javaScript = new JavaScript(this.webDriver);
        this.wait = new Waiting(this.webDriver);
    }

    /**
     * Execute custom javascript code on some WebElement.
     *
     * @param script  code to be executed
     * @param element argument for the script
     * @return execution result
     */
    @Deprecated
    protected Object customJavaScriptCall(String script, WebElement element) {
        return javaScript.call(script, element);
    }

    /**
     * Returns JavaScriptExecutor (created from WebDriver)
     *
     * @return JavaScript executor
     */
    @Deprecated
    protected JavascriptExecutor getJavaScriptExecutor() {
        return javaScript.getExecutor();
    }

    /**
     * Execute custom javascript code on multiple elements.
     *
     * @param script   code to be executed
     * @param elements arguments for the script
     * @return execution result
     */
    @Deprecated
    protected Object customJavaScriptCall(String script, List<WebElement> elements) {
        return javaScript.call(script, elements);
    }

    /**
     * Click on element through javascript
     *
     * @param element -
     */
    @Deprecated
    protected void clickOnElementJavaScript(WebElement element) {
        javaScript.clickOnElement(element);
    }

    /**
     * Navigate to URL and initialize a new page object.
     * Default timeout from EndToEndProperties is used.
     *
     * @param pageClass class of requested page object
     * @param url       navigate to this url
     * @param <T>       return type corresponds to pageClass
     * @return initialized page object
     */
    protected final <T extends GenericPage> T addPageToFactory(Class<T> pageClass, String url) {
        return addPageToFactory(pageClass, url, EndToEndProperties.getInstance().ACTUAL_TIMEOUT);
    }

    /**
     * Navigate to URL and initialize a new page object.
     *
     * @param pageClass class of requested page object
     * @param url       navigate to this url
     * @param timeout   maximum time to wait for page to load
     * @param <T>       return type corresponds to pageClass
     * @return initialized page object
     */
    protected final <T extends GenericPage> T addPageToFactory(Class<T> pageClass, String url, int timeout) {
        webDriver.get(url);
        wait.waitForPageToLoad(timeout);
        return addPageToFactory(pageClass);
    }

    /**
     * Init page through factory and check if page content is valid
     *
     * @param pageClass -
     * @param <T>       - New page
     * @return new page in factory
     */
    protected final <T extends GenericPage> T addPageToFactory(Class<T> pageClass) {
        // USE CUSTOM ElementFactory INSTEAD OF DEFAULT PageFactory
        T newPage = ElementFactory.initElements(this.webDriver, pageClass);
        newPage.initPageControls();
        if (!newPage.isPageContentValid()) {           // validate content of the page
            throw new IllegalStateException(String.format(
                    "\nPage %s has incorrect content or it isn't the page you expected" +
                            "\nCurrent Url:   %s " +
                            "\nCurrent Title: %s", pageClass.toString(), getCurrentUrl(), getTitle()
            )
            );
        }
        return newPage;
    }
    /**
     * Custom initialization of controls on page
     * Select, custom findElementBy etc.
     */
    protected void initPageControls() {
    }

    /**
     * Server as a check for content of the page
     * It should protect in incorrect navigation
     *
     * @return true, if new added page has valid contain
     */
    public abstract boolean isPageContentValid();

    /**
     * Get current url
     *
     * @return -
     */
    public String getCurrentUrl() {
        return webDriver.getCurrentUrl();
    }

    /**
     * Get current open webpage Title
     *
     * @return StringUtils.EMPTY when no title tag is find
     */
    public String getTitle() {
        try {
            return webDriver.getTitle();
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * Wait for document.readyState to be 'complete'
     *
     * @param timeout maximum time to wait
     */
    @Deprecated
    protected void waitForPageToLoad(int timeout) {
        wait.waitForPageToLoad(timeout);
    }

    /**
     * Process javascript code
     *
     * @param script -
     * @return return executed Javascript code
     */
    @Deprecated
    protected Object customJavaScriptCall(String script) {
        return javaScript.call(script);
    }

    /**
     * Get info about title - refreshed
     *
     * @param newTitle -
     * @return true / false - title is same before and after
     */
    public boolean isTitleRefreshed(String newTitle) {
        return isTitleRefreshed(newTitle, EndToEndProperties.getInstance().ACTUAL_TIMEOUT);
    }

    /**
     * Wait until page's title contains new text.
     *
     * @param newTitle wait for this title
     * @param timeOut  in seconds
     * @return true / false - title is same before and after
     */
    public boolean isTitleRefreshed(String newTitle, int timeOut) {
        if (timeOut < 0) {
            timeOut = 0;
        }

        return new WebDriverWait(webDriver, timeOut).until(ExpectedConditions.titleContains(newTitle));
    }

    /**
     * Get HTML source of the open page
     *
     * @return empty string when no 'html' tag is found
     */
    public String getPageSource() {
        WebElement bodyTag = isElementPresent(By.tagName("html"));
        return bodyTag == null ? "" : webDriver.getPageSource();
    }

    /**
     * Check if element is present on page
     *
     * @param by element locator
     * @return element on page
     */
    public Element isElementPresent(By by) {
        try {
            return findElement(by);
        } catch (NoSuchElementException e) {
            return null;
        }
    }

    protected Element findElement(By by) {
        return findElement(by, Element.class);
    }

    /**
     * Extends functionality of {@link WebElement#findElement(By)} to work with custom elements
     *
     * @param by          WebElement locator
     * @param elementType interface type of requested element (e.g. Table.class)
     * @param <T>         a custom WebElement interface defined by elementType
     * @return a WebElement wrapped in custom interface
     */
    protected <T extends Element> T findElement(By by, Class<T> elementType) {
        return ElementFinder.findElement(by, webDriver, elementType);
    }

    /**
     * Check if text is present within page's body tag
     *
     * @param text - searched text
     *             - note - there is some issue with searching in source text
     * @return info if text exists on page
     */
    public boolean verifyIfTextPresent(String text) {
        return verifyIfTextPresent(text, By.tagName("body"));
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
        return tag != null && tag.getText().contains(text);
    }

    /**
     * Check if text is present within page's body tag
     *
     * @param regex - searched substring as a regular expression
     *              - note - there is some issue with searching in source text
     * @return info if text exists on page
     */
    public boolean verifyIfTextPresentByRegex(String regex) {
        return verifyIfTextPresentByRegex(regex, By.tagName("body"));
    }

    /**
     * Check if text is present within page's desired tag
     *
     * @param regex searched substring as a regular expression
     * @param by    element locator
     * @return true if text is present somewhere inside the page element, false otherwise
     */
    public boolean verifyIfTextPresentByRegex(String regex, By by) {
        WebElement tag = isElementPresent(by);
        regex = "(?s:.*)" + regex + "(?s:.*)";
        return tag != null && tag.getText().matches(regex);
    }

    /**
     * Immediately check if the element is present, without any waiting
     * After use, the default timeout value is set to 60 sec.
     *
     * @param by element locator
     * @return true if element is present on the page, false otherwise
     */
    protected boolean isElementPresentNow(By by) {
        try {
            wait.turnOffImplicitTimeout();
            webDriver.findElement(by);
            return true;
        } catch (NoSuchElementException e) {
            return false;
        } finally {
            wait.turnOnImplicitTimeout(EndToEndProperties.getInstance().ACTUAL_TIMEOUT);
        }
    }

    /**
     * Turn off implicit timeout
     * This is mainly used when explicit waiting is needed
     */
    @Deprecated
    protected void turnOffImplicitTimeout() {
        wait.turnOffImplicitTimeout();
    }

    /**
     * Turn on implicit timeout
     * This is used after explicit waiting finished its work
     *
     * @param seconds - setUp implicit timeout
     */
    @Deprecated
    protected void turnOnImplicitTimeout(int seconds) {
        wait.turnOnImplicitTimeout(seconds);
    }

    /**
     * Check if element is present on page through wait call - Ajax
     *
     * @param by      element locator
     * @param timeout The time which driver will waite before star verification
     * @return element exists on page
     */
    @Deprecated
    public WebElement isElementPresent(By by, int timeout) {
        return wait.isElementPresent(by, timeout);
    }

    /**
     * Check if element is visible on page through wait call - Ajax
     *
     * @param by      element locator
     * @param timeout The time which driver will waite before star verification
     * @return element exists on page
     */
    @Deprecated
    public WebElement isElementVisible(By by, int timeout) {
        return wait.isElementVisible(by, timeout);
    }

    /**
     * Create fluent wait with polling 1 sec
     * ignoring StaleElementReferenceException
     * <p>
     * example usage:
     * <code>
     * fluentWait.until(new Predicate&lt;WebElement&gt;() {
     * </code>
     *
     * @param element - element is queried through pageFactory or webDriver
     * @param timeout - how long should fluentWait be waiting
     * @param <T>     - The input type for each condition used with this instance.
     * @return fluent wait object for currently used WebDriver instance
     */
    @Deprecated
    protected <T> FluentWait<T> createFluentWait(final T element, int timeout) {
        return wait.createFluentWait(element, timeout);
    }

    /**
     * Switch to newly opened window through window handle
     * You should switch back / close window after your actions on the new window are done
     * switchToWindow(mwh) -&gt; change active back to caller
     * note: when more than two windows are open this wont work
     *
     * @return name of the main active window handle
     */
    protected String switchToNewWindow() {
        String mainWindowHandle = webDriver.getWindowHandle();

        Set<String> allHandler = webDriver.getWindowHandles();

        if (allHandler.size() >= 1) {
            for (String currentHandle : allHandler) {
                if (!currentHandle.contains(mainWindowHandle)) {
                    webDriver.switchTo().window(currentHandle);
                    break;
                }
            }
        }

        return mainWindowHandle;
    }

    /**
     * Switch to window passed by handle
     *
     * @param mainWindowHandle selenium's browser window handle
     */
    protected void switchToWindow(String mainWindowHandle) {
        webDriver.switchTo().window(mainWindowHandle);
    }

    /**
     * Accept confirmation dialog
     */
    public void acceptAlert() {
        acceptAlert(EndToEndProperties.getInstance().ACTUAL_TIMEOUT);
    }

    /**
     * Accept confirmation dialog
     * specify time for wait on dialogue
     *
     * @param timeOut time during which waiting alert
     */
    public void acceptAlert(int timeOut) {
        Alert alert = wait.waitForAlert(timeOut);
        if (alert != null) {
            alert.accept();
        }
    }

    /**
     * Cancel, Dismiss - confirmation dialog
     * specify time for wait on dialogue
     *
     * @param timeOut - te
     */
    public void dismissAlert(int timeOut) {
        Alert alert = wait.waitForAlert(timeOut);
        if (alert != null) {
            alert.dismiss();
        }
    }

    /**
     * Cancel, Dismiss - confirmation dialog
     */
    public void dismissAlert() {
        dismissAlert(EndToEndProperties.getInstance().ACTUAL_TIMEOUT);
    }

    /**
     * Wait for config, alert modal window
     *
     * @param timeOut - default time for waiting
     * @return ModalDialogue window or null in case no Alert is present on the page
     */
    @Deprecated
    protected Alert waitForAlert(int timeOut) {
        return wait.waitForAlert(timeOut);
    }

    /**
     * Deselect current element by clicking on the 'body' element.
     */
    public void deselectAll() {
        findElement(By.tagName("body")).sendKeys("");
    }

    /**
     * Wait for element to be visible and clickable.
     *
     * @param locator WebElement locator
     * @param timeout maximum time to wait
     */
    @Deprecated
    protected void waitUntilElementIsVisible(By locator, int timeout) {
        wait.waitUntilElementIsVisible(locator, timeout);
    }

    /**
     * Wait until element disappear
     *
     * @param locator locator of WebElement
     * @param timeout timeout in seconds
     */
    @Deprecated
    protected void waitUntilElementDisappears(By locator, int timeout) {
        wait.waitUntilElementDisappears(locator, timeout);
    }

    /**
     * Get instance of webDriver
     *
     * @return currently used WebDriver
     */
    protected final WebDriver getWebDriver() {
        return webDriver;
    }

    /**
     * @param locator WebElement that should contain the text
     * @param text    desired content of WebElement
     * @return is text present?
     */
    @Deprecated
    protected boolean waitForTextInElement(By locator, String text) {
        return wait.waitForTextInElement(locator, text);
    }

    protected List<Element> findElements(By by) {
        return findElements(by, Element.class);
    }

    /**
     * Extends functionality of {@link WebElement#findElements(By)} to work with custom elements
     *
     * @param by          WebElement locator
     * @param elementType interface type of requested element (e.g. Table.class)
     * @param <T>         a custom WebElement interface defined by elementType
     * @return a list WebElements wrapped in custom interfaces
     */
    protected <T extends Element> List<T> findElements(By by, Class<T> elementType) {
        return ElementFinder.findElements(by, webDriver, elementType);
    }

    protected <T extends WebElement> void tryToFill(String data, T element) {
        tryToFill(data, element, EndToEndProperties.getInstance().ACTUAL_TIMEOUT);
    }

    /**
     * Fill element passed as argument.
     * If element is a Select then select by visible text.
     * Otherwise clear, sendKeys(data) and send TAB key.
     *
     * @param data    Text to be used as input. It may be null.
     * @param element Element to be filled with data.
     * @param <T>     Any type which extends WebElement
     * @param timeout - The time during implicit wait will work
     */
    protected <T extends WebElement> void tryToFill(String data, T element, int timeout) {
        wait.turnOnImplicitTimeout(timeout);
        if (data != null) {

            if (element.getTagName().equals("select")) {
                Select select = createSelectFromWebElement(element);
                try {
                    select.selectByVisibleText(data);
                } catch (Exception e) {
                    thinkTime(1); // wait before trying again
                    select.selectByVisibleText(data);
                }
            } else {
                element.clear();
                element.sendKeys(data + "\t");
            }
            wait.waitForPageToLoad();
        }
        wait.turnOnImplicitTimeout();
    }

    /**
     * Create Select wrapper from WebElement
     *
     * @param webElement select box element
     * @return representation of the box with selectable options
     */
    protected Select createSelectFromWebElement(WebElement webElement) {
        if (webElement == null) {
            throw new IllegalArgumentException("webElement cannot be null");
        }
        return new Select(webElement);
    }

    /**
     * Introduce think time so page can load correctly
     * Mainly support for ajax calls
     *
     * @param seconds - how many second test should wait
     */
    public void thinkTime(long seconds) {
        wait.thinkTime(seconds);
    }

    /**
     * Wait for document.readyState to be 'complete'
     */
    @Deprecated
    protected void waitForPageToLoad() {
        wait.waitForPageToLoad();
    }

    /**
     * Turn on implicit timeout
     * This is used after explicit waiting finished its work
     */
    @Deprecated
    protected void turnOnImplicitTimeout() {
        wait.turnOnImplicitTimeout();
    }

    /**
     * Verify if an element has current focus in the browser.
     *
     * @param element WebElement that should have the focus
     * @return true if focused, false otherwise
     */
    protected boolean hasFocus(Element element) {
        return element.equals(webDriver.switchTo().activeElement());
    }

}

