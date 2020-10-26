package mas.controls;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Table wrapper.
 */
public class CustomTable extends CustomElement implements Table {
    /**
     * Creates a Table for a given WebElement.
     *
     * @param element element to wrap up
     */
    public CustomTable(WebElement element) {
        super(element);
    }

    @Override
    public int getRowCount() {
        return getRows().size();
    }

    @Override
    public int getColumnCount() {
        return findElements(By.xpath(".//tr[1]/*")).size();
    }

    @Override
    public WebElement getCellAtIndex(int rowIdx, int colIdx) {
        // Get the row at the specified index
        WebElement row = getRows().get(rowIdx);

        List<WebElement> cells;

        // Cells are most likely to be td tags
        if ((cells = row.findElements(By.tagName("td"))).size() > 0) {
            return cells.get(colIdx);
        }
        // Failing that try th tags
        else if ((cells = row.findElements(By.tagName("th"))).size() > 0) {
            return cells.get(colIdx);
        } else {
            final String error = String
                    .format("Could not find cell at row: %s column: %s",
                            rowIdx, colIdx);
            throw new RuntimeException(error);
        }
    }

    @Override
    public int getColumnIndexByText(String text) {
        if (getWrappedElement().findElements(By.xpath("//tr/*[text()=\"" + text + "\" or .//*[text()=\"" + text + "\"]]"))
                .size() == 0) {
            return -1;
        } else {
            return getWrappedElement().findElements(
                    By.xpath("(//tr/*[text()=\"" + text + "\" or .//*[text()=\"" + text + "\"]])[1]/preceding-sibling::*"))
                    .size();
        }
    }

    @Override
    public int getRowIndexByText(String text) {
        if (getWrappedElement().findElements(By.xpath("//tr[./*[text()=\"" + text + "\" or .//*[text()=\"" + text + "\"]]]"))
                .size() == 0) {
            return -1;
        } else {
            return getWrappedElement().findElements(
                    By.xpath("(//tr[./*[text()=\"" + text + "\" or .//*[text()=\"" + text + "\"]]])[1]/preceding-sibling::*"))
                    .size();
        }
    }

    @Override
    public List<String> getHeadings() {
        return getWrappedElement()
                .findElements(By.xpath(".//th"))
                .stream()
                .map(WebElement::getText)
                .collect(Collectors.toList());
    }

    @Override
    public String getTextAtIndex(int rowIdx, int colIdx) {
        return getCellAtIndex(rowIdx, colIdx).getText();
    }

    /**
     * Gets all rows in the table in order header > body > footer
     *
     * @return list of row WebElements
     */
    private List<WebElement> getRows() {
        return findElements(By.xpath(".//tr"));
    }

    @Override
    public String toString() {
        return getWrappedElement().toString();
    }

}
