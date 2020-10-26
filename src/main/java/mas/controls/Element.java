package mas.controls;

import mas.controls.internal.ImplementedBy;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Locatable;
import org.openqa.selenium.internal.WrapsElement;

import java.util.List;


/**
 * wraps a web element interface with extra functionality. Anything added here will be added to all descendants.
 */
@ImplementedBy(CustomElement.class)
public interface Element extends WebElement, WrapsElement, Locatable {
    /**
     * Returns true when the inner element is ready to be used.
     *
     * @return boolean true for an initialized WebElement, or false if we were somehow passed a null WebElement.
     */
    boolean elementWired();

    /**
     * Returns the content of element's 'value' attribute
     *
     * @return 'value' attribute
     */
    String getValue();

    /**
     * Extends functionality of {@link WebElement#findElement(By)} to work with custom elements
     *
     * @param by          WebElement locator
     * @param elementType interface type of requested element (e.g. Table.class)
     * @param <T>         a custom WebElement interface defined by elementType
     * @return a WebElement wrapped in custom interface
     */
    <T extends Element> T findElement(By by, Class<T> elementType);

    /**
     * Extends functionality of {@link WebElement#findElements(By)} to work with custom elements
     *
     * @param by          WebElement locator
     * @param elementType interface type of requested element (e.g. Table.class)
     * @param <T>         a custom WebElement interface defined by elementType
     * @return a list WebElements wrapped in custom interfaces
     */
    <T extends Element> List<T> findElements(By by, Class<T> elementType);

    /**
     * Extends functionality of {@link WebElement#findElement(By)} to work with custom elements
     *
     * @param by WebElement locator
     * @return a WebElement wrapped in custom interface
     */
    @Override
    Element findElement(By by);

    /**
     * Extends functionality of {@link WebElement#findElements(By)} to work with custom elements
     *
     * @param by WebElement locator
     * @return a list WebElements wrapped in custom interfaces
     */
    List<Element> findAllElements(By by);

    /**
     * Find parent element of this node
     *
     * @return parent tag
     */
    Element getParent();

    @Override
    String toString();
}
