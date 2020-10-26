package mas.utils;

import java.util.Locale;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import static mas.utils.Config.COUNTRY_CODE;
import static mas.utils.Config.COUNTRY_LANGUAGE;

public class LocalReader {
    private static Locale locale = null;
    private static ResourceBundle resourceBundle = null;

    public static String getSting(String code) {
        locale = new Locale(COUNTRY_CODE, COUNTRY_LANGUAGE);
        resourceBundle = PropertyResourceBundle.getBundle("Localization", locale);
        return resourceBundle.getString(code).trim();
    }
}


