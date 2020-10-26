package mas.utils.runTime;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;

public class JavaScript {

    private final JavascriptExecutor executor;

    public JavaScript(WebDriver driver) {
        this.executor = ((JavascriptExecutor) driver);
    }

    /**
     * Returns JavaScriptExecutor (created from WebDriver)
     *
     * @return JavaScript executor
     */
    public JavascriptExecutor getExecutor() {
        return executor;
    }

    /**
     * Process javascript code
     *
     * @param script -
     * @return return executed Javascript code
     */
    public Object call(String script) {
        return executor.executeScript(script);
    }

    /**
     * Execute custom javascript code on multiple elements.
     *
     * @param script   code to be executed
     * @param elements arguments for the script
     * @return execution result
     */
    public Object call(String script, List<WebElement> elements) {
        return executor.executeScript(script, elements);
    }

    /**
     * Click on element by invoking native action
     *
     * @param element - DOM element which should receive the click event
     */
    public void clickOnElement(WebElement element) {
        call("arguments[0].click()", element);
    }

    /**
     * Execute custom javascript code on some WebElement.
     *
     * @param script  code to be executed
     * @param element argument for the script
     * @return execution result
     */
    public Object call(String script, WebElement element) {
        return executor.executeScript(script, element);
    }
}
