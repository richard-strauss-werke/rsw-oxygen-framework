package com.aerhard.oxygen.framework.inplace;

import com.aerhard.oxygen.plugin.dbtagger.SearchDialog;
import org.apache.log4j.Logger;
import ro.sync.annotations.api.API;
import ro.sync.annotations.api.APIType;
import ro.sync.annotations.api.SourceType;
import ro.sync.ecss.extensions.api.AuthorAccess;
import ro.sync.ecss.extensions.api.AuthorConstants;
import ro.sync.ecss.extensions.api.AuthorOperationException;
import ro.sync.ecss.extensions.api.access.AuthorWorkspaceAccess;
import ro.sync.ecss.extensions.api.editor.AuthorInplaceContext;
import ro.sync.ecss.extensions.api.node.AuthorElement;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import java.util.Map;

/**
 * A button opening an edit dialog for querying a name database and inserting a TEI
 * key reference to the selected record.
 *
 * @author Alexander Erhard
 */
@API(type = APIType.INTERNAL, src = SourceType.PUBLIC)
public class NameButton extends InplaceButton {

    /**
     * Logger for logging.
     */
    private static final Logger LOGGER = Logger.getLogger(NameButton.class
            .getName());

    private String ns;

    /**
     * @return the ns
     */
    public String getNs() {
        return ns;
    }

    /**
     * @param ns the ns to set
     */
    public void setNs(String ns) {
        this.ns = ns;
    }

    /**
     * @param editableAttribute the editableAttribute to set
     */
    public void setEditableAttribute(String editableAttribute) {
        this.editableAttribute = editableAttribute;
    }

    private String editableAttribute;

    private AuthorElement element;
    private String elementName;

    private AuthorAccess authorAccess;

    // TODO test arguments and throw errors

    protected void initConfig(Map<String, Object> arguments) {
        ns = (String) arguments.get("ns");
        editableAttribute = (String) arguments.get("edit");
    }

    public NameButton() {
    }

    @Override
    public void performAction(AuthorInplaceContext context,
                              AuthorAccess authorAccess) {

        this.authorAccess = authorAccess;
        this.element = context.getElem();
        this.elementName = element.getName();

        Map<String, Object> arguments = context.getArguments();

        initConfig(arguments);

        String title = (String) arguments.get("title");
        String url = (String) arguments.get("url");

        String selection = authorAccess.getEditorAccess().getSelectedText();

        String subUrl = null;

        openDialog(selection, title, url, subUrl, authorAccess.getWorkspaceAccess());

    }

    /**
     * Opens the search dialog.
     *
     * @param selection
     *            the selection in the current editor pane
     * @param title the title of the dialog
     * @param url the request url
     * @param subUrl the url for sub-item requests
     * @param workspace the workspace object
     */
    void openDialog(final String selection, String title, String url, String subUrl,
                    final AuthorWorkspaceAccess workspace) {
        SearchDialog searchDialog = new SearchDialog(workspace,
                title, null, null, url, subUrl, selection);
        if (searchDialog.load(selection)) {
            final String[] selectedSearchResult = searchDialog.showDialog();
            if (selectedSearchResult != null) {
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                            insertData(selectedSearchResult);
                    }
                });
            }
        }
    }

    public void insertData(String[] resultVals) {
        stopEditing(false);
        int attributeCount = element.getAttributesCount();
        String currentAttributeName;
        String[][] attributes = new String[attributeCount][2];
        for (int i = 0; i < attributeCount; i++) {
            currentAttributeName = element.getAttributeAtIndex(i);
            attributes[i][0] = currentAttributeName;
            attributes[i][1] = element.getAttribute(currentAttributeName)
                    .getValue();
        }

        authorAccess.getDocumentController().beginCompoundEdit();

        String attributeString = " " + editableAttribute + "=\"" + resultVals[0] + "\"";

        for (String[] attribute : attributes) {
            if (!attribute[0].equals(editableAttribute)) {
                attributeString += " " + attribute[0] + "=\"" + attribute[1]
                        + "\"";
            }
        }

        String result = "<"
                + elementName
                + attributeString
                + " xmlns=\""
                + ns
                + "\">"
                + resultVals[1].replace("&", "&amp;").replace("<", "&lt;")
                .replace("<", "&gt;") + "</" + elementName + ">";

        try {
            authorAccess.getDocumentController().insertXMLFragment(result,
                    element, AuthorConstants.POSITION_BEFORE);
        } catch (AuthorOperationException e1) {
            LOGGER.error(e1, e1);
        }
        authorAccess.getDocumentController().deleteNode(element);

        getNameBtn().setText(resultVals[1]);

        authorAccess.getDocumentController().endCompoundEdit();

    }

    @Override
    public void setBtnText(AuthorInplaceContext context) {

        initConfig(context.getArguments());

        AuthorElement element = context.getElem();
        String elementContent = null;

        try {
            elementContent = element.getTextContent();
        } catch (BadLocationException e) {
            LOGGER.error(e, e);
        }

        if (elementContent == null || elementContent.isEmpty()) {
            elementContent = "[Name]";
        }

        getNameBtn().setText(elementContent);

    }

}