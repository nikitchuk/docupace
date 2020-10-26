package mas.controls;

import org.openqa.selenium.WebElement;


public class CustomHyperlink extends CustomElement implements Hyperlink {
    /**
     * Wraps a WebElement with link functionality.
     *
     * @param element element to wrap up
     */
    public CustomHyperlink(WebElement element) {
        super(element);
    }

    @Override
    public String getDestinationAddress() {
        return getWrappedElement().getAttribute("href");
    }

    @Override
    public String getTargetFrame() {
        return getWrappedElement().getAttribute("target");
    }

    @Override
    public void click() {
        super.click();
    }

    @Override
    public String toString() {
        return getWrappedElement().toString();
    }
}
