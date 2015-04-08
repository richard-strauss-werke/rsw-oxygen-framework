package com.aerhard.oxygen.framework.mei.button;

import com.aerhard.oxygen.framework.inplace.EditButton;
import com.aerhard.oxygen.framework.inplace.Editable;

import ro.sync.annotations.api.API;
import ro.sync.annotations.api.APIType;
import ro.sync.annotations.api.SourceType;

/**
 * A button opening an edit dialog for MEI date attribute
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
        ns = "http://www.music-encoding.org/ns/mei";
        titleProperty = "dateButton.titleText";
        emptyProperty = "dateButton.emptyText";

        String[] certValues = { "", "high", "medium", "low" };
        String[] certValueLabels = i18n.getString("cert.valueLabels")
                .split(",");
        String[] certValueLabelsButton = i18n.getString(
                "cert.valueLabelsButton").split(",");

        editables = new Editable[] {
                new Editable("isodate", "date.when", "date.whenButton"),
                new Editable("notbefore", "date.notBefore",
                        "date.notBeforeButton"),
                new Editable("notA" + "after", "date.notAfter",
                        "date.notAfterButton"),
                new Editable("startdate", "date.from", "date.fromButton"),
                new Editable("enddate", "date.to", "date.toButton"),
                new Editable("cert", "date.certainty", "", certValues,
                        certValueLabels, certValueLabelsButton),
                new Editable("#text", "date.text", "date.textButton") };

    }

    /**
     * @see ro.sync.ecss.extensions.api.Extension#getDescription()
     */
    @Override
    public String getDescription() {
        return "A button to edit MEI date attributes.";
    }

}