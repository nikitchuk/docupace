package mas.controls.internal;

import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.DefaultElementLocatorFactory;
import org.openqa.selenium.support.pagefactory.ElementLocatorFactory;
import org.openqa.selenium.support.pagefactory.FieldDecorator;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * Element factory for wrapped elements. Similar to {@link PageFactory}
 */
public final class ElementFactory {

    /**
     * See {@link PageFactory#initElements(WebDriver driver, Class)}
     *
     * @param driver - The driver that will be used to look up the elements
     * @param pageClassToProxy  - A class which will be initialised.
     * @param <T> - Class of the PageObject
     * @return An instantiated instance of the class with fields proxied
     */
    public static <T> T initElements(WebDriver driver, Class<T> pageClassToProxy) {
        T page = instantiatePage(driver, pageClassToProxy);
        return initElements(driver, page);
    }

    /**
     * Copy of {@link PageFactory#instantiatePage(WebDriver, Class)}
     */
    private static <T> T instantiatePage(WebDriver driver, Class<T> pageClassToProxy) {
        try {
            try {
                Constructor<T> constructor = pageClassToProxy.getConstructor(WebDriver.class);
                return constructor.newInstance(driver);
            } catch (NoSuchMethodException e) {
                return pageClassToProxy.newInstance();
            }
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException("Could not instantiate page " + pageClassToProxy, e);
        }
    }

    /**
     * As
     * {@link ElementFactory#initElements(WebDriver, Class)}
     * but will only replace the fields of an already instantiated Page Object.
     *
     * @param searchContext A search context that will be used to look up the elements
     * @param page          The object with WebElement and <code>List{@literal <WebElement>}</code>fields that should be proxied.
     * @param <T> - Class of the PageObject
     * @return the initialized page-object.
     */
    public static <T> T initElements(SearchContext searchContext, T page) {
        initElements(new ElementDecorator(new DefaultElementLocatorFactory(searchContext)), page);
        return page;
    }

    /**
     * @param decorator     A decorator that will be used to decorate the elements
     * @param page          The object with WebElement and <code>List{@literal <WebElement>}</code>fields that should be proxied.
     * see {@link PageFactory#initElements(ElementLocatorFactory, Object)}
     */
    public static void initElements(FieldDecorator decorator, Object page) {
        PageFactory.initElements(decorator, page);
    }

    /**
     * see {@link PageFactory#initElements(ElementLocatorFactory, Object)}
     * @param factory for locating elements.
     * @param page          The object with WebElement and <code>List{@literal <WebElement>}</code>fields that should be proxied.
     */
    public static void initElements(final ElementLocatorFactory factory, Object page) {
        initElements(new ElementDecorator(factory), page);
    }
}
