package mas.controls;

import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * Wrapper around a WebElement for the Select class in Selenium.
 */
public class CustomSelect extends CustomElement implements Select {
    private final org.openqa.selenium.support.ui.Select innerSelect;

    /**
     * Wraps a WebElement with checkbox functionality.
     *
     * @param element to wrap up
     */
    public CustomSelect(WebElement element) {
        super(element);
        this.innerSelect = new org.openqa.selenium.support.ui.Select(element);
    }

    @Override
    public boolean isMultiple() {
        return innerSelect.isMultiple();
    }

    @Override
    public void deselectByIndex(int index) {
        innerSelect.deselectByIndex(index);
    }

    @Override
    public void selectByValue(String value) {
        try {
            innerSelect.selectByValue(value);
        } catch (NoSuchElementException e) {
            throw new NoSuchElementException("Option not found in select.\nAvailable options are:\n" + getTextsOfAllOptions(), e);
        }
    }

    @Override
    public List<String> getValuesOfAllOptions() {
        return getOptions().stream().map(option -> option.getAttribute("value")).collect(Collectors.toList());
    }

    @Override
    public WebElement getFirstSelectedOption() {
        return innerSelect.getFirstSelectedOption();
    }

    @Override
    public void selectByVisibleText(String text) {
        try {
            innerSelect.selectByVisibleText(text);
        } catch (NoSuchElementException e) {
            throw new NoSuchElementException("Option not found in select.\nAvailable options are:\n" + getTextsOfAllOptions(), e);
        }
    }

    @Override
    public void deselectByValue(String value) {
        innerSelect.deselectByValue(value);
    }

    @Override
    public void deselectAll() {
        innerSelect.deselectAll();
    }

    @Override
    public List<WebElement> getAllSelectedOptions() {
        return innerSelect.getAllSelectedOptions();
    }

    @Override
    public List<WebElement> getOptions() {
        return innerSelect.getOptions();
    }

    @Override
    public void deselectByVisibleText(String text) {
        innerSelect.deselectByVisibleText(text);
    }

    @Override
    public void selectByIndex(int index) {
        innerSelect.selectByIndex(index);
    }

    @Override
    public String getTextOfFirstSelectedOption() {
        return getFirstSelectedOption().getText();
    }

    @Override
    public List<String> getTextsOfAllOptions() {
        return getOptions().stream().map(WebElement::getText).collect(Collectors.toList());
    }

    @Override
    public void selectByRegex(String regex) {
        List<String> options = getTextsOfAllOptions();
        for (String option : options) {
            if (option.matches(regex)) {
                innerSelect.selectByVisibleText(option);
                return;
            }
        }
    }

    @Override
    public int getNumberOfOptions() {
        return innerSelect.getOptions().size();
    }

    @Override
    public String selectRandomOption(boolean includeFirstOption) {
        int index;
        if (includeFirstOption) {
            index = new Random().nextInt(innerSelect.getOptions().size());
        } else {
            index = 1 + new Random().nextInt(innerSelect.getOptions().size() - 1);
        }
        selectByIndex(index);
        return getTextsOfAllOptions().get(index);
    }

    @Override
    public String toString() {
        return getWrappedElement().toString();
    }
}
