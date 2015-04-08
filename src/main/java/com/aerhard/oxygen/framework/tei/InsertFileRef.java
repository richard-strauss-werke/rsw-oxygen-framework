package com.aerhard.oxygen.framework.tei;

import com.aerhard.oxygen.framework.common.DocUtil;

import ro.sync.annotations.api.API;
import ro.sync.annotations.api.APIType;
import ro.sync.annotations.api.SourceType;
import ro.sync.ecss.extensions.api.ArgumentDescriptor;
import ro.sync.ecss.extensions.api.ArgumentsMap;
import ro.sync.ecss.extensions.api.AuthorAccess;
import ro.sync.ecss.extensions.api.AuthorOperation;
import ro.sync.ecss.extensions.api.AuthorOperationException;

/**
 * Opens a file browser and inserts a relative path to the selected file into
 * the XML document.
 * 
 * @author Alexander Erhard
 *
 */
@API(type = APIType.INTERNAL, src = SourceType.PUBLIC)
public class InsertFileRef implements AuthorOperation {

    /**
     * the start path of the file dialog. Note that this path gets removed from
     * the user selected path in order to obtain a relative path.
     */
    private static final String ARG_PATH = "path";
    /**
     * the insertion template.
     */
    private static final String ARG_TEMPLATE = "template";

    /**
     * @see ro.sync.ecss.extensions.api.AuthorOperation#doOperation(ro.sync.ecss.extensions.api.AuthorAccess,
     *      ro.sync.ecss.extensions.api.ArgumentsMap)
     */
    @Override
    public void doOperation(AuthorAccess authorAccess, ArgumentsMap map)
            throws IllegalArgumentException, AuthorOperationException {

        String path = (String) map.getArgumentValue(ARG_PATH);
        String template = (String) map.getArgumentValue(ARG_TEMPLATE);
        insertReference(authorAccess, path, template, true);
    }

    /**
     * @see ro.sync.ecss.extensions.api.Extension#getDescription()
     */
    @Override
    public String getDescription() {
        return "Opens a file browser and inserts a relative path to the selected file into the XML document.";
    }

    /**
     * @see ro.sync.ecss.extensions.api.AuthorOperation#getArguments()
     */
    @Override
    public ArgumentDescriptor[] getArguments() {
        return new ArgumentDescriptor[] {
                new ArgumentDescriptor(ARG_PATH,
                        ArgumentDescriptor.TYPE_STRING, "Pfad"),

                new ArgumentDescriptor(ARG_TEMPLATE,
                        ArgumentDescriptor.TYPE_STRING, "Einfüge-Vorlage") };
    }

    /**
     * creates the document fragment to insert into the XML document
     * 
     * @param path
     *            the selected path
     * @param template
     *            the insertion template
     * @return the document fragment
     */
    public String createElementFragment(String path, String template) {
        return template.replaceAll("\\$\\{url\\}", path);
    }

    /**
     * removes the initial search path from the selected file path to get a
     * relative path
     * 
     * @param selectedPath
     *            the file path selected by the user
     * @param basePath
     *            the initial search path
     * @return the relative file path
     */
    private String cropPath(String selectedPath, String basePath) {
        return (selectedPath.contains(basePath)) ? selectedPath
                .substring(basePath.length() + 1).replace("\\", "/") : "";
    }

    public void insertReference(AuthorAccess authorAccess, String basePath,
            String template, boolean schemaAware)
            throws AuthorOperationException {

        String selectedPath = InsertLinkUtil.chooseURLForLink(authorAccess,
                "Datei wählen", basePath);

        if (selectedPath != null) {
            String path = cropPath(selectedPath, basePath);

            if (template.startsWith("@")) {
                
                String[] attributeProperties = DocUtil.readAttributeProperties(template); 

                String attributeName = attributeProperties[0];
                if (attributeName != null) {
                    DocUtil.setAttributeFromCaret(authorAccess, attributeName, path);
                }

            } else {
                String fragment = createElementFragment(path, template);
                if (!"".equals(fragment)) {
                    authorAccess.getDocumentController()
                            .insertXMLFragmentSchemaAware(
                                    fragment,
                                    authorAccess.getEditorAccess()
                                            .getCaretOffset());
                }
            }

        }
    }

}