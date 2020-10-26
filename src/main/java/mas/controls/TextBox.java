package mas.controls;


import mas.controls.internal.ImplementedBy;

/**
 * Text field functionality.
 */
@ImplementedBy(CustomTextBox.class)
public interface TextBox extends Element {
    /**
     * @param text The text to type into the field.
     */
    void fill(String text);

    /**
     * @param text add this parameter at the end of string
     */
    void add(String text);

    /**
     * @param number The text to type into the field.
     */
    void fill(Number number);

    /**
     * @param number add this parameter at the end of string
     */
    void add(Number number);

    void clear();

    /**
     * Gets the value of an input field.
     *
     * @return String with the value of the field.
     */
    String getText();

    /**
     * Clear content by sending keys Ctrl-A, Delete
     */
    void clearByDeletion();

    /**
     * Verify that the number of characters in this field is equal to 0.
     *
     * @return true if length is 0; false otherwise.
     */
    boolean isEmpty();

    @Override
    String toString();
}
