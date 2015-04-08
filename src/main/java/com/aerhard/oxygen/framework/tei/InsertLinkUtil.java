package com.aerhard.oxygen.framework.tei;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import ro.sync.ecss.extensions.api.AuthorAccess;
import ro.sync.ecss.extensions.api.access.AuthorUtilAccess;

/**
 * Utility class for insert web link.
 * 
 * @author adriana_sbircea
 * @author costi
 *
 */
public class InsertLinkUtil {

  /**
   * Choose url.
   * 
   * @param authorAccess The author access.
   * @param title        The dialog title.
   * 
   * @return The chosen url.
   */
  public static String chooseURLForLink(AuthorAccess authorAccess, String title, String filename) {
    String chosenURL = null;
    
    File dir = new File(filename);
    
    File file = authorAccess.getWorkspaceAccess().chooseFile(dir, title, null, null, false);
    
    /*
    String sel = authorAccess.getWorkspaceAccess().chooseURLPath(title, null, null);
	*/
    if (file != null) {
    	String sel = file.getAbsolutePath();
      AuthorUtilAccess util = authorAccess.getUtilAccess();
      try {
        chosenURL = authorAccess.getXMLUtilAccess().escapeAttributeValue(
            util.makeRelative(
                authorAccess.getEditorAccess().getEditorLocation(), 
                //Also remove user credentials if this is the case.
                util.removeUserCredentials(new URL(sel))));
      } catch (MalformedURLException e1) {
        // If there is no protocol, let the path specified by the user
        chosenURL = sel;
      }
    }
    return chosenURL;
  }
}