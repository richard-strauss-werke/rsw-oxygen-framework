package com.aerhard.oxygen.framework.common;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.text.BadLocationException;

import ro.sync.ecss.extensions.api.AuthorAccess;
import ro.sync.ecss.extensions.api.AuthorDocumentController;
import ro.sync.ecss.extensions.api.AuthorOperationException;
import ro.sync.ecss.extensions.api.node.AttrValue;
import ro.sync.ecss.extensions.api.node.AuthorElement;
import ro.sync.ecss.extensions.api.node.AuthorNode;

/**
 * Utility class for commonly used document operations of the framework.
 * 
 * @author Alexander Erhard
 *
 */
public class DocUtil {

    /**
     * Gets the value of an attribute; if the attribute doesn't exist or its
     * value is null, returns the empty string;
     * 
     * @param element
     *            the element containing the attribute
     * @param attName
     *            the name of the attribute
     * @return the attribute value
     */
    public static String getAttributeOrEmptyString(AuthorElement element,
            String attName) {
        AttrValue attrNode = element.getAttribute(attName);
        if (attrNode == null) {
            return "";
        }
        String val = attrNode.getValue();
        if (val == null) {
            val = "";
        }
        return val;
    }

    /**
     * Sets an attribute value; if the value is null or the empty string, the
     * attribute value gets removed.
     * 
     * @param controller
     *            oXygen's authorDocumentController
     * @param element
     *            the element containing the attribute
     * @param attName
     *            the name of the attribute
     * @param value
     *            the attribute value to set
     */
    public static void modifyAttribute(AuthorDocumentController controller,
            AuthorElement element, String attName, String value) {

        AttrValue av = (value == null || value.isEmpty()) ? null
                : new AttrValue(value);

        controller.setAttribute(attName, av, element);
    }

    /**
     * Gets the name and the value of the attribute specified in a template string.
     * 
     * @param template
     *            the template
     * @return the attribute name or null if no name could be found
     */
    public static String[] readAttributeProperties(String template) {

        String[] result = new String[2];
        
        Pattern p = Pattern.compile("@(.*?)=\\\"(.*)\\\"");
        Matcher m = p.matcher(template);

        if (m.find()) {
            result[0] = m.group(1);
            result[1] = m.group(2);
            return result;
        }

        return null;
    }

    
    // TODO let the users determine the taret path withan xpath expression
    /**
     * Sets an attribute starting from the caret position
     * 
     * @param authorAccess
     *            oXygen's authorAccess
     * @param name
     *            the attribute name
     * @param value
     *            the attribute value
     * @throws AuthorOperationException
     */
    public static void setAttributeFromCaret(AuthorAccess authorAccess,
            String name, String value) throws AuthorOperationException {
        AuthorNode node;
        try {
            node = authorAccess.getDocumentController().getNodeAtOffset(
                    authorAccess.getEditorAccess().getCaretOffset());
        } catch (BadLocationException e) {
            throw new AuthorOperationException(
                    "Cannot identify the current node", e);
        }
        while (node != null && !(node instanceof AuthorElement)) {
            node = node.getParent();
        }

        DocUtil.modifyAttribute(authorAccess.getDocumentController(),
                (AuthorElement) node, name, value);
    }

}
