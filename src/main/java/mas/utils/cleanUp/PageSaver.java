package mas.utils.cleanUp;

import mas.utils.runTime.EndToEndProperties;
import org.openqa.selenium.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public final class PageSaver extends AbstractSaver {

    public static final String JS_REPLACE = "arguments[0].href=arguments[1]";
    private static Map<String, Integer> captured = new HashMap<>();
    private static String HTML_FILE_NAME_FORMAT = "failedPage-%s-err%02d.html";
    private static String TXT_FILE_NAME_FORMAT = "failedPage-%s-err%02d.txt";

    /**
     * Constructor
     *
     * @param driver Default WebDriver, which will be used for saving page
     */
    public PageSaver(WebDriver driver) {
        super(driver);
    }

    /**
     * Save page's content to target directory as TXT from the currently used WebDriver.
     *
     * @param testName Simple name of output file, without any path or extension.
     * @return saved whole page in txt format
     */
    public String saveAsTXT(String testName) {
        return saveAsTXT(testName, driver);
    }

    /**
     * Save page's content to target directory as TXT
     *
     * @param testName Simple name of output file, without any path or extension.
     * @param webDriver Default WebDriver
     * @return saved page context in txt format
     */
    public static String saveAsTXT(String testName, WebDriver webDriver) {
        if (webDriver == null) {
            return null;
        }
        String text;
        try {
            text = webDriver.findElement(By.xpath("//HTML")).getText();
        } catch (NoSuchSessionException e) {
            logger.error("Could not save page's text content.", e);
            return null;
        }
        String filename = getFileName(testName, captured, TXT_FILE_NAME_FORMAT);
        return saveToFile(filename, text, "Could not save page's text content.");
    }

    private static String saveToFile(String filename, String content, String msg) {
        try {
            Files.write(Paths.get(SAVE_DIRECTORY + filename), content.getBytes());
            return SAVE_DIRECTORY_URL + filename;
        } catch (IOException e) {
            logger.error(msg, e);
            return "";
        }
    }

    /**
     * Save page's source code to target directory as HTML from the currently used WebDriver.
     *
     * @param testName Simple name of output file, without any path or extension.
     * @return Saved page as HTML code
     */
    public String saveAsHTML(String testName) {
        return saveAsHTML(testName, driver);
    }

    /**
     * Save page's source code to target directory as HTML
     *
     * @param testName Simple name of output file, without any path or extension.
     * @param webDriver Default WebDriver, which will be used for saving page
     * @return Saved page as HTML code
     */
    public static String saveAsHTML(String testName, WebDriver webDriver) {
        if (webDriver == null) {
            return null;
        }
        String html;
        try {
            modifyHTML(webDriver);
            html = webDriver.getPageSource();
        } catch (NoSuchSessionException e) {
            logger.error("Could not save page's source code.", e);
            return null;
        }

        String filename = getFileName(testName, captured, HTML_FILE_NAME_FORMAT);
        return saveToFile(filename, html, "Could not save page's source code.");
    }

    /**
     * Change all references from relative to absolute links
     *
     * @param webDriver currently used driver
     */
    private static void modifyHTML(WebDriver webDriver) {
        if (EndToEndProperties.getInstance().MODIFY_PAGE_REFERENCES) {
            webDriver.manage().timeouts().implicitlyWait(1, TimeUnit.SECONDS);
            List<WebElement> elements = webDriver.findElements(By.xpath("//*[@href]"));
            final JavascriptExecutor jse = (JavascriptExecutor) webDriver;
            for (WebElement element : elements) {
                try {
                    String absoluteRef = element.getAttribute("href");
                    jse.executeScript(JS_REPLACE, element, absoluteRef);
                } catch (WebDriverException e) {
                    logger.error("Failed to modify a reference in HTML code before saving the page.");
                    break;
                }
            }
        }
    }
}
