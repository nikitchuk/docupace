package mas.controls;

import org.openqa.selenium.WebElement;

/**
 * Wraps a label on a html form with some behavior.
 */
public class CustomLabel extends CustomElement implements Label {

    /**
     * Creates a Element for a given WebElement.
     *
     * @param element element to wrap up
     */
    public CustomLabel(WebElement element) {
        super(element);
    }

    @Override
    public String getFor() {
        return getWrappedElement().getAttribute("for");
    }

    @Override
    public String getText() {
        return super.getText();
    }

    @Override
    public String toString() {
        return getWrappedElement().toString();
    }
}
