package mas.controls;

import org.openqa.selenium.WebElement;

public class CustomButton extends CustomElement implements Button {

    /**
     * Wraps a WebElement with button functionality.
     *
     * @param element element to wrap up
     */
    public CustomButton(WebElement element) {
        super(element);
    }

    @Override
    public void click() {
        super.click();
    }

    @Override
    public String toString() {
        return getWrappedElement().toString();
    }

    @Override
    public String getAssociatedFormId() {
        return getWrappedElement().getAttribute("form");
    }

    @Override
    public String getButtonType() {
        return getWrappedElement().getAttribute("type");
    }
}
