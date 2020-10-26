package mas.controls;

import org.openqa.selenium.WebElement;

/**
 * Wrapper class like Select that wraps basic checkbox functionality.
 */
public class CustomCheckBox extends CustomElement implements CheckBox {

    /**
     * Wraps a WebElement with checkbox functionality.
     *
     * @param element to wrap up
     */
    public CustomCheckBox(WebElement element) {
        super(element);
    }

    @Override
    public String toString() {
        return getWrappedElement().toString();
    }

    @Override
       public void toggle() {
        getWrappedElement().click();
    }

    @Override
    public void check() {
        if (!isChecked()) {
            toggle();
        }
    }

    @Override
    public void uncheck() {
        if (isChecked()) {
            toggle();
        }
    }

    @Override
    public void set(boolean shouldCheck) {
        if (shouldCheck) {
            check();
        } else {
            uncheck();
        }
    }

    @Override
    public boolean isChecked() {
        return getWrappedElement().isSelected();
    }


}
