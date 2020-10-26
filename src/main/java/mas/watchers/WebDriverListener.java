package mas.watchers;

import mas.utils.runTime.EndToEndProperties;
import mas.utils.runTime.Event;
import mas.utils.runTime.EventAppender;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.events.AbstractWebDriverEventListener;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static mas.utils.runTime.Event.Type;


public class WebDriverListener extends AbstractWebDriverEventListener {
    private static Set<String> safeScripts = new HashSet(Arrays.asList("arguments[0].href=arguments[1]", "return (document.readyState === 'complete')", "if (typeof jQuery !== 'undefined') {return jQuery.active !== 0;} else {return false}", "if (typeof PrimeFaces !== 'undefined') {return !PrimeFaces.ajax.Queue.isEmpty();} else {return false;}", "if (typeof Wicket === 'object') {for (let c in Wicket.channelManager.channels) {  if (Wicket.channelManager.channels[c].busy) {    return true;  }}return false;} else {return false;}", "try {   if (document.readyState !== 'complete') {     return false;   }   if (window.jQuery) {     if (window.jQuery.active) {       return false;     } else if (window.jQuery.ajax && window.jQuery.ajax.active) {       return false;     }   }   if (window.angular) {     if (!window.qa) {       window.qa = {         doneRendering: false       };     }     var injector = window.angular.element('body').injector();     var $rootScope = injector.get('$rootScope');     var $http = injector.get('$http');     var $timeout = injector.get('$timeout');     if ($rootScope.$$phase === '$apply'         || $rootScope.$$phase === '$digest'         || $http.pendingRequests.length !== 0) {       window.qa.doneRendering = false;       return false;     }     if (!window.qa.doneRendering) {       $timeout(function() {         window.qa.doneRendering = true;       }, 0);       return false;     }   }   return true; } catch (ex) {   return false; }", "if (typeof sqaActiveAjax !== 'undefined') {return sqaActiveAjax > 0;} else {return false}", "if (typeof sqaActiveAjax === 'undefined') {\n  sqaActiveAjax = 0;\n}\n(function (open) {\n  if (typeof XMLHttpRequest.prototype.sqaModified === 'undefined') {\n    XMLHttpRequest.prototype.sqaModified = true;\n    XMLHttpRequest.prototype.open = function (method, url, async, user, pass) {\n      this.addEventListener('readystatechange', function () {\n        if (this.readyState === 1) {\n          sqaActiveAjax++;\n        }\n        if (this.readyState === 4) {\n          sqaActiveAjax--;\n        }\n      }, false);\n      open.call(this, method, url, async, user, pass);\n    };\n  }\n}) (XMLHttpRequest.prototype.open);\n"));
    private String lastLocator;
    private String valueBefore;
    private long initTime;

    public WebDriverListener() {
    }

    public static void addSafeScript(String script) {
        safeScripts.add(script);
    }

    public void beforeNavigateTo(String url, WebDriver driver) {
        Event event = (new Event(Type.GO_TO)).setBefore(driver.getCurrentUrl()).setValue(url);
        this.append(event);
    }

    public void append(Event event) {
        EventAppender.logEvent(event);
    }

    public void beforeNavigateBack(WebDriver driver) {
        Event event = (new Event(Type.BACK)).setBefore(driver.getCurrentUrl());
        this.append(event);
    }

    public void beforeNavigateForward(WebDriver driver) {
        Event event = (new Event(Type.FORWARD)).setBefore(driver.getCurrentUrl());
        this.append(event);
    }

    public void beforeNavigateRefresh(WebDriver driver) {
        Event event = (new Event(Type.REFRESH)).setBefore(driver.getCurrentUrl());
        this.append(event);
    }

    public void afterFindBy(By by, WebElement element, WebDriver driver) {
        this.lastLocator = element == null ? by.toString() : this.lastLocator + " -> " + this.parseLocator(element);
    }

    public void beforeClickOn(WebElement element, WebDriver driver) {
        this.setTimeout(driver, 1);
        String locator = this.parseLocator(element);
        String text = this.getText(element);
        String snippet = this.getHTML(element);
        Event event = new Event(this.getClickType(element));
        this.setTimeout(driver, EndToEndProperties.getInstance().ACTUAL_TIMEOUT);
        event.setValue(text);
        event.setLocator(locator);
        event.setSnippet(snippet);
        this.append(event);
    }

    public void beforeChangeValueOf(WebElement element, WebDriver driver, CharSequence[] keysToSend) {
        this.valueBefore = this.getValue(element);
    }

    private String getValue(WebElement element) {
        try {
            String value = element.getAttribute("value");
            return value == null ? "" : value;
        } catch (StaleElementReferenceException | NullPointerException var3) {
            return "";
        }
    }

    public void afterChangeValueOf(WebElement element, WebDriver selenium, CharSequence[] keysToSend) {
        this.setTimeout(selenium, 1);
        String locator = this.parseLocator(element);
        String text = this.getValue(element);
        String snippet = this.getHTML(element);
        this.setTimeout(selenium, EndToEndProperties.getInstance().ACTUAL_TIMEOUT);
        Event event;
        if (text.isEmpty()) {
            event = (new Event(Type.CLEAR)).setBefore(this.valueBefore);
        } else if (this.valueBefore.isEmpty()) {
            event = (new Event(Type.FILL)).setValue(text).setAfter(text);
        } else if (text.startsWith(this.valueBefore) && text.length() != this.valueBefore.length()) {
            event = (new Event(Type.APPEND)).setBefore(this.valueBefore).setAfter(text).setValue(text.substring(this.valueBefore.length()));
        } else {
            event = (new Event(Type.CHANGE)).setBefore(this.valueBefore).setAfter(text);
        }

        event.setLocator(locator);
        event.setSnippet(snippet);
        this.append(event);
    }

    public void beforeScript(String script, WebDriver driver) {
        if (!safeScripts.contains(script)) {
            Event event = (new Event(Type.SCRIPT)).setValue(script);
            if (script.toLowerCase().contains("arguments[")) {
                event.setLocator(this.lastLocator);
            }

            this.append(event);
        }

    }

    private Type getClickType(WebElement element) {
        try {
            if ("option".equals(element.getTagName())) {
                return Type.SELECT;
            }
        } catch (StaleElementReferenceException | NullPointerException var2) {
            ;
        }

        return Type.CLICK;
    }

    private String getText(WebElement element) {
        try {
            String value = this.getValue(element);
            return value.isEmpty() ? element.getText() : value;
        } catch (StaleElementReferenceException | NullPointerException var3) {
            return "";
        }
    }

    private String parseLocator(WebElement element) {
        return element == null ? "" : this.parseLocator(element.toString());
    }

    private String parseLocator(String locator) {
        int arrow = locator.lastIndexOf(" -> ");
        if (arrow == -1) {
            return locator;
        } else {
            String head = this.parseLocator(locator.substring(1, arrow));
            String tail = locator.substring(arrow + 4, locator.length() - 1);
            int dif = StringUtils.countMatches(tail, "]") - StringUtils.countMatches(tail, "[");
            if (dif > 0) {
                head = head.substring(dif);
                tail = tail.substring(0, tail.length() - dif);
            }

            return head.matches("\\[\\w+Driver: [ (\\w-]+\\)\\]") ? tail : head + " -> " + tail;
        }
    }

    private void setTimeout(WebDriver driver, int time) {
        driver.manage().timeouts().implicitlyWait((long)time, TimeUnit.SECONDS);
    }

    private String getHTML(WebElement element) {
        try {
            return element.getAttribute("outerHTML");
        } catch (StaleElementReferenceException | NullPointerException var2) {
            return null;
        }
    }
}