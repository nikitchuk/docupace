package mas.utils.runTime;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

public class Waiting {

    private static final String ACTIVE_JQUERY =
            "if (typeof jQuery != 'undefined') " +
                    "{return jQuery.active != 0;} " +
                    "else {return false}";
    private static final String ACTIVE_PRIME_FACES =
            "if (typeof PrimeFaces != 'undefined') " +
                    "{return !PrimeFaces.ajax.Queue.isEmpty();} " +
                    "else {return false;}";
    private static final String ACTIVE_WICKET =
            "if (typeof Wicket == 'object') {" +
                    "for (var c in Wicket.channelManager.channels) {" +
                    "  if (Wicket.channelManager.channels[c].busy) {" +
                    "    return true;" +
                    "  }" +
                    "}" +
                    "return false;" +
                    "} else {return false;}";
    private static final String DOCUMENT_READY =
            "return (document.readyState == 'complete')";
    private static final String SQA_ACTIVE_AJAX = "if (typeof sqaActiveAjax !== 'undefined') "
            + "{return sqaActiveAjax > 0;} "
            + "else {return false}";
    private static final String SQA_AJAX_INTERCEPTOR = "if (typeof sqaActiveAjax === 'undefined') {\n"
            + "  sqaActiveAjax = 0;\n"
            + "}\n"
            + "(function (open) {\n"
            + "  if (typeof XMLHttpRequest.prototype.sqaModified === 'undefined') {\n"
            + "    XMLHttpRequest.prototype.sqaModified = true;\n"
            + "    XMLHttpRequest.prototype.open = function (method, url, async, user, pass) {\n"
            + "      this.addEventListener('readystatechange', function () {\n"
            + "        if (this.readyState === 1) {\n"
            + "          sqaActiveAjax++;\n"
            + "        }\n"
            + "        if (this.readyState === 4) {\n"
            + "          sqaActiveAjax--;\n"
            + "        }\n"
            + "      }, false);\n"
            + "      open.call(this, method, url, async, user, pass);\n"
            + "    };\n"
            + "  }\n"
            + "}) (XMLHttpRequest.prototype.open);\n";
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final JavaScript javaScript;
    private final WebDriver webDriver;

    public Waiting(WebDriver driver) {
        webDriver = driver;
        this.javaScript = new JavaScript(driver);
    }

    private int defaultTimeout() {
        return 15;
    }

    /**
     * Wait for until all AJAX channels are closed.
     * First you need to call {@link #enableAjaxInterceptor()} to start capturing. (e.g. in Page's constructor)
     */
    public void waitForSQAAjaxInterceptor() {
        final Wait<WebDriver> wait = new WebDriverWait(webDriver, defaultTimeout(), defaultPolling())
                .withMessage("Waiting for all AJAX requests to complete.");
        wait.until(driver -> !((boolean) javaScript.call(SQA_ACTIVE_AJAX)));
    }

    public void waitForDocumentLoading() throws InterruptedException {
        JavaScript javascript = new JavaScript(webDriver);
        if (javascript.call("return $('#fileuploadform-files').val()").equals(1)){
            System.out.println("File uploaded");
            Thread.sleep(2000);
        } else  {
            Thread.sleep(4000);
        }
        System.out.println("Script result is "+ javascript.call("return $('#fileuploadform-files').val()"));
    }

    private int defaultPolling() {
        return EndToEndProperties.getInstance().POLLING_CYCLE;
    }

    /**
     * Enable capturing of all AJAX messages to count active channels.
     * This enables later calls to {@link #waitForSQAAjaxInterceptor()}
     */
    public void enableAjaxInterceptor() {
        javaScript.call(SQA_AJAX_INTERCEPTOR);
    }

    /**
     * Wait for document.readyState to be 'complete'
     */
    public void waitForPageToLoad() {
        waitForPageToLoad(defaultTimeout());
    }

    /**
     * Wait for document.readyState to be 'complete'
     *
     * @param timeout maximum time to wait
     */
    public void waitForPageToLoad(int timeout) {
        final Wait<WebDriver> wait = new WebDriverWait(webDriver, timeout, defaultPolling());
        wait.until(driver -> (Boolean) javaScript.call(DOCUMENT_READY));
    }

    /**
     * Check if element is present on page through wait call - Ajax
     *
     * @param webElement      element locator
     * @param timeout The time which driver will waite before star verification
     * @return element exists on page
     */
    public WebElement isElementVisible(WebElement webElement, int timeout) {
        try {
            return new WebDriverWait(webDriver, timeout)
                    .until(ExpectedConditions.visibilityOf(webElement));
        } catch (TimeoutException e) {
            return null;
        }
    }

    /**
     * Check if element is visible on page through wait call - Ajax
     *
     * @param by      element locator
     * @param timeout The time which driver will waite before star verification
     * @return element exists on page
     */
    public WebElement isElementVisible(By by, int timeout) {
        try {
            return new WebDriverWait(webDriver, timeout)
                    .until(ExpectedConditions.visibilityOfElementLocated(by));
        } catch (TimeoutException e) {
            return null;
        }
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
    public <T> FluentWait<T> createFluentWait(final T element, int timeout) {
        return new FluentWait<>(element)
                .withTimeout(Duration.ofSeconds(timeout))
                .pollingEvery(Duration.ofSeconds(1))
                .ignoring(StaleElementReferenceException.class);
    }

    public FluentWait<WebDriver> createFluentWait() {
        return new WebDriverWait(webDriver, EndToEndProperties.getInstance().ACTUAL_TIMEOUT)
                .pollingEvery(Duration.ofSeconds(EndToEndProperties.getInstance().POLLING_CYCLE))
                .ignoring(StaleElementReferenceException.class);
    }

    /**
     * Wait for config, alert modal window
     *
     * @param timeOut - default time for waiting
     * @return ModalDialogue window or null in case no Alert is present on the page
     */
    public Alert waitForAlert(int timeOut) {
        try {
            // get dialogue - confirm, alert ...
            return (new WebDriverWait(webDriver, timeOut)).until((WebDriver webDriver) -> webDriver.switchTo().alert());
        } catch (NoAlertPresentException | TimeoutException ex) {
            return null;
        }
    }

    /**
     * Wait for element to be visible and clickable.
     *
     * @param element WebElement locator
     * @param timeout maximum time to wait
     */
    public void waitUntilElementIsVisible(WebElement element, int timeout) {
        Wait<WebDriver> wait = new FluentWait<>(webDriver)
                .withTimeout(Duration.ofSeconds((long)timeout))
                .pollingEvery(Duration.ofMillis((long)defaultPolling()))
                .ignoring(NoSuchElementException.class)
                .ignoring(StaleElementReferenceException.class);
        wait.until(ExpectedConditions.visibilityOf(element));
        wait.until(ExpectedConditions.elementToBeClickable(element));
    }



    public void waitUntilElementIsVisible(By locator, int timeout) {
        Wait<WebDriver> wait = new FluentWait<>(webDriver)
                .withTimeout(Duration.ofSeconds((long)timeout))
                .pollingEvery(Duration.ofMillis((long)defaultPolling()))
                .ignoring(NoSuchElementException.class)
                .ignoring(StaleElementReferenceException.class);
        wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
        wait.until(ExpectedConditions.elementToBeClickable(locator));
    }

    public void waitUntilElementIsInvisible(By locator, int timeout) {
        Wait<WebDriver> wait = new FluentWait<>(webDriver)
                .withTimeout(Duration.ofSeconds((long)timeout))
                .pollingEvery(Duration.ofMillis((long)defaultPolling()))
                .ignoring(NoSuchElementException.class)
                .ignoring(StaleElementReferenceException.class);
        wait.until(ExpectedConditions.invisibilityOfElementLocated(locator));
    }

    /**
     * Wait until element disappear
     *
     * @param locator locator of WebElement
     * @param timeout timeout in seconds
     */
    public void waitUntilElementDisappears(By locator, int timeout) {
        turnOffImplicitTimeout();
        Wait<WebDriver> wait = new FluentWait<>(webDriver)
                .withTimeout(Duration.ofSeconds((long)timeout))
                .pollingEvery(Duration.ofMillis((long)defaultPolling()))
                .ignoring(NoSuchElementException.class);

        wait.until(ExpectedConditions.invisibilityOfElementLocated(locator));
        turnOnImplicitTimeout(defaultTimeout());
    }

    /**
     * Turn off implicit timeout
     * This is mainly used when explicit waiting is needed
     */
    public void turnOffImplicitTimeout() {
        webDriver.manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);
    }

    /**
     * Turn on implicit timeout
     * This is used after explicit waiting finished its work
     *
     * @param seconds - setUp implicit timeout
     */
    public void turnOnImplicitTimeout(int seconds) {
        webDriver.manage().timeouts().implicitlyWait(seconds, TimeUnit.SECONDS);
        EndToEndProperties.getInstance().ACTUAL_TIMEOUT = seconds;
    }

    /**
     * @param locator WebElement that should contain the text
     * @param text    desired content of WebElement
     * @return is text present?
     */
    public boolean waitForTextInElement(By locator, String text) {
        WebDriverWait wait = new WebDriverWait(webDriver, defaultTimeout());
        return wait.until(ExpectedConditions.textToBePresentInElementLocated(locator, text));
    }

    /**
     * Turn on implicit timeout
     * This is used after explicit waiting finished its work
     */
    public void turnOnImplicitTimeout() {
        turnOnImplicitTimeout(EndToEndProperties.getInstance().DEFAULT_TIMEOUT);
    }

    public void waitForAjax() {
        waitForAjax(defaultTimeout());
    }

    public void waitForAjax(int timeout) {
        final Wait<WebDriver> wait = new WebDriverWait(webDriver, timeout, defaultPolling());
        wait.until(driver ->
                !((boolean) javaScript.call(ACTIVE_JQUERY))
                        && !((boolean) javaScript.call(ACTIVE_PRIME_FACES))
                        && !((boolean) javaScript.call(ACTIVE_WICKET))
        );
    }

    /**
     * Introduce think time so page can load correctly
     * Mainly support for ajax calls
     *
     * @param seconds - how many second test should wait
     */
    public void thinkTime(long seconds) {
        try {
            Thread.sleep(seconds * 1000);
        } catch (InterruptedException e) {
            logger.error("Error while suspending a thread.", e);
        }
    }

    public void waitForElementToBePresent(WebElement element) {
        waitUntilElementIsVisible(element, defaultTimeout());
    }

    public void until(ExpectedCondition<WebElement> elementToBeClickable) {
    }

    //Wait Until JS Ready
    public void waitUntilFileUploaded() {
        WebDriverWait wait = new WebDriverWait(webDriver, defaultTimeout());

        //Wait for Javascript to load
        ExpectedCondition<Boolean> jsLoad = driver -> (javaScript).call("return document.readyState").toString().equals("complete");

        //Get JS is Ready
        boolean jsReady = javaScript.call("return document.readyState").toString().equals("complete");

        //Wait Javascript until it is Ready!
        if(!jsReady) {
            System.out.println("JS in NOT Ready!");
            //Wait for Javascript to load
            wait.until(jsLoad);
        } else {
            System.out.println("JS is Ready!");
        }
    }

    //Wait Until JS Ready
    public void waitUntilJSReady() {
        WebDriverWait wait = new WebDriverWait(webDriver, defaultTimeout());

        //Wait for Javascript to load
        ExpectedCondition<Boolean> jsLoad = driver -> (javaScript).call("return document.readyState").toString().equals("complete");

        //Get JS is Ready
        boolean jsReady = javaScript.call("return document.readyState").toString().equals("complete");

        //Wait Javascript until it is Ready!
        if(!jsReady) {
            System.out.println("JS in NOT Ready!");
            //Wait for Javascript to load
            wait.until(jsLoad);
        } else {
            System.out.println("JS is Ready!");
        }
    }

    public WebElement isElementPresent(By by, int timeout) {
        try {
            return new WebDriverWait(webDriver, timeout)
                    .until(ExpectedConditions.presenceOfElementLocated(by));
        } catch (TimeoutException e) {
            return null;
        }
    }

    public void sleep(long seconds) {
        try {
            TimeUnit.SECONDS.sleep(seconds);
        } catch (InterruptedException var4) {
            this.logger.warn("Got exception while waiting:", var4);
        }

    }

}
