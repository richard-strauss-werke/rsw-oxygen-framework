package com.aerhard.oxygen.framework.tei;

import com.aerhard.oxygen.plugin.dbtagger.util.HttpUtil;
import org.apache.log4j.Logger;
import org.json.JSONObject;
import ro.sync.annotations.api.API;
import ro.sync.annotations.api.APIType;
import ro.sync.annotations.api.SourceType;
import ro.sync.ecss.extensions.api.*;

import java.net.MalformedURLException;
import java.net.URL;

@API(type = APIType.INTERNAL, src = SourceType.PUBLIC)
public class CreateDocument implements AuthorOperation {

    /**
     * Logger for logging.
     */
    private static final Logger LOGGER = Logger.getLogger(CreateDocument.class.getName());


    private static final String ARG_URL = "url";
    private static final String ARG_TPL = "tpl";
    private static final String ARG_USER = "user";
    private static final String ARG_STATIC_PATH = "staticPath";
    private HttpUtil httpUtil = new HttpUtil();

    public CreateDocument() {
    }

    /**
     * @see AuthorOperation#doOperation(AuthorAccess, ArgumentsMap)
     */
    @Override
    public void doOperation(AuthorAccess authorAccess, ArgumentsMap map)
            throws IllegalArgumentException, AuthorOperationException {

        String url = (String) map.getArgumentValue(ARG_URL);
        String tpl = (String) map.getArgumentValue(ARG_TPL);
        String user = (String) map.getArgumentValue(ARG_USER);
        String staticPath = (String) map.getArgumentValue(ARG_STATIC_PATH);

        String newDocumentPath = createDocument(authorAccess, url, tpl, user);

        if (tpl == null || "".equals(tpl)) {

            // TODO if tpl is "" / null, clone the current document
        }


        if (newDocumentPath != null) {
            try {
                authorAccess.getWorkspaceAccess().open(new URL(staticPath + newDocumentPath));
            } catch (MalformedURLException e) {
                authorAccess.getWorkspaceAccess().showErrorMessage(e.toString());
            }
        }
    }

    /**
     * @see AuthorOperation#getArguments()
     */
    @Override
    public ArgumentDescriptor[] getArguments() {
        return new ArgumentDescriptor[]{
                new ArgumentDescriptor(
                        ARG_URL,
                        ArgumentDescriptor.TYPE_STRING,
                        "URL"),
                new ArgumentDescriptor(
                        ARG_TPL,
                        ArgumentDescriptor.TYPE_STRING,
                        "Template"),
                new ArgumentDescriptor(
                        ARG_USER,
                        ArgumentDescriptor.TYPE_STRING,
                        "Benutzer"),
                new ArgumentDescriptor(
                        ARG_STATIC_PATH,
                        ArgumentDescriptor.TYPE_STRING,
                        "Statischer Teil der URL des neuen Dokuments")
        };
    }


    public String createDocument(AuthorAccess authorAccess, String url, String tpl, String user) {

        String response = httpUtil.get(null, null, url, tpl, false);
        String errorMessage;

        if (response != null) {
            try {
                JSONObject responseJSON = new JSONObject(response);
                if (responseJSON.has("success") && responseJSON.has("msg")) {
                    if (responseJSON.getBoolean("success")) {
                        return responseJSON.getString("msg");
                    }
                    errorMessage = responseJSON.getString("msg");
                } else {
                    errorMessage = "Invalid JSON object.";
                }
            } catch (Exception e) {
                errorMessage = e.toString();
            }

            authorAccess.getWorkspaceAccess().showErrorMessage("Konnte die Datei nicht anlegen."
                    + System.getProperty("line.separator") + errorMessage);
        }

        return null;
    }

    /**
     * @see Extension#getDescription()
     */
    @Override
    public String getDescription() {
        return "Create a Document in the database and open it in oXygen";
    }


}