package mas.models.common.enums;


import mas.utils.Config;

import java.util.Arrays;


enum ApplicationURL {

    GENERIC("https://{ENV}-qa.united.com");

    private static String environment;
    private static ApplicationURL self;
    private final String urlUnited;


    ApplicationURL(String urlUnited) {
        this.urlUnited = urlUnited;
    }

    static ApplicationURL getInstance() {
        if (self == null) {
            self = getInstance(Config.ENV.toString());
        }
        return self;
    }

    static ApplicationURL getInstance(String environmentCode) {
        String code = isEnumPresent(environmentCode) ? environmentCode.toUpperCase() : "GENERIC";
        environment = environmentCode.toLowerCase();
        return valueOf(code);
    }

    private static boolean isEnumPresent(String code) {
        return Arrays.toString(values()).contains(code);
    }

    private String resolvePlaceholders(String url) {
        return url.replace("{ENV}", environment);
    }

    public String getUrlUnited() {
        return resolvePlaceholders(urlUnited);
    }


}
