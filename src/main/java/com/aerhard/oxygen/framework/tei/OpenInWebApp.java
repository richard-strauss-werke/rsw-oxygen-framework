package com.aerhard.oxygen.framework.tei;

import java.net.MalformedURLException;
import java.net.URL;

import org.apache.log4j.Logger;

import ro.sync.annotations.api.API;
import ro.sync.annotations.api.APIType;
import ro.sync.annotations.api.SourceType;
import ro.sync.ecss.extensions.api.ArgumentDescriptor;
import ro.sync.ecss.extensions.api.ArgumentsMap;
import ro.sync.ecss.extensions.api.AuthorAccess;
import ro.sync.ecss.extensions.api.AuthorOperation;
import ro.sync.ecss.extensions.api.AuthorOperationException;

@API(type = APIType.INTERNAL, src = SourceType.PUBLIC)
public class OpenInWebApp implements AuthorOperation {

    /**
     * Logger for logging.
     */
    private static final Logger LOGGER = Logger.getLogger(OpenInWebApp.class.getName());    

    
    private static final String ARG_ONE = "url";

    /**
     * @see ro.sync.ecss.extensions.api.AuthorOperation#doOperation(ro.sync.ecss.extensions.api.AuthorAccess,
     *      ro.sync.ecss.extensions.api.ArgumentsMap)
     */
    @Override
    public void doOperation(AuthorAccess authorAccess, ArgumentsMap map)
            throws IllegalArgumentException, AuthorOperationException {
        String url = (String) map.getArgumentValue(ARG_ONE);

        openExternalLink(authorAccess, url);
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
        return new ArgumentDescriptor[] { new ArgumentDescriptor(
                ARG_ONE, ArgumentDescriptor.TYPE_STRING, "URL") };
    }

    public void openExternalLink(AuthorAccess authorAccess, String urlParameter)
            throws AuthorOperationException {

        String selected = authorAccess.getEditorAccess().getSelectedText();
        String url = urlParameter + "&q=" + selected;

        URL fullUrl;

        try {
            fullUrl = new URL(url);
            authorAccess.getWorkspaceAccess().openInExternalApplication(
                    fullUrl, true);
        } catch (MalformedURLException e) {
            LOGGER.error(e, e);
        }

    }
}