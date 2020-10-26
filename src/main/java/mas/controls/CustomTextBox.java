package mas.controls;

import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;

/**
 * TextInput  wrapper.
 */
public class CustomTextBox extends CustomElement implements TextBox {
    /**
     * Creates a Element for a given WebElement.
     *
     * @param element element to wrap up
     */
    public CustomTextBox(WebElement element) {
        super(element);
    }

    @Override
    public void clear() {
        getWrappedElement().clear();
    }

    /**
     * Gets the value of an input field.
     *
     * @return String with the value of the field.
     */
    @Override
    public String getText() {
        String text = getWrappedElement().getText();
        String value = getWrappedElement().getAttribute("value");

        if (value != null && value.length() > 0) {
            return value;
        }
        return text;
    }

    @Override
    public String toString() {
        return getWrappedElement().toString();
    }

    @Override
    public void fill(String text) {
        WebElement element = getWrappedElement();
        element.clear();
        element.sendKeys(text);
    }

    @Override
    public void add(String text) {
        WebElement element = getWrappedElement();
        element.sendKeys(text);
    }

    @Override
    public void fill(Number number) {
        fill(number.toString());
    }

    @Override
    public void add(Number number) {
        add(number.toString());
    }

    @Override
    public void clearByDeletion() {
        WebElement element = getWrappedElement();
        element.sendKeys(Keys.CONTROL, "a");
        element.sendKeys(Keys.DELETE);
    }

    @Override
    public boolean isEmpty() {
        return getText().length() == 0;
    }
}
