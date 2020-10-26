package mas.utils.runTime;

import mas.models.common.enums.Browser;
import mas.models.common.enums.Machine;
import mas.watchers.WebDriverListener;
import org.openqa.selenium.Platform;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.ie.InternetExplorerOptions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.events.EventFiringWebDriver;
import org.openqa.selenium.support.events.WebDriverEventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.TimeUnit;

public class DriverWrapper {

    private static final Logger logger = LoggerFactory.getLogger(DriverWrapper.class);


    private static final String MAC_CHROME_PATH = "/src/main/resources/drivers/chromedriver/mac/chromedriver";
    private static final String UBUNTU_CHROME_PATH = "/src/main/resources/drivers/chromedriver/chromedriver";
    private static final String UBUNTU_FIREFOX_PATH = "/src/main/resources/drivers/geckodriver/geckodriver";
    private static final String WINDOWS_CHROME_PATH = "/src/main/resources/drivers/chromedriver/chromedriver.exe";
    private static final String WINDOWS_FIREFOX_PATH = "/src/main/resources/drivers/geckodriver/geckodriver.exe";
    private static final String WINDOWS_IE_PATH = "/src/main/resources/drivers/iedriver/IEDriverServer.exe";

    private static final String GRID_URL = "http://localhost:4444/wd/";

    private final String system = System.getProperty("os.name").toLowerCase();
    private final String path_prefix = new File("").getAbsolutePath();


    private static EventFiringWebDriver driver;

    public DriverWrapper(Browser browser, Machine machine) {
        initializeGuiBaseSetup(browser, machine);
    }

    private void initializeGuiBaseSetup(Browser browser, Machine machine) {

        try {
            setDriver(browser, machine);
            logger.info(machine.toString() + browser.toString() + " is starting...");
        } catch (WebDriverException e) {
            logger.warn("Error....." + e.getStackTrace());
        }
    }

    public EventFiringWebDriver getDriver() {
        return driver;
    }


    protected void setWebDriver(WebDriver driver) {

        DriverWrapper.driver = new EventFiringWebDriver(driver);
    }

    private void setDriver(Browser browser, Machine machine) {
        System.out.println(browser);
        switch (browser) {
            case CHROME:

                if (machine.equals(Machine.LOCAL)) {
                    setWebDriver(initChromeDriver());
                } else {
                    setWebDriver(initChromeRemoteDriver(browser));
                }
                break;
            case FIREFOX:
                if (machine.equals(Machine.LOCAL)) {
                    setWebDriver(initFirefoxDriver());
                } else {
                    setWebDriver(initFirefoxRemoteDriver(browser));
                }
                break;
            case IE:
                if (machine.equals(Machine.LOCAL)) {
                    setWebDriver(initIE());
                }
                break;
            default:
                throw new UnsupportedOperationException(String.format("WebDriver type '%s' is not supported.", browser));
        }
        if (EndToEndProperties.getInstance().LOG_WEB_DRIVER_ACTIONS)
            registerNewListener(new WebDriverListener());
        setUpDefaultTimeouts(60);
        driver.manage().window().maximize();
    }

    public void registerNewListener(WebDriverEventListener listener) {
        driver.register(listener);
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

    private WebDriver initChromeDriver() {
        System.out.println(system.startsWith("win"));
        if (system.startsWith("win")) {
            System.setProperty("webdriver.chrome.driver", path_prefix + WINDOWS_CHROME_PATH);
        } else if ("linux".equals(system)) {
            System.setProperty("webdriver.chrome.driver", path_prefix + UBUNTU_CHROME_PATH);
        }else if (system.startsWith("mac")) {
            System.setProperty("webdriver.chrome.driver", path_prefix + MAC_CHROME_PATH);
        }

        ChromeOptions options = new ChromeOptions();
//        options.addArguments("--headless");
        options.addArguments("--hide-scrollbars");
        options.addArguments("--disable-gpu");
        return new ChromeDriver(options);
    }

    private WebDriver initFirefoxDriver() {
        if (system.startsWith("win")) {
            System.setProperty("webdriver.gecko.driver", path_prefix + WINDOWS_FIREFOX_PATH);
        } else if ("linux".equals(system)) {
            System.setProperty("webdriver.gecko.driver", path_prefix + UBUNTU_FIREFOX_PATH);
        }
        FirefoxOptions options = new FirefoxOptions();
        options.setCapability("marionette", false);
        return new FirefoxDriver(options);
    }

    private WebDriver initIE() {
        if (system.startsWith("win")) {
            System.setProperty("webdriver.ie.driver",  path_prefix + WINDOWS_IE_PATH);
        } else if ("linux".equals(system)) {
            System.setProperty("webdriver.ie.driver", path_prefix + UBUNTU_FIREFOX_PATH);
        }
        InternetExplorerOptions options = new InternetExplorerOptions();
        options.setCapability("requireWindowFocus", true);
        options.setCapability(InternetExplorerDriver.INTRODUCE_FLAKINESS_BY_IGNORING_SECURITY_DOMAINS, true);
        return new InternetExplorerDriver(options);
    }

    private DesiredCapabilities setRemoteCapabilities(Browser browser) {
        String browserName = browser.toString().toLowerCase();
        DesiredCapabilities desiredCapabilities = DesiredCapabilities.firefox();
        desiredCapabilities.setBrowserName(browserName);
        desiredCapabilities.setPlatform(Platform.LINUX);
        return desiredCapabilities;
    }

    private WebDriver initRemoteDriver(DesiredCapabilities desiredCapabilities) {
        try {
            return new RemoteWebDriver(new URL(GRID_URL), desiredCapabilities);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return driver;
    }

    private WebDriver initChromeRemoteDriver(Browser browser) {
        DesiredCapabilities desiredCapabilities = setRemoteCapabilities(browser);
        return initRemoteDriver(desiredCapabilities);
    }

    private WebDriver initFirefoxRemoteDriver(Browser browser) {
        DesiredCapabilities desiredCapabilities = setRemoteCapabilities(browser);
        return initRemoteDriver(desiredCapabilities);
    }


}
