package mas.controls;


import mas.controls.internal.ImplementedBy;

/**
 * Html form label.
 */
@ImplementedBy(CustomLabel.class)
public interface Label extends Element {
    /**
     * Gets the for attribute on the label.
     *
     * @return string containing value of the for attribute, null if empty.
     */
    String getFor();

    String getText();

    @Override
    String toString();
}
