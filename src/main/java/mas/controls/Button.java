package mas.controls;


import mas.controls.internal.ImplementedBy;

@ImplementedBy(CustomButton.class)
public interface Button extends Element {

    /**
     * Click on this button.
     */
    @Override
    void click();

    /**
     * Get form_id if button is associated with a 'form' tag.
     *
     * @return form_id or null
     */
    String getAssociatedFormId();

    /**
     * Get button's type if present.
     * Usually 'button', 'reset' or 'submit'
     *
     * @return button type or null
     */
    String getButtonType();

    @Override
    String toString();
}
