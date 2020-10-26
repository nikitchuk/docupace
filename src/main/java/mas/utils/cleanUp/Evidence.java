package mas.utils.cleanUp;

import java.util.ArrayList;
import java.util.List;

public class Evidence {
    private String screenshotURL;
    private String htmlUrl;
    private String textUrl;

    public String getHtmlUrl() {
        return htmlUrl;
    }

    public void setHtmlUrl(String htmlUrl) {
        this.htmlUrl = htmlUrl;
    }

    public String getScreenshotURL() {
        return screenshotURL;
    }

    public void setScreenshotURL(String screenshotURL) {
        this.screenshotURL = screenshotURL;
    }

    public String getTextUrl() {
        return textUrl;
    }

    public void setTextUrl(String textUrl) {
        this.textUrl = textUrl;
    }

    public boolean hasScreenshot() {
        return screenshotURL != null && !screenshotURL.trim().isEmpty();
    }

    public boolean hasHtml() {
        return htmlUrl != null && !htmlUrl.trim().isEmpty();
    }

    public boolean hasText() {
        return textUrl != null && !textUrl.trim().isEmpty();
    }

    public String formatScreenshotUrl() {
        return screenshotURL == null ? "" : "\n" + screenshotURL;
    }

    public String formatHtmlUrl() {
        return htmlUrl == null ? "" : "\n" + htmlUrl;
    }

    public String formatTextUrl() {
        return textUrl == null ? "" : "\n" + textUrl;
    }

    public List<String> toList() {
        List<String> evidence = new ArrayList<>();
        if (screenshotURL != null) evidence.add(screenshotURL);
        if (htmlUrl != null) evidence.add(htmlUrl);
        if (textUrl != null) evidence.add(textUrl);
        if (evidence.isEmpty()) return null;
        return evidence;
    }
}
