package com.aerhard.oxygen.framework.inplace;

/**
 * An editable property of an XML document, for instance an attribute, an
 * attribute part or a text node.
 * 
 * @author Alexander Erhard
 *
 */
public class Editable {

    /**
     * A selector to retrieve the property from the input
     */
    private String selector;
    /**
     * The value of the property
     */
    private String value;
    /**
     * The UI label of the property
     */
    private String label;
    /**
     * The property's label on a UI button in the XML document
     */
    private String buttonLabel;
    /**
     * An array of predefined values for the attribute; properties with
     * predefined values are rendered as combo boxes, properties without as text
     * fields.
     */
    private String[] predefinedValues = null;
    /**
     * The labels of the predefined values as shown in the edit dialog.
     */
    private String[] predefinedValueLabels = null;
    /**
     * The labels of the predefined values as shown in a UI button's text.
     */
    private String[] predefinedValueLabelsButton = null;
    /**
     * Indicates if the current editable is active; if an editable is set
     * inactive, it gets hidden from display.
     */
    private Boolean active = true;

    /**
     * Editable constructor for text fields 
     * @param selector
     * @param label
     * @param buttonLabel
     */
    public Editable(String selector, String label, String buttonLabel) {
        this.selector = selector;
        this.label = label;
        this.buttonLabel = buttonLabel;
    }

    /**
     * Editable constructor for combo boxes
     * @param selector
     * @param label
     * @param buttonLabel
     * @param predefinedValues
     * @param predefinedValueLabels
     * @param predefinedValueLabelsButton
     */
    public Editable(String selector, String label, String buttonLabel,
            String[] predefinedValues, String[] predefinedValueLabels,
            String[] predefinedValueLabelsButton) {
        this.selector = selector;
        this.label = label;
        this.buttonLabel = buttonLabel;
        this.predefinedValues = predefinedValues;
        this.predefinedValueLabels = predefinedValueLabels;
        this.setPredefinedValueLabelsButton(predefinedValueLabelsButton);
    }

    /**
     * @return the name
     */
    public String getSelector() {
        return selector;
    }

    /**
     * @param selector
     *            the selector to set
     */
    public void setSelector(String selector) {
        this.selector = selector;
    }

    /**
     * @return the value
     */
    public String getValue() {
        return value;
    }

    /**
     * @param value
     *            the value to set
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * @return the label
     */
    public String getLabel() {
        return label;
    }

    /**
     * @param label
     *            the label to set
     */
    public void setLabel(String label) {
        this.label = label;
    }

    /**
     * @return the buttonLabel
     */
    public String getButtonLabel() {
        return buttonLabel;
    }

    /**
     * @param buttonLabel
     *            the buttonLabel to set
     */
    public void setButtonLabel(String buttonLabel) {
        this.buttonLabel = buttonLabel;
    }

    public String[] getPredefinedValues() {
        return predefinedValues;
    }

    public void setPredefinedValues(String[] predefinedValues) {
        this.predefinedValues = predefinedValues;
    }

    public String[] getPredefinedValueLabels() {
        return predefinedValueLabels;
    }

    public void setPredefinedValueLabels(String[] predefinedValueLabels) {
        this.predefinedValueLabels = predefinedValueLabels;
    }

    public String[] getPredefinedValueLabelsButton() {
        return predefinedValueLabelsButton;
    }

    public void setPredefinedValueLabelsButton(
            String[] predefinedValueLabelsButton) {
        this.predefinedValueLabelsButton = predefinedValueLabelsButton;
    }

    public Boolean isActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

}
