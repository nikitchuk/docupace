package mas.controls;

import mas.controls.internal.ElementFinder;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Locatable;
import org.openqa.selenium.interactions.Coordinates;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * An implementation of the Element interface. Delegates its work to an underlying WebElement instance for
 * custom functionality.
 */
public class CustomElement implements Element {

    protected static final Logger logger = LoggerFactory.getLogger(CustomElement.class);
    private final WebElement element;

    /**
     * Creates a Element for a given WebElement.
     *
     * @param element element to wrap up
     */
    public CustomElement(final WebElement element) {
        this.element = element;
    }

    @Override
    public void click() {
        element.click();
    }

    @Override
    public void submit() {
        element.submit();
    }

    @Override
    public void sendKeys(CharSequence... keysToSend) {
        element.sendKeys(keysToSend);
    }

    @Override
    public void clear() {
        element.clear();
    }

    @Override
    public String getTagName() {
        return element.getTagName();
    }

    @Override
    public String getAttribute(String name) {
        return element.getAttribute(name);
    }

    @Override
    public boolean isSelected() {
        return element.isSelected();
    }

    @Override
    public boolean isEnabled() {
        return element.isEnabled();
    }

    @Override
    public String getText() {
        return element.getText();
    }

    @Override
    public List<WebElement> findElements(By by) {
        return element.findElements(by);
    }

    @Override
    public boolean isDisplayed() {
        return element.isDisplayed();
    }

    @Override
    public Point getLocation() {
        return element.getLocation();
    }

    @Override
    public Dimension getSize() {
        return element.getSize();
    }

    @Override
    public Rectangle getRect() {
        return element.getRect();
    }

    @Override
    public String getCssValue(String propertyName) {
        return element.getCssValue(propertyName);
    }

    @Override
    public WebElement getWrappedElement() {
        return element;
    }

    @Override
    public Coordinates getCoordinates() {
        return ((Locatable) element).getCoordinates();
    }

    @Override
    public boolean elementWired() {
        return (element != null);
    }

    @Override
    public String getValue() {
        return element.getAttribute("value");
    }

    @Override
    public <T extends Element> T findElement(By by, Class<T> elementType) {
        return ElementFinder.findElement(by, element, elementType);
    }

    @Override
    public <T extends Element> List<T> findElements(By by, Class<T> elementType) {
        return ElementFinder.findElements(by, element, elementType);
    }

    @Override
    public Element findElement(By by) {
        return findElement(by, Element.class);
    }

    @Override
    public List<Element> findAllElements(By by) {
        return findElements(by, Element.class);
    }

    @Override
    public Element getParent() {
        return findElement(By.xpath("./.."));
    }

    @Override
    public <X> X getScreenshotAs(OutputType<X> outputType) {
        return element.getScreenshotAs(outputType);
    }

    @Override
    public String toString() {
        return element.toString();
    }
}
