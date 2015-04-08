package com.aerhard.oxygen.framework.tei.button;

import com.aerhard.oxygen.framework.inplace.EditButton;
import com.aerhard.oxygen.framework.inplace.Editable;

import ro.sync.annotations.api.API;
import ro.sync.annotations.api.APIType;
import ro.sync.annotations.api.SourceType;

/**
 * A button opening an edit dialog for TEI date attribute
 * @author Alexander Erhard
 *
 */
@API(type = APIType.INTERNAL, src = SourceType.PUBLIC)
public class DateButton extends EditButton {

    /**
     * Constructor.
     */
    public DateButton() {
        initConfig();
    }

    public void initConfig() {
        ns = "http://www.tei-c.org/ns/1.0";
        titleProperty = "dateButton.titleText";
        emptyProperty = "dateButton.emptyText";

        String[] certValues = { "", "high", "medium", "low" };

        String[] certValueLabels = i18n.getString("cert.valueLabels")
                .split(",");
        String[] certValueLabelsButton = i18n.getString(
                "cert.valueLabelsButton").split(",");

        String[] precisionValues = { "", "medium" };

        String[] precisionValueLabels = i18n.getString("precision.valueLabels")
                .split(",");
        String[] precisionValueLabelsButton = i18n.getString(
                "precision.valueLabelsButton").split(",");

        editables = new Editable[] {
                new Editable("when", "date.when", "date.whenButton"),
                new Editable("notBefore", "date.notBefore",
                        "date.notBeforeButton"),
                new Editable("notAfter", "date.notAfter", "date.notAfterButton"),
                new Editable("from", "date.from", "date.fromButton"),
                new Editable("to", "date.to", "date.toButton"),
                new Editable("cert", "date.certainty", "", certValues,
                        certValueLabels, certValueLabelsButton),
                new Editable("precision", "date.precision", "",
                        precisionValues, precisionValueLabels,
                        precisionValueLabelsButton),
                new Editable("#text", "date.text", "date.textButton") };
    }

    /**
     * @see ro.sync.ecss.extensions.api.Extension#getDescription()
     */
    @Override
    public String getDescription() {
        return "A button to edit TEI date attributes.";
    }

}