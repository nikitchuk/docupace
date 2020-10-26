package masTest.ui;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import mas.parent.GenericTest;
import mas.services.api.WSConfig;
import mas.utils.Config;
import mas.utils.generators.GeneratorConfig;
import mas.utils.runTime.Waiting;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.junit.Assume;
import org.junit.Rule;
import org.junit.rules.ErrorCollector;
import org.junit.rules.RuleChain;
import org.junit.rules.TestRule;
import org.junit.rules.Timeout;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestContextManager;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import javax.ws.rs.core.Response;
import java.io.IOException;

@ContextConfiguration(classes = {WSConfig.class, GeneratorConfig.class},
        loader = AnnotationConfigContextLoader.class)
public abstract class BaseTest extends GenericTest {
    protected static ObjectMapper mapper = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, false)
            .configure(JsonGenerator.Feature.IGNORE_UNKNOWN, true)
            .configure(JsonParser.Feature.IGNORE_UNDEFINED, true);

    @Rule
    public ErrorCollector collector = new ErrorCollector();

    @Rule
    public final TestRule timeout = RuleChain
            .outerRule(new Timeout(Config.TESTS_TIMEOUT_VALUE, Config.TESTS_TIMEOUT_UNIT));


    protected BaseTest() {
        try {
            TestContextManager testContextManager = new TestContextManager(getClass());
            testContextManager.prepareTestInstance(this);



        } catch (Exception ex) {
            String message = "Services not connected";
            throw new RuntimeException(message, ex);
        }
        setDefaultLanguage();
        this.wait = new Waiting(getWebDriver());
    }

    private void setDefaultLanguage() {
        Config.COUNTRY_CODE = "EN";
        Config.COUNTRY_LANGUAGE = "en";
    }


    protected void logParsingError(Throwable e) {
        logger.error(e.getLocalizedMessage());
    }
}