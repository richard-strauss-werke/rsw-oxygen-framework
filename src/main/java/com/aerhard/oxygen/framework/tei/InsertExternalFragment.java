/*
 *  The Syncro Soft SRL License
 *
 *  Copyright (c) 1998-2009 Syncro Soft SRL, Romania.  All rights
 *  reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions
 *  are met:
 *
 *  1. Redistribution of source or in binary form is allowed only with
 *  the prior written permission of Syncro Soft SRL.
 *
 *  2. Redistributions of source code must retain the above copyright
 *  notice, this list of conditions and the following disclaimer.
 *
 *  3. Redistributions in binary form must reproduce the above copyright
 *  notice, this list of conditions and the following disclaimer in
 *  the documentation and/or other materials provided with the
 *  distribution.
 *
 *  4. The end-user documentation included with the redistribution,
 *  if any, must include the following acknowledgment:
 *  "This product includes software developed by the
 *  Syncro Soft SRL (http://www.sync.ro/)."
 *  Alternately, this acknowledgment may appear in the software itself,
 *  if and wherever such third-party acknowledgments normally appear.
 *
 *  5. The names "Oxygen" and "Syncro Soft SRL" must
 *  not be used to endorse or promote products derived from this
 *  software without prior written permission. For written
 *  permission, please contact support@oxygenxml.com.
 *
 *  6. Products derived from this software may not be called "Oxygen",
 *  nor may "Oxygen" appear in their name, without prior written
 *  permission of the Syncro Soft SRL.
 *
 *  THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 *  WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 *  OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  DISCLAIMED.  IN NO EVENT SHALL THE SYNCRO SOFT SRL OR
 *  ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 *  SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 *  LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 *  USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 *  ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 *  OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 *  OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 *  SUCH DAMAGE.
 */

/*
 * Note: Above copyright notice applies to major parts of the performRequest method
 * A. Erhard
 */

package com.aerhard.oxygen.framework.tei;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.text.BadLocationException;

import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;

import ro.sync.annotations.api.API;
import ro.sync.annotations.api.APIType;
import ro.sync.annotations.api.SourceType;
import ro.sync.ecss.extensions.api.ArgumentDescriptor;
import ro.sync.ecss.extensions.api.ArgumentsMap;
import ro.sync.ecss.extensions.api.AuthorAccess;
import ro.sync.ecss.extensions.api.AuthorConstants;
import ro.sync.ecss.extensions.api.AuthorOperation;
import ro.sync.ecss.extensions.api.AuthorOperationException;
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
public class InsertExternalFragment implements AuthorOperation {

    /**
     * Logger for logging.
     */
    private static final Logger LOGGER = Logger
            .getLogger(InsertExternalFragment.class.getName());

    private static final String ARG_TARGET_LOCATION = "targetLocation";
    private static final String ARG_URL = "url";
    private static final String ARG_USER = "user";
    private static final String ARG_ACTION = "action";

    public InsertExternalFragment() {
    }

    /**
     * @see ro.sync.ecss.extensions.api.AuthorOperation#doOperation(ro.sync.ecss.extensions.api.AuthorAccess,
     *      ro.sync.ecss.extensions.api.ArgumentsMap)
     */
    @Override
    public void doOperation(AuthorAccess authorAccess, ArgumentsMap map)
            throws IllegalArgumentException, AuthorOperationException {
        String xPathTarget = (String) map.getArgumentValue(ARG_TARGET_LOCATION);
        String url = (String) map.getArgumentValue(ARG_URL);
        String user = (String) map.getArgumentValue(ARG_USER);
        String action = (String) map.getArgumentValue(ARG_ACTION);

        try {
            performRequest(authorAccess, xPathTarget, url, user, action);
        } catch (IOException e) {
            LOGGER.error(e, e);
        }

    }

    /**
     * @see ro.sync.ecss.extensions.api.Extension#getDescription()
     */
    @Override
    public String getDescription() {
        return "Stelle in Datenbank suchen";
    }

    /**
     * @see ro.sync.ecss.extensions.api.AuthorOperation#getArguments()
     */
    @Override
    public ArgumentDescriptor[] getArguments() {
        return new ArgumentDescriptor[] {
                new ArgumentDescriptor(ARG_TARGET_LOCATION,
                        ArgumentDescriptor.TYPE_STRING, "Ziel-XPath"),
                new ArgumentDescriptor(ARG_URL, ArgumentDescriptor.TYPE_STRING,
                        "URL"),
                new ArgumentDescriptor(ARG_USER,
                        ArgumentDescriptor.TYPE_STRING, "Benutzer"),
                new ArgumentDescriptor(ARG_ACTION,
                        ArgumentDescriptor.TYPE_CONSTANT_LIST, "Aktion",
                        new String[] { "surround", "atcaret", "asfirstchild",
                                "replace" }, "surround") };
    }

    public void performRequest(AuthorAccess authorAccess, String xpathTarget,
            String urlParameter, String user, String action)
            throws AuthorOperationException, IOException {

        String response = request(urlParameter + user);

        if (response != null) {

            // Get the current node and the current element
            // (they may be different if the current node is not an element)
            AuthorNode currentNode;
            AuthorElement currentElement;
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
            if (node != null) {
                currentElement = (AuthorElement) node;
            } else {
                currentElement = authorAccess.getDocumentController()
                        .getAuthorDocumentNode().getRootElement();
            }

            // Get the source, this will be the input for the script
            AuthorElement sourceElement = currentElement;

            String currentElementLocation = "";

            AuthorNode tmp = currentElement;
            if (tmp.isDescendentOf(sourceElement)) {
                while (tmp != sourceElement) {
                    AuthorElement parent = ((AuthorElement) tmp.getParent());
                    int index = parent.getContentNodes().indexOf(tmp) + 1;
                    currentElementLocation = "/*[" + index + "]"
                            + currentElementLocation;
                    tmp = parent;
                }
            }

            // The target element is where the result is put, depending on the
            // action it can replace this element
            // or it can be inserted relative to this element.
            AuthorNode targetNode;
            if (xpathTarget instanceof String && !"".equals(xpathTarget)) {
                AuthorNode[] results = authorAccess.getDocumentController()
                        .findNodesByXPath(xpathTarget, true, true,
                                false);
                if (results.length > 0) {
                    targetNode = results[0];
                } else {
                    throw new AuthorOperationException(
                            "The target XPath location does not identify a node: "
                                    + xpathTarget);
                }
            } else {
                // if the target is not specified we take that as the current
                // element.
                targetNode = currentNode;
            }

            if ("surround".equals(action) || action == null) {
                surroundSelection(authorAccess, response);
            } else if ("replace".equals(action)) {
                authorAccess.getDocumentController().insertXMLFragment(
                        response, targetNode, AuthorConstants.POSITION_BEFORE);
                authorAccess.getDocumentController().deleteNode(targetNode);
            } else if ("atcaret".equals(action)) {
                authorAccess.getDocumentController().insertXMLFragment(
                        response,
                        authorAccess.getEditorAccess().getCaretOffset());
            } else {
                authorAccess.getDocumentController().insertXMLFragment(
                        response, targetNode,
                        AuthorConstants.POSITION_INSIDE_FIRST);
            }
        } else {
            authorAccess.getWorkspaceAccess().showErrorMessage(
                    "Fehler bei der Server-Anfrage \"" + urlParameter + user
                            + "\"");
        }

    }

    private void surroundSelection(AuthorAccess authorAccess, String response)
            throws AuthorOperationException {
        String[] splitResponse = splitContentFromElement(response);

        if (splitResponse != null) {
            String[] buttons = { "OK", "Abbrechen" };
            int[] buttonVals = { 1, 0 };

            int userChoice = authorAccess.getWorkspaceAccess()
                    .showConfirmDialog("Querverweis einfügen",
                            "Verweis auf " + splitResponse[1] + " einfügen?",
                            buttons, buttonVals);

            if (userChoice == 1) {
                CommonsOperationsUtil.surroundWithFragment(authorAccess, false,
                        splitResponse[0]);
            }
        }
    }

    String request(String urlParameter) {
        String response = null;
        try {
            URL url = new URL(urlParameter);

            URLConnection urlConnection = url.openConnection();

            if (url.getUserInfo() != null) {
                String basicAuth = "Basic " + new String(new Base64().encode(url.getUserInfo().getBytes()));
                urlConnection.setRequestProperty("Authorization", basicAuth);
            }

            InputStream is = urlConnection.getInputStream();
            InputStreamReader isr = new InputStreamReader(is, "UTF-8");

            int numCharsRead;
            char[] charArray = new char[1024];
            StringBuilder sb = new StringBuilder();
            while ((numCharsRead = isr.read(charArray)) > 0) {
                sb.append(charArray, 0, numCharsRead);
            }
            response = sb.toString();
        } catch (MalformedURLException e) {
            LOGGER.error(e, e);
        } catch (IOException e) {
            LOGGER.error(e, e);
        }
        return response;
    }

    String[] splitContentFromElement(String string) {

        String[] result = null;

        // expect a start and end tag
        Pattern p = Pattern.compile("(^<.*?>)(.*)(<.*?>$)");
        Matcher m = p.matcher(string);

        if (m.find()) {
            result = new String[2];
            result[0] = m.group(1) + m.group(3);
            result[1] = m.group(2);
        }

        return result;
    }

}