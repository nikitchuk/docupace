package mas.utils.runTime;

import org.openqa.selenium.Proxy;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Properties;


public final class EndToEndProperties {
    private static final Properties properties = new Properties();

    /**
     * Instance of this class.
     */
    private static EndToEndProperties instance = null;

    /**
     * Display a warning message with links to saved files when test fails.
     */
    public boolean PRINT_WARNING_WHEN_ASSERTION_FAILS = true;

    /**
     * Print all related test scenarios when a test starts.
     */
    public boolean PRINT_SCENARIOS = true;

    /**
     * Available actions are: save screenshot, save source code, save text content
     */
    public boolean TAKE_ACTIONS_WHEN_ADDING_TO_COLLECTOR = true;

    /**
     * Display a message for each action on WebElement and WebDriver navigation.
     */
    public boolean LOG_WEB_DRIVER_ACTIONS = true;

    /**
     * Run Firefox through a local proxy server which logs all requests and responses in a HAR file.
     */
    public boolean LOG_FIREFOX_PERFORMANCE_DATA = false;

    /**
     * Display a message when a test starts or finishes, containing the test result and duration.
     */
    public boolean PRINT_TEST_RESULTS = true;

    /**
     * If a test fails, then save all the text content of currently loaded page.
     */
    public boolean SAVE_FAILED_PAGE_AS_TXT = false;

    /**
     * If a test fails, then save the source code of currently loaded page.
     */
    public boolean SAVE_FAILED_PAGE_AS_HTML = true;

    /**
     * If a test fails, then take a screenshot of currently loaded page.
     */
    public boolean SAVE_SCREENSHOT = true;

    /**
     * When saving a page's source code then:
     * TRUE - change all 'href' attributes from relative to absolute path
     * FALSE - leave the source code unchanged
     * <p>
     * It is useful to have all links remapped in order to view the saved page with all pictures and styles.
     */
    public boolean MODIFY_PAGE_REFERENCES = true;

    /**
     * Serves as backup value for ACTUAL_TIMEOUT
     */
    public int DEFAULT_TIMEOUT = 60;

    /**
     * Fail if WebElement is not found within this amount of time [in seconds]
     */
    public int ACTUAL_TIMEOUT = DEFAULT_TIMEOUT;

    /**
     * How often a WebElement should be checked for it's state [in milliseconds]
     */
    public int POLLING_CYCLE = 500;

    /**
     * Path to FIGlet font. Default is "./ascii_standard.flf".
     */
    public String FIGLET_FONT_LOCATION = "./ascii_standard.flf";

    /**
     * Path to location where eventLog should be saved.
     */
    public String EVENT_LOG = "target/eventLog.json";

    /**
     * Should the list of scenarios contain only build-relevant scenarios?
     */
    public boolean FILTER_SCENARIOS_BY_COUNTRY = true;

    /**
     * Ignore unit tests from parent classes?
     * Will only execute tests from designated class.
     */
    public boolean IGNORE_PARENT_TESTS = true;

    /**
     * If set to true then all screenshots would be saved with reduced bit-depth.
     * False leaves the screenshot unmodified.
     */
    public boolean COMPRESS_SCREENSHOTS = false;

    /**
     * If empty then proxy type in browser is set to {@link Proxy.ProxyType}
     * If set to 'default' then proxy settings are left unmodified
     * If set then proxy type is set to {@link Proxy.ProxyType#PAC} with specified URL
     */
    public String PROXY_CONFIG = "default";

    /**
     * Private constructor tries to load configuration from file
     */
    private EndToEndProperties() {
        final ClassLoader loader = Thread.currentThread().getContextClassLoader();
        final URL url = loader.getResource("endToEnd.properties");
        FileInputStream is = null;

        if (url == null) return;

        try {
            String file = url.getFile();
            is = new FileInputStream(file);
        } catch (IOException | NullPointerException e) {
            System.err.println("Failed to load configuration from " + url);
        }
        try {
            properties.load(is);
            PRINT_WARNING_WHEN_ASSERTION_FAILS = getProperty("printCollectorAssertionWarning", PRINT_WARNING_WHEN_ASSERTION_FAILS);
            PRINT_SCENARIOS = getProperty("printScenarios", PRINT_SCENARIOS);
            TAKE_ACTIONS_WHEN_ADDING_TO_COLLECTOR = getProperty("takeActionsWhenAddingToCollector", TAKE_ACTIONS_WHEN_ADDING_TO_COLLECTOR);
            LOG_WEB_DRIVER_ACTIONS = getProperty("logWebDriverActions", LOG_WEB_DRIVER_ACTIONS);
            LOG_FIREFOX_PERFORMANCE_DATA = getProperty("logFirefoxPerformanceData", LOG_FIREFOX_PERFORMANCE_DATA);
            PRINT_TEST_RESULTS = getProperty("printTestResults", PRINT_TEST_RESULTS);
            SAVE_FAILED_PAGE_AS_TXT = getProperty("saveFailedPageAsTXT", SAVE_FAILED_PAGE_AS_TXT);
            SAVE_FAILED_PAGE_AS_HTML = getProperty("saveFailedPageAsHTML", SAVE_FAILED_PAGE_AS_HTML);
            MODIFY_PAGE_REFERENCES = getProperty("modifyPageReferences", MODIFY_PAGE_REFERENCES);
            SAVE_SCREENSHOT = getProperty("saveScreenshot", SAVE_SCREENSHOT);
            DEFAULT_TIMEOUT = getProperty("defaultTimeout", DEFAULT_TIMEOUT);
            POLLING_CYCLE = getProperty("pollingDelay", POLLING_CYCLE);
            FIGLET_FONT_LOCATION = getProperty("figletFontLocation", FIGLET_FONT_LOCATION);
            EVENT_LOG = getProperty("eventLog", EVENT_LOG);
            FILTER_SCENARIOS_BY_COUNTRY = getProperty("filterScenarios", FILTER_SCENARIOS_BY_COUNTRY);
            IGNORE_PARENT_TESTS = getProperty("ignoreParentTests", IGNORE_PARENT_TESTS);
            PROXY_CONFIG = getProperty("proxyConfig", PROXY_CONFIG);
            COMPRESS_SCREENSHOTS = getProperty("compressScreenshots", COMPRESS_SCREENSHOTS);

            if (is != null) is.close();
        } catch (IOException e) {
            System.err.println("Failed to load configuration from " + url);
        }
    }

    /**
     * Get a property from configuration file if available
     * or return a default value otherwise.
     *
     * @param name     property name
     * @param original default value
     * @return defined configuration
     */
    private static boolean getProperty(String name, boolean original) {
        return Boolean.parseBoolean(properties.getProperty(name, String.valueOf(original)));
    }

    /**
     * Get a property from configuration file if available
     * or return a default value otherwise.
     *
     * @param name     property name
     * @param original default value
     * @return defined configuration
     */
    private static int getProperty(String name, int original) {
        return Integer.parseInt(properties.getProperty(name, String.valueOf(original)));
    }

    private static String getProperty(String name, String original) {
        return properties.getProperty(name, original);
    }

    /**
     * Return the instance of EndToEndProperties
     *
     * @return Single instance of EndToEndProperties
     */
    public static EndToEndProperties getInstance() {
        if (instance == null) {
            instance = new EndToEndProperties();
        }
        return instance;
    }

    /**
     * Country tested by this build
     */
    public Country COUNTRY = Country.NOT_SPECIFIED;
}

