package mas.controls;


import mas.controls.internal.ImplementedBy;

@ImplementedBy(CustomHyperlink.class)
public interface Hyperlink extends Element {

    /**
     * Click on this hyperlink.
     */
    void click();

    /**
     * Get destination specified in href attribute.
     *
     * @return href value or null
     */
    String getDestinationAddress();

    /**
     * Get target frame or window, where the link should be opened.
     * Usually '_blank', '_self', '_parent', '_top' or frame's name
     *
     * It is not supported in HTML5!
     *
     * @return target value or null
     */
    String getTargetFrame();

    @Override
    String toString();
}
