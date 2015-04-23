package com.aerhard.oxygen.framework.tei;

import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;
import javax.swing.text.Position;

import org.apache.log4j.Logger;

import com.aerhard.oxygen.framework.common.DocUtil;
import com.aerhard.oxygen.plugin.dbtagger.SearchDialog;

import ro.sync.annotations.api.API;
import ro.sync.annotations.api.APIType;
import ro.sync.annotations.api.SourceType;
import ro.sync.ecss.extensions.api.AuthorAccess;
import ro.sync.ecss.extensions.api.ArgumentDescriptor;
import ro.sync.ecss.extensions.api.ArgumentsMap;
import ro.sync.ecss.extensions.api.AuthorConstants;
import ro.sync.ecss.extensions.api.AuthorOperation;
import ro.sync.ecss.extensions.api.AuthorOperationException;
import ro.sync.ecss.extensions.api.access.AuthorWorkspaceAccess;
import ro.sync.ecss.extensions.api.node.AuthorElement;
import ro.sync.ecss.extensions.api.node.AuthorNode;
import ro.sync.ecss.extensions.commons.operations.CommonsOperationsUtil;

/**
 * uses parts of ro.sync.ecss.extensions.commons.operations.TransformOperation
 * 
 * @author Alexander Erhard
 *
 */
@API(type = APIType.INTERNAL, src = SourceType.PUBLIC)
public class InsertKeyRef implements AuthorOperation {

    /**
     * Logger for logging.
     */
    private static final Logger LOGGER = Logger.getLogger(InsertKeyRef.class
            .getName());

    public final static String ACTION_SURROUND = "Surround";
    public final static String ACTION_INSERT_ATTRIBUTE = "Insert attribute";

    public final static String ACTION_REPLACE = "Replace";
    public final static String ACTION_AT_CARET = "At caret position";
    public final static String ACTION_INSERT_BEFORE = AuthorConstants.POSITION_BEFORE;
    public final static String ACTION_INSERT_AFTER = AuthorConstants.POSITION_AFTER;
    public final static String ACTION_INSERT_AS_FIRST_CHILD = AuthorConstants.POSITION_INSIDE_FIRST;
    public final static String ACTION_INSERT_AS_LAST_CHILD = AuthorConstants.POSITION_INSIDE_LAST;
    public final static String CARET_POSITION_PRESERVE = "Preserve";
    public final static String CARET_POSITION_BEFORE = "Before";
    public final static String CARET_POSITION_START = "Start";
    public final static String CARET_POSITION_EDITABLE = "First editable position";
    public final static String CARET_POSITION_END = "End";
    public final static String CARET_POSITION_AFTER = "After";

    private static final String ARGUMENT_XPATH_TARGET = "targetLocation";
    private static final String ARGUMENT_URL = "url";
    private static final String ARGUMENT_ACTION = "action";
    private static final String ARGUMENT_CARET_POSITION = "caretPosition";

    private static final String ARGUMENT_TITLE = "title";
    private static final String ARGUMENT_TEMPLATE = "template";

    private ArgumentDescriptor[] arguments = null;

    private AuthorAccess authorAccess;
    private String template;
    private String action;
    // The target element is where the result is put, depending on the
    // action it can replace this element
    // or it can be inserted relative to this element.
    private AuthorNode targetNode;
    private String xPathTarget;
    private String caretPosition;


    public InsertKeyRef() {

        arguments = new ArgumentDescriptor[6];

        ArgumentDescriptor argumentDescriptor;

        argumentDescriptor = new ArgumentDescriptor(
                ARGUMENT_XPATH_TARGET,
                ArgumentDescriptor.TYPE_XPATH_EXPRESSION,
                "An XPath expression indicating the insert location for the result of the transformation.\n"
                        + "Note: If it is not defined then the insert location will be at the caret.");
        arguments[0] = argumentDescriptor;

        argumentDescriptor = new ArgumentDescriptor(ARGUMENT_URL,
                ArgumentDescriptor.TYPE_STRING, "The URL.");
        arguments[1] = argumentDescriptor;

        argumentDescriptor = new ArgumentDescriptor(
                ARGUMENT_ACTION,
                ArgumentDescriptor.TYPE_CONSTANT_LIST,
                "The insert action relative to the node determined by the target XPath expression.\n"
                        + "It can be: "
                        + ACTION_REPLACE
                        + ", "
                        + ACTION_SURROUND
                        + ", "
                        + ACTION_INSERT_ATTRIBUTE
                        + ", "
                        + ACTION_AT_CARET
                        + ", "
                        + ACTION_INSERT_BEFORE
                        + ", "
                        + ACTION_INSERT_AFTER
                        + ", "
                        + ACTION_INSERT_AS_FIRST_CHILD
                        + " or "
                        + ACTION_INSERT_AS_LAST_CHILD + ".\n", new String[] {
                        ACTION_REPLACE, ACTION_SURROUND,
                        ACTION_INSERT_ATTRIBUTE, ACTION_AT_CARET,
                        ACTION_INSERT_BEFORE, ACTION_INSERT_AFTER,
                        ACTION_INSERT_AS_FIRST_CHILD,
                        ACTION_INSERT_AS_LAST_CHILD }, ACTION_REPLACE);
        arguments[2] = argumentDescriptor;

        argumentDescriptor = new ArgumentDescriptor(ARGUMENT_CARET_POSITION,
                ArgumentDescriptor.TYPE_CONSTANT_LIST,
                "The position of the caret after the action is executed.\n"
                        + "It can be: " + CARET_POSITION_PRESERVE + ", "
                        + CARET_POSITION_BEFORE + ", " + CARET_POSITION_START
                        + ", " + CARET_POSITION_EDITABLE + ", "
                        + CARET_POSITION_END + " or " + CARET_POSITION_AFTER
                        + ".\n", new String[] { CARET_POSITION_PRESERVE,
                        CARET_POSITION_BEFORE, CARET_POSITION_START,
                        CARET_POSITION_EDITABLE, CARET_POSITION_END,
                        CARET_POSITION_AFTER }, CARET_POSITION_EDITABLE);
        arguments[3] = argumentDescriptor;

        argumentDescriptor = new ArgumentDescriptor(ARGUMENT_TITLE,
                ArgumentDescriptor.TYPE_STRING, "Titel");
        arguments[4] = argumentDescriptor;

        argumentDescriptor = new ArgumentDescriptor(ARGUMENT_TEMPLATE,
                ArgumentDescriptor.TYPE_STRING, "Template");
        arguments[5] = argumentDescriptor;

    }

    @Override
    public void doOperation(AuthorAccess authorAccess, ArgumentsMap args)
            throws AuthorOperationException {

        caretPosition = "";
        Object xcaretPosition = args.getArgumentValue(ARGUMENT_CARET_POSITION);
        if (xcaretPosition instanceof String) {
            caretPosition = (String) xcaretPosition;
        }

        xPathTarget = "";
        Object xxPathTarget = args.getArgumentValue(ARGUMENT_XPATH_TARGET);
        if (xxPathTarget instanceof String) {
            xPathTarget = (String) xxPathTarget;
        }

        String url = "";
        Object xurl = args.getArgumentValue(ARGUMENT_URL);
        if (xurl instanceof String) {
            url = (String) xurl;
        }

        action = "";
        Object xaction = args.getArgumentValue(ARGUMENT_ACTION);
        if (xaction instanceof String) {
            action = (String) xaction;
        }

        String title = "";
        Object xtitle = args.getArgumentValue(ARGUMENT_TITLE);
        if (xtitle instanceof String) {
            title = (String) xtitle;
        }

        template = "";
        Object xtemplate = args.getArgumentValue(ARGUMENT_TEMPLATE);
        if (xtitle instanceof String) {
            template = (String) xtemplate;
        }

        insertByAuthor(authorAccess, title, url);

    }

    public void insertByAuthor(AuthorAccess authorAccess, String title,
            String url) throws AuthorOperationException {

        this.authorAccess = authorAccess;

        // Get the current node and the current element
        // (they may be different if the current node is not an element)
        AuthorNode currentNode;
        AuthorNode node;
        try {
            node = authorAccess.getDocumentController().getNodeAtOffset(
                    authorAccess.getEditorAccess().getCaretOffset());
        } catch (BadLocationException e) {
            throw new AuthorOperationException(
                    "Cannot identify the current node", e);
        }
        currentNode = node;
        while (node != null && !(node instanceof AuthorElement)) {
            node = node.getParent();
        }

        // The target element is where the result is put, depending on the
        // action it can replace this element
        // or it can be inserted relative to this element.
        if (xPathTarget != null && !"".equals(xPathTarget)) {
            AuthorNode[] results = authorAccess.getDocumentController()
                    .findNodesByXPath(xPathTarget, true, true, false);
            if (results.length > 0) {
                targetNode = results[0];
            } else {
                throw new AuthorOperationException(
                        "The target XPath location does not identify a node: "
                                + xPathTarget);
            }
        } else {
            // if the target is not specified we take that as the current
            // element.
            targetNode = currentNode;
        }

        

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
                        try {
                            insertData(selectedSearchResult);
                        } catch (AuthorOperationException e) {
                            LOGGER.error(e, e);
                        }
                    }
                });
            }
        }
    }

    public void insertData(String[] resultVals) throws AuthorOperationException {
        String text = resultVals[1].replace("&", "&amp;").replace("<", "&lt;")
                .replace("<", "&gt;");

        // Replace template variable "${key}" with result key
        String result = template.replace("${key}", resultVals[0]);
        // Remove "{$selection}" from result
        result = result.replace("${selection}", "");
        result = result.replace("${text}", text);

        // Store initial caret information
        int offset = authorAccess.getEditorAccess().getCaretOffset();
        int insertionOffset = authorAccess.getEditorAccess().getCaretOffset();
        if (ACTION_REPLACE.equals(action)) {
            insertionOffset = targetNode.getStartOffset();
        } else if (!ACTION_AT_CARET.equals(action)
                && !ACTION_SURROUND.equals(action)
                && !ACTION_INSERT_ATTRIBUTE.equals(action)) {
            String xpath = xPathTarget != null
                    && (xPathTarget).trim().length() > 0 ? xPathTarget
                    : ".";
            // Evaluate the expression and obtain the offset of the first
            // node from the result
            insertionOffset = authorAccess.getDocumentController()
                    .getXPathLocationOffset(xpath, action);
        }

        // We create a position to keep track of the end offset of the
        // inserted fragment.
        Position endOffsetPos = null;
        try {
            endOffsetPos = authorAccess.getDocumentController()
                    .createPositionInContent(insertionOffset);
        } catch (BadLocationException e1) {
            LOGGER.error(e1, e1);
        }

        // insert result
        authorAccess.getDocumentController().beginCompoundEdit();
        if (ACTION_SURROUND.equals(action)) {
            CommonsOperationsUtil.surroundWithFragment(authorAccess, false,
                    result);
        } else if (ACTION_INSERT_ATTRIBUTE.equals(action)) {
            String[] attributeProperties = DocUtil
                    .readAttributeProperties(result);
            DocUtil.modifyAttribute(authorAccess.getDocumentController(),
                    (AuthorElement) targetNode, attributeProperties[0],
                    attributeProperties[1]);
        } else if (ACTION_REPLACE.equals(action)) {
            authorAccess.getDocumentController().insertXMLFragment(result,
                    targetNode, ACTION_INSERT_BEFORE);
            authorAccess.getDocumentController().deleteNode(targetNode);
        } else if (ACTION_AT_CARET.equals(action)) {
            authorAccess.getDocumentController().insertXMLFragment(result,
                    authorAccess.getEditorAccess().getCaretOffset());
        } else {
            authorAccess.getDocumentController().insertXMLFragment(result,
                    targetNode, action);
        }

        authorAccess.getDocumentController().endCompoundEdit();

        // Get additional caret information.
        int startOffset = insertionOffset + 1;
        int endOffset = endOffsetPos != null ? endOffsetPos.getOffset() - 1
                : startOffset;

        if (offset < startOffset) {
            offset = startOffset;
        }
        if (offset > endOffset) {
            offset = endOffset;
        }

        if (CARET_POSITION_BEFORE.equals(caretPosition)) {
            authorAccess.getEditorAccess().setCaretPosition(startOffset - 1);
        } else if (CARET_POSITION_START.equals(caretPosition)) {
            authorAccess.getEditorAccess().setCaretPosition(startOffset);
        } else if (CARET_POSITION_PRESERVE.equals(caretPosition)) {
            authorAccess.getEditorAccess().setCaretPosition(offset);
        } else if (CARET_POSITION_END.equals(caretPosition)) {
            authorAccess.getEditorAccess().setCaretPosition(endOffset);
        } else if (CARET_POSITION_AFTER.equals(caretPosition)) {
            authorAccess.getEditorAccess().setCaretPosition(endOffset + 1);
        } else { // default: CARET_POSITION_EDITABLE
            try {
                authorAccess.getEditorAccess().goToNextEditablePosition(
                        startOffset - 1, endOffset);
            } catch (BadLocationException e) {
                LOGGER.error(e, e);
            }
        }

    }

    /**
     * @see ro.sync.ecss.extensions.api.AuthorOperation#getArguments()
     */
    @Override
    public ArgumentDescriptor[] getArguments() {
        return arguments;
    }

    /**
     * @see ro.sync.ecss.extensions.api.AuthorOperation#getDescription()
     */
    @Override
    public String getDescription() {
        return "Verweisziele via http aus der Datenbank holen und in das Dokument schreiben.";
    }
}