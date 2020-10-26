package mas.controls.internal;

import mas.controls.Element;
import org.openqa.selenium.By;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

public class ElementFinder {

    private static Logger logger = LoggerFactory.getLogger(ElementFinder.class);

    public static <T extends Element> T findElement(By by, SearchContext context, Class<T> elementType) {
        if (elementType.isAnnotationPresent(ImplementedBy.class)) {
            Class<?> clazz = getElementClass(elementType);
            if (Element.class.isAssignableFrom(clazz)) {
                WebElement newElement = context.findElement(by);
                try {
                    Constructor<?> constructor = clazz.getConstructors()[0];
                    Object e = constructor.newInstance(newElement);
                    return elementType.cast(e);
                } catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
                    logger.error("Cannot cast to custom element type {}.", elementType);
                }
            }
        }
        return null;
    }

    private static <T extends Element> Class<?> getElementClass(Class<T> elementType) {
        ImplementedBy annotation = elementType.getAnnotation(ImplementedBy.class);
        return annotation.value();
    }

    public static <T extends Element> List<T> findElements(By by, SearchContext context, Class<T> elementType) {
        if (elementType.isAnnotationPresent(ImplementedBy.class)) {
            Class<?> clazz = getElementClass(elementType);
            if (Element.class.isAssignableFrom(clazz)) {
                List<WebElement> newElements = context.findElements(by);
                try {
                    List<T> result = new ArrayList<>();
                    for (WebElement element : newElements) {
                        Constructor<?> constructor = clazz.getConstructors()[0];
                        Object e = constructor.newInstance(element);
                        result.add(elementType.cast(e));
                    }
                    return result;
                } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                    logger.error("Cannot cast to custom element type {}.", elementType);
                }

            }
        }
        return null;
    }
}
