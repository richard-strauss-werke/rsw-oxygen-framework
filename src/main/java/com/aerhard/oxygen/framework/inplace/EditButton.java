package com.aerhard.oxygen.framework.inplace;

import java.awt.Component;
import java.util.Arrays;
import java.util.ResourceBundle;

import javax.swing.text.BadLocationException;

import org.apache.log4j.Logger;

import com.aerhard.oxygen.framework.common.DocUtil;

import ro.sync.annotations.api.API;
import ro.sync.annotations.api.APIType;
import ro.sync.annotations.api.SourceType;
import ro.sync.ecss.extensions.api.AuthorAccess;
import ro.sync.ecss.extensions.api.AuthorConstants;
import ro.sync.ecss.extensions.api.AuthorDocumentController;
import ro.sync.ecss.extensions.api.AuthorOperationException;
import ro.sync.ecss.extensions.api.editor.AuthorInplaceContext;
import ro.sync.ecss.extensions.api.node.AuthorElement;

/**
 * A UI button for displaying an element's properties in a custom format and opening an {@link EditDialog}.
 * @author Alexander Erhard
 *
 */
@API(type = APIType.EXTENDABLE, src = SourceType.PUBLIC)
public class EditButton extends InplaceButton {

    protected String titleProperty;
    protected String emptyProperty;
    protected String prefixProperty = null;

    /**
     * Logger for logging.
     */
    private static final Logger LOGGER = Logger.getLogger(EditButton.class
            .getName());

    /** The localization resource bundle. */
    protected ResourceBundle i18n;

    protected String ns = null;
    protected Editable[] editables = null;

    /**
     * Constructor.
     */
    public EditButton() {
        i18n = ResourceBundle.getBundle("Extensions");
    }

    @Override
    public void performAction(AuthorInplaceContext context,
            AuthorAccess authorAccess) {

        AuthorElement element = context.getElem();

        getValuesFromDocument(context, element);

        Boolean updateData = false;
        EditDialog dialog;

        try {
            dialog = new EditDialog(authorAccess.getWorkspaceAccess(), i18n.getString(titleProperty),
                    editables);
            dialog.setLocationRelativeTo((Component) authorAccess
                    .getWorkspaceAccess().getParentFrame());
            updateData = dialog.showDialog();
        } catch (Exception e1) {
            LOGGER.error(e1, e1);
        }

        if (updateData != null) {
             stopEditing(false);
            updateDocument(context, authorAccess, element);
        }
    }

    /**
     * Gets the editable values from the XML document
     * @param context
     * @param element
     */
    protected void getValuesFromDocument(AuthorInplaceContext context,
            AuthorElement element) {

        Boolean editText = ("true".equals(context.getArguments().get(
                "editText")));

        for (Editable editable : editables) {
            if (editable.getSelector().equals("#text")) {
                editable.setActive(editText);
                if (editText) {
                    String textContent;
                    try {
                        textContent = element.getTextContent();
                    } catch (BadLocationException e) {
                        LOGGER.error(e, e);
                        textContent = "";
                    }
                    editable.setValue(textContent);
                }
            } else {
                editable.setValue(DocUtil.getAttributeOrEmptyString(
                        element, editable.getSelector()));
            }
        }
    }

    /**
     * Writes the editable values to the XML document
     * @param context
     * @param authorAccess
     * @param element
     */
    protected void updateDocument(AuthorInplaceContext context,
            AuthorAccess authorAccess, AuthorElement element) {
        AuthorDocumentController controller = authorAccess
                .getDocumentController();
        controller.beginCompoundEdit();

        if ("true".equals(context.getArguments().get("editText"))) {
            String attributes = "";
            String text = "";

            for (Editable editable : editables) {
                if (editable.isActive()
                        && !editable.getValue().isEmpty()) {

                    if (editable.getSelector().equals("#text")) {
                        text += editable.getValue().replace("&", "&amp;")
                                .replace("<", "&lt;").replace("<", "&gt;");
                    } else {
                        attributes += " " + editable.getSelector() + "=\""
                                + editable.getValue() + "\"";
                    }
                }
            }

            String elementName = element.getName();
            String result = "<" + elementName + attributes + " xmlns=\"" + ns + "\">"
                    + text + "</" + elementName + ">";

            try {
                controller.insertXMLFragment(result, element,
                        AuthorConstants.POSITION_BEFORE);
            } catch (AuthorOperationException e1) {
                LOGGER.error(e1, e1);
            }
            controller.deleteNode(element);

        } else {
            for (Editable editable : editables) {
                if (editable.isActive()
                        && !editable.getSelector().equals("#text")) {
                    DocUtil.modifyAttribute(controller, element,
                            editable.getSelector(), editable.getValue());
                }

            }
        }
        controller.endCompoundEdit();
    }

    @Override
    public void setBtnText(AuthorInplaceContext context) {

        StringBuilder sb = new StringBuilder();
        
        String loopDelim = "";
        String innerDelim =", ";
        
        getValuesFromDocument(context, context.getElem());

        int index;

        for (Editable editable : editables) {
            if (editable.isActive() && !editable.getValue().isEmpty()) {
                sb.append(loopDelim);
                if (editable.getPredefinedValues() != null) {
                    index = Arrays.asList(editable.getPredefinedValues())
                            .indexOf(editable.getValue());
                    if (index != -1) {
                        sb.append("(").append(editable.getPredefinedValueLabelsButton()[index]).append(")");
                    }
                } else {
                    sb.append(String.format(i18n.getString(editable
                            .getButtonLabel()), editable.getValue()));
                }
                loopDelim = innerDelim;
            }
        }

        if (sb.length() == 0) {
            getNameBtn().setText(i18n.getString(emptyProperty));
        } else {
            sb.insert(0, prefixProperty == null ? "" : i18n
                            .getString(prefixProperty) + " ");
            getNameBtn().setText(sb.toString());
        }

    }

    /**
     * @see ro.sync.ecss.extensions.api.Extension#getDescription()
     */
    @Override
    public String getDescription() {
        return "An extendable button component to edit multiple attributes and the text node of an element.";
    }

}