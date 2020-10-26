package mas.utils;


import com.codepine.api.testrail.model.Run;
import io.github.cdimascio.dotenv.Dotenv;
import mas.models.common.enums.Browser;
import mas.models.common.enums.EnvironmentType;
import mas.models.common.enums.Machine;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public final class Config {
    //DotEnv
    private static Dotenv dotenv = Dotenv.load();
    private static final boolean FROM_ENV = Boolean.parseBoolean(dotenv.get("FROM_ENV")) || Boolean.parseBoolean(System.getProperty("fromEnv", "false"));

    public static final TimeUnit TESTS_TIMEOUT_UNIT = TimeUnit.MINUTES;
    public static final int TESTS_TIMEOUT_VALUE = 10;

    public static final Machine MACHINE = FROM_ENV ? Machine.valueOf(dotenv.get("MACHINE")) : Machine.valueOf(System.getProperty("machine", "LOCAL"));
    public static final Browser BROWSER = FROM_ENV ? Browser.valueOf(dotenv.get("BROWSER")) : Browser.valueOf(System.getProperty("browser", "CHROME"));
    public static final EnvironmentType ENV = FROM_ENV ? EnvironmentType.valueOf(dotenv.get("ENV")) : EnvironmentType.valueOf(System.getProperty("env", "DEMO"));


    public static Run TEST_RAIL_RUN;
    public static List<Integer> TEST_CASES_IDS = new ArrayList();


    /**
     * Credentials
     */
    public static final String LOGIN_ADMIN = FROM_ENV ? dotenv.get("LOGIN_ADMIN") : System.getProperty("loginAdmin", "dme");
    public static final String PASSWORD_ADMIN = FROM_ENV ? dotenv.get("PASSWORD_ADMIN") : System.getProperty("passwordAdmin", "test");


    /**
     * Language app
     */
    public static String COUNTRY_CODE = "EN";
    public static String COUNTRY_LANGUAGE = "en";

    private static Integer getCheckDif(String checkDif) {
        return Integer.parseInt(checkDif);
    }

    private Config() {
    }
}