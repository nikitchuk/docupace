package mas.utils.cleanUp;

import mas.utils.runTime.EndToEndProperties;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public final class CleanUpUtil {

    private Logger logger = LoggerFactory.getLogger(CleanUpUtil.class);
    private ScreenshotTaker screenshotTaker;
    private PageSaver pageSaver;

    public CleanUpUtil(WebDriver driver) {
        screenshotTaker = new ScreenshotTaker(driver);
        pageSaver = new PageSaver(driver);
    }

    public Evidence handleError(String name) {
        Evidence evidence = new Evidence();
        evidence.setScreenshotURL(handleScreenshot(name));
        evidence.setHtmlUrl(handleHTML(name));
        evidence.setTextUrl(handlePageText(name));

        printWarning(name, evidence);

        return evidence;
    }

    private String handleScreenshot(String name) {
        if (EndToEndProperties.getInstance().SAVE_SCREENSHOT) {
            return screenshotTaker.takeScreenshot(name);
        }
        return null;
    }

    private String handleHTML(String name) {
        if (EndToEndProperties.getInstance().SAVE_FAILED_PAGE_AS_HTML) {
            return pageSaver.saveAsHTML(name);
        }
        return null;
    }

    private String handlePageText(String name) {
        if (EndToEndProperties.getInstance().SAVE_FAILED_PAGE_AS_TXT) {
            return pageSaver.saveAsTXT(name);
        }
        return null;
    }

    private void printWarning(String name, Evidence evidence) {
        if (EndToEndProperties.getInstance().PRINT_WARNING_WHEN_ASSERTION_FAILS) {
            logger.error("Test failed at: {} - SAVING EVIDENCE {}{}{}",
                    name,
                    evidence.formatScreenshotUrl(),
                    evidence.formatTextUrl(),
                    evidence.formatHtmlUrl());
        }
    }
}
