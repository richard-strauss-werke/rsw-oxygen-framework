package com.aerhard.oxygen.framework.tei;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

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
import ro.sync.ecss.extensions.api.node.AuthorNode;

@API(type=APIType.INTERNAL, src=SourceType.PUBLIC)
public class CreateMonogr implements AuthorOperation {
  
    /**
     * Logger for logging.
     */
    private static final Logger LOGGER = Logger.getLogger(CreateMonogr.class.getName());    

    
	private static final String ARG_ONE ="url";
	private static final String ARG_TWO ="pro";
	private static final String ARG_THREE ="benutzer";
	
	public CreateMonogr() {}
	
  /**
   * @see ro.sync.ecss.extensions.api.AuthorOperation#doOperation(ro.sync.ecss.extensions.api.AuthorAccess, ro.sync.ecss.extensions.api.ArgumentsMap)
   */
  @Override
  public void doOperation(AuthorAccess authorAccess, ArgumentsMap map)
      throws IllegalArgumentException, AuthorOperationException {
		String url = (String)map.getArgumentValue(ARG_ONE);
		String pro = (String)map.getArgumentValue(ARG_TWO);
		String benutzer = (String)map.getArgumentValue(ARG_THREE);
		
		String[] buttons = {"OK", "Abbrechen"};
		int[] buttonVals = {1, 0};
		int doIt = authorAccess.getWorkspaceAccess().showConfirmDialog(
				  "Neues Sammelwerk anlegen", "Soll ein neuer Sammelwerk-Datensatz geschaffen werden, der das aktuelle Dokument enthält und ein Querverweis auf dieses Sammelwerk eingefügt werden? (Vor dem Anlegen bitte sicherstellen, dass ein entsprechendes Sammelwerk nicht schon in der Datenbank ist.)", buttons, buttonVals);
		
		if (doIt == 1) {
			try {
				createDoc(authorAccess, url, pro, benutzer);
			} catch (IOException e) {
			    LOGGER.error(e, e);
			}
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
              new ArgumentDescriptor(
                      ARG_ONE,
                      ArgumentDescriptor.TYPE_STRING,
                      "URL"),
              new ArgumentDescriptor(
                      ARG_TWO,
                      ArgumentDescriptor.TYPE_STRING,
                      "pro"),
              new ArgumentDescriptor(
                      ARG_THREE,
                      ArgumentDescriptor.TYPE_STRING,
                      "benutzer")
      };
	}
  
  
  public void createDoc(AuthorAccess authorAccess, String urlParameter, String pro, String benutzer) throws AuthorOperationException, IOException {
	  
	  String result = "";
	  
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
			result = sb.toString();
		} catch (MalformedURLException e) {
		    LOGGER.error(e, e);
		} catch (IOException e) {
		    LOGGER.error(e, e);
		}
	  
	  String fullFn;
	  String resultContent;
	  
		if (result.contains("<answer>erfolg</answer>")) {
			
			int startPosition = result.indexOf("<p3>") + 4;  
			int endPosition = result.indexOf("</p3>");
			fullFn = result.substring(startPosition, endPosition);
			
			startPosition = result.lastIndexOf("/r") + 1;  
			endPosition = startPosition + 6;
			resultContent = result.substring(startPosition, endPosition);
			
			AuthorNode currentNode;
		    
		    AuthorNode node;
		    try {
		      node = authorAccess.getDocumentController().getNodeAtOffset(
		          authorAccess.getEditorAccess().getCaretOffset());
		    } catch (BadLocationException e) {
		      throw new AuthorOperationException("Cannot identify the current node", e);
		    }
		    currentNode = node;
			
			authorAccess.getDocumentController().insertXMLFragment(
		              "<bid>"+resultContent+"</bid>", currentNode, AuthorConstants.POSITION_BEFORE);
		    authorAccess.getDocumentController().deleteNode(currentNode);
			
			
			String run = pro + fullFn;
			Runtime rt = Runtime.getRuntime();
			rt.exec(run);
			
			String lockUrl = urlParameter.substring(0, urlParameter.lastIndexOf("/")) + "/fn_lock_set.xq?filename="+fullFn+"&username="+benutzer;

		  try {
			  URL url = new URL(lockUrl);

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
				result = sb.toString();

			} catch (MalformedURLException e) {
			    LOGGER.error(e, e);
			} catch (IOException e) {
			    LOGGER.error(e, e);
			}
		} else {
			authorAccess.getWorkspaceAccess().showErrorMessage("Konnte die Datei nicht anlegen.\n("+result+")");
		}
	
  }

}