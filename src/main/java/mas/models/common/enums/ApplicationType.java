package mas.models.common.enums;

/**
 * Enum which realize each type of application which uses login page for access.
 */
public enum ApplicationType {


    UNITED(ApplicationURL.getInstance().getUrlUnited());


    private final String url;

    ApplicationType(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

}
