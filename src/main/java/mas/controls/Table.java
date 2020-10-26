package mas.controls;

import mas.controls.internal.ImplementedBy;
import org.openqa.selenium.WebElement;

import java.util.List;


/**
 * Table functionality.
 */
@ImplementedBy(CustomTable.class)
public interface Table extends Element {

    /**
     * Gets the number of rows in the table
     *
     * @return int equal to the number of rows in the table
     */
    int getRowCount();

    /**
     * Gets the number of columns in the table
     *
     * @return int equal to the number of rows in the table
     */
    int getColumnCount();

    /**
     * Gets the WebElement of the cell at the specified index
     *
     * @param rowIdx The zero based index of the row
     * @param colIdx The zero based index of the column
     * @return the WebElement of the cell at the specified index
     */
    WebElement getCellAtIndex(int rowIdx, int colIdx);

    /**
     * Get index of the first column, that contains specified text
     *
     * @param text Text to be found in table
     * @return The zero based index or -1 if text is not found
     */
    int getColumnIndexByText(String text);

    /**
     * Get index of the first row, that contains specified text
     *
     * @param text Text to be found in table
     * @return The zero based index or -1 if text is not found
     */
    int getRowIndexByText(String text);

    /**
     * Get values of all table headings as a list
     *
     * @return values of all 'th' tags
     */
    List<String> getHeadings();

    /**
     * Gets text of the cell at the specified index
     *
     * @param rowIdx The zero based index of the row
     * @param colIdx The zero based index of the column
     * @return text of the cell at the specified index
     */
    String getTextAtIndex(int rowIdx, int colIdx);

    @Override
    String toString();
}
