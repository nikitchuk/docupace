package mas.parent;

import mas.controls.internal.ElementFactory;
import mas.models.common.Context;
import mas.models.common.enums.Browser;
import mas.models.common.enums.EnvironmentType;
import mas.models.common.enums.Machine;
import mas.runner.ScreenshotTakingJUnit4ClassRunner;
import mas.utils.Config;
import mas.utils.cleanUp.CleanUpUtil;
import mas.utils.cleanUp.FinalCleanup;
import mas.utils.cleanUp.ScreenshotTakingErrorCollector;
import mas.utils.runTime.DriverWrapper;
import mas.utils.runTime.EndToEndProperties;
import mas.utils.runTime.Waiting;
import mas.watchers.CollectorGuard;
import mas.watchers.TestStartupWatcher;
import org.junit.Rule;
import org.junit.rules.TestName;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.events.EventFiringWebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;


@RunWith(ScreenshotTakingJUnit4ClassRunner.class)
public abstract class GenericTest {
    private static EventFiringWebDriver driver;
    protected Context context;
    protected EnvironmentType environment;
    protected Machine machine = Config.MACHINE;
    protected Browser browser = Config.BROWSER;
    protected Waiting wait;

    protected static final Logger logger = LoggerFactory.getLogger(GenericTest.class);
    @Rule
    public TestName testName = new TestName();
    @Rule
    public TestStartupWatcher startupWatcher = new TestStartupWatcher();
    private CleanUpUtil cleaner;
    @Rule
    public ScreenshotTakingErrorCollector collector = setUpErrorCollector();


    private ScreenshotTakingErrorCollector setUpErrorCollector() {
        if (CollectorGuard.getCollector() == null)
            CollectorGuard.setCollector(new ScreenshotTakingErrorCollector(cleaner));

        return CollectorGuard.getCollector();
    }

    protected void init() {
        if (driver == null) {
            DriverWrapper driverWrapper = new DriverWrapper(browser, machine);
            driver = driverWrapper.getDriver();
            this.cleaner = new CleanUpUtil(driver);
        }
        this.collector.setCleaner(this.cleaner);
    }

    /**
     * Get webDriver
     *
     * @return Currently used WebDriver
     */
    public WebDriver getWebDriver() {
        return driver;
    }

    protected void setWebDriver(final WebDriver driver) {
        GenericTest.driver = new EventFiringWebDriver(driver);
        cleaner = new CleanUpUtil(GenericTest.driver);
        collector.setCleaner(cleaner);
    }

    public CleanUpUtil getCleaner() {
        if (cleaner == null) cleaner = new CleanUpUtil(driver);
        return cleaner;
    }

    /**
     * Turn off implicit timeout
     * This is mainly used when explicit waiting is needed
     */
    protected void turnOffImplicitTimeout() {
        driver.manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);
    }

    /**
     * Turn on implicit timeout
     * This is used after explicit waiting finished its work
     */
    protected void turnOnImplicitTimeout() {
        turnOnImplicitTimeout(EndToEndProperties.getInstance().ACTUAL_TIMEOUT);
    }

    /**
     * Turn on implicit timeout
     * This is used after explicit waiting finished its work
     *
     * @param seconds - setUp implicit timeout
     */
    protected void turnOnImplicitTimeout(int seconds) {
        driver.manage().timeouts().implicitlyWait(seconds, TimeUnit.SECONDS);
        EndToEndProperties.getInstance().ACTUAL_TIMEOUT = seconds;
    }

    /**
     * Set up default timeouts for pageLoad, script and async tasks
     *
     * @param timeOut implicit timeouts in seconds
     */
    protected void setUpDefaultTimeouts(int timeOut) {
        driver.manage().timeouts().pageLoadTimeout(timeOut, TimeUnit.SECONDS);
        driver.manage().timeouts().implicitlyWait(timeOut, TimeUnit.SECONDS);
        driver.manage().timeouts().setScriptTimeout(timeOut, TimeUnit.SECONDS);
        EndToEndProperties.getInstance().ACTUAL_TIMEOUT = timeOut;
    }


    /**
     * Init page through factory and check if page content is valid
     *
     * @param pageClass page object to be used
     * @param <T>       should extend GenericPage
     * @return initialized page object
     */
    protected <T extends GenericPage> T addPageToFactory(Class<T> pageClass) {
        T newPage = ElementFactory.initElements(driver, pageClass);
        newPage.initPageControls();
        if (!newPage.isPageContentValid()) {           // validate content of the page
            throw new IllegalStateException(String.format(
                    "\nPage %s has incorrect content or it isn't the page you expected." +
                            "\nCurrent Url:   %s " +
                            "\nCurrent Title: %s", pageClass.toString(), newPage.getCurrentUrl(), newPage.getTitle()
            )
            );
        }
        return newPage;
    }

    /**
     * Releases driver
     * This can't be a components @After method since we need an open driver to take a screenshot in case of failure!
     */
    @FinalCleanup
    private void quitDriver() {
        if (driver != null) {
            driver.quit();
            driver = null;
        }
    }
}
