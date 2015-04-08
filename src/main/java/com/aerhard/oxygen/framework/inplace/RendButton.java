package com.aerhard.oxygen.framework.inplace;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.aerhard.oxygen.framework.common.DocUtil;
import com.aerhard.oxygen.framework.inplace.EditButton;
import com.aerhard.oxygen.framework.inplace.Editable;

import ro.sync.annotations.api.API;
import ro.sync.annotations.api.APIType;
import ro.sync.annotations.api.SourceType;
import ro.sync.ecss.extensions.api.AuthorAccess;
import ro.sync.ecss.extensions.api.AuthorDocumentController;
import ro.sync.ecss.extensions.api.editor.AuthorInplaceContext;
import ro.sync.ecss.extensions.api.node.AuthorElement;

/**
 * A button opening an edit dialog for the contents of a TEI paragraph's rend attribute
 * @author Alexander Erhard
 *
 */
@API(type = APIType.INTERNAL, src = SourceType.PUBLIC)
public class RendButton extends EditButton {

    private String attribute = "rend";
    protected String matchingString = "(.*?)";
    protected String replaceMatchingString = ".*?";

    public RendButton() {
        initConfig();
    }

    public void initConfig() {
        titleProperty = "rendButton.titleText";
        emptyProperty = "rendButton.emptyText";
        prefixProperty = "rendButton.prefixText";

        String[] alignValues = { "", "left", "center", "right" };
        String[] alignValueLabels = i18n.getString("align.valueLabels").split(
                ",");

        editables = new Editable[] {
                new Editable("indent-left\\(" + matchingString + "chars\\)",
                        "rend.indent", "rend.indentButton"),
                new Editable("indent-first\\(" + matchingString + "chars\\)",
                        "rend.firstLineIndent", "rend.firstLineIndentButton"),
                new Editable("align\\(" + matchingString + "\\)",
                        "rend.alignment", "", alignValues, alignValueLabels,
                        alignValueLabels) };
    }

    @Override
    protected void getValuesFromDocument(AuthorInplaceContext context,
            AuthorElement element) {

        Boolean indent = ("true".equals(context.getArguments().get(
                "padding")));

        Boolean firstLineIndent = ("true".equals(context
                .getArguments().get("firstLineIndent")));

        String attValue = DocUtil.getAttributeOrEmptyString(element, attribute);

        setEditableValue(editables[0], indent, attValue);
        setEditableValue(editables[1], firstLineIndent, attValue);
        setEditableValue(editables[2], true, attValue);
    }

    private void setEditableValue(Editable editable, Boolean indent,
            String attValue) {
        Pattern p;
        Matcher m;
        String indentValue;
        if (indent) {
            p = Pattern.compile(editable.getSelector());
            m = p.matcher(attValue);
            indentValue = m.find() ? m.group(1) : "";
            editable.setActive(true);
            editable.setValue(indentValue);
        } else {
            editable.setActive(false);
        }
    }

    @Override
    protected void updateDocument(AuthorInplaceContext context,
            AuthorAccess authorAccess, AuthorElement element) {

        AuthorDocumentController controller = authorAccess
                .getDocumentController();

        String attValue = DocUtil.getAttributeOrEmptyString(element, attribute);
        
        String result = "";
        String regex;

        for (Editable editable : editables) {
            if (editable.isActive()) {
                regex = editable.getSelector();
                if (!editable.getValue().isEmpty()) {
                    result += regex.replace("\\", "")
                            .replace(matchingString, editable.getValue());
                }
                String replaceRegex = regex.replace(matchingString, replaceMatchingString);
                attValue = attValue.replaceAll(replaceRegex, "");
            }
        }
        DocUtil.modifyAttribute(controller, element, "rend", attValue+result);
    }

    /**
     * @see ro.sync.ecss.extensions.api.Extension#getDescription()
     */
    @Override
    public String getDescription() {
        return "A button to edit TEI rend attributes.";
    }

}