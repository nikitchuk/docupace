package mas.utils.cleanUp;

import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URLEncoder;
import java.nio.file.Paths;
import java.util.Map;

public abstract class AbstractSaver {

    protected static final Logger logger = LoggerFactory.getLogger(AbstractSaver.class);

    protected static final String SAVE_DIRECTORY = System.getProperty("screenshotDir", "target/");
    protected static final String SAVE_DIRECTORY_URL = System.getProperty("screenshotURL", getURLBase());
    protected final WebDriver driver;


    /**
     * Constructor
     *
     * @param driver Default WebDriver, which will be used for saving page
     */
    public AbstractSaver(WebDriver driver) {
        this.driver = driver;
    }

    /**
     * Get currently used directory in form of URL.
     *
     * @return Current path.
     */
    private static String getURLBase() {
        try {
            return Paths.get(SAVE_DIRECTORY).toUri().toURL().toString();
        } catch (MalformedURLException e) {
            return "file://" + new File(SAVE_DIRECTORY).getAbsolutePath() + File.separator;
        }
    }

    /**
     * Create formatted filename for page where the test failed
     *
     * @param fileName Simple name of output file, without any path or extension
     * @param format - template
     * @param captured - increment file name
     * @return failed page's complete filename
     */
    protected static String getFileName(String fileName, Map<String, Integer> captured, String format) {

        // GET NEXT NUMERIC SUFFIX
        if (captured.containsKey(fileName)) {
            captured.put(fileName, captured.get(fileName) + 1);
        } else {
            captured.put(fileName, 1);
        }

        int orderNum = captured.get(fileName);

        // ESCAPE INVALID CHARACTERS
        try {
            fileName = URLEncoder.encode(fileName, "UTF-8");
        } catch (UnsupportedEncodingException ignored) {
        }

        // TRIM FILENAME TO SUITABLE LENGTH
        if (fileName.length() > 200) {
            fileName = fileName.substring(0, 200);
        }

        return String.format(format, fileName, orderNum);
    }

}
