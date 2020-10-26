package mas.utils.runTime;

import java.util.ArrayList;
import java.util.List;

public class Event {

    private String locator;
    private String before;
    private String after;
    private String value;
    private Throwable throwable;
    private Type type;
    private long time = System.currentTimeMillis();
    private List<Event> children;
    private List<String> scenarios;
    private List<String> evidence;
    private String snippet;

    public Event(Type type) {
        this.type = type;
    }

    public String getSnippet() {
        return snippet;
    }

    public Event setSnippet(String snippet) {
        this.snippet = snippet;
        return this;
    }

    public List<String> getEvidence() {
        return evidence;
    }

    public Event setEvidence(List<String> evidence) {
        this.evidence = evidence;
        return this;
    }

    public List<String> getScenarios() {
        return scenarios;
    }

    public Event setScenarios(List<String> scenarios) {
        this.scenarios = scenarios;
        return this;
    }

    public List<Event> getChildren() {
        return children;
    }

    public Event setChildren(List<Event> events) {
        this.children = events;
        return this;
    }

    public void addChild(Event event) {
        if (children == null) children = new ArrayList<>();
        children.add(event);
    }

    public String getValue() {
        return value;
    }

    public Event setValue(String value) {
        this.value = value;
        return this;
    }

    public long getTime() {
        return time;
    }

    public Event setTime(long time) {
        this.time = time;
        return this;
    }

    public String getLocator() {
        return locator;
    }

    public Event setLocator(String locator) {
        this.locator = locator;
        return this;
    }

    public String getBefore() {
        return before;
    }

    public Event setBefore(String before) {
        this.before = before;
        return this;
    }

    public String getAfter() {
        return after;
    }

    public Event setAfter(String after) {
        this.after = after;
        return this;
    }

    public Type getType() {
        return type;
    }

    public Event setType(Type type) {
        this.type = type;
        return this;
    }

    public Throwable getThrowable() {
        if (throwable == null) return null;
        return throwable.getCause();
    }

    public Event setThrowable(Throwable throwable) {
        this.throwable = new Throwable(throwable);
        return this;
    }

    public enum Type {
        GO_TO, REFRESH, BACK, FORWARD,
        CLICK, CLEAR, APPEND, FILL, CHANGE, SELECT,
        WS_REQUEST, WS_RESPONSE,
        SCRIPT, ERROR, SKIP, TEST,
        MOCK, INFO, OTHER, QUERY
    }
}
