package com.aerhard.oxygen.framework.tei;

import java.util.Arrays;

import javax.swing.text.BadLocationException;

import org.apache.log4j.Logger;

import ro.sync.ecss.extensions.api.ArgumentDescriptor;
import ro.sync.ecss.extensions.api.ArgumentsMap;
import ro.sync.ecss.extensions.api.AuthorAccess;
import ro.sync.ecss.extensions.api.AuthorOperation;
import ro.sync.ecss.extensions.api.AuthorOperationException;
import ro.sync.ecss.extensions.api.node.AuthorDocumentFragment;
import ro.sync.ecss.extensions.api.node.AuthorNode;

/**
 * @author Radu Coravu (http://www.oxygenxml.com/forum/topic6939.html)
 * @author Alexander Erhard (adjustments)
 */
public class SplitParagraph implements AuthorOperation {

    /**
     * Logger for logging.
     */
    private static final Logger LOGGER = Logger.getLogger(SplitParagraph.class.getName());    

	@Override
	public String getDescription() {
		// TODO Auto-generated method stub
		return null;
	}

	private String[] splittable = {"p", "div", "l", "seg", "item", "addrLine"};
	
	/**
	   * @see ro.sync.ecss.extensions.api.AuthorOperation#doOperation(ro.sync.ecss.extensions.api.AuthorAccess, ro.sync.ecss.extensions.api.ArgumentsMap)
	   */
	  @Override
	  public void doOperation(AuthorAccess authorAccess, ArgumentsMap args)
	  throws IllegalArgumentException, AuthorOperationException {
	    //Split the current paragraph
	    int caretOffset = authorAccess.getEditorAccess().getCaretOffset();
	    String nodeName = null; 
	    AuthorNode paragraphAtCaret = null;
	    try {
	      //Maybe we are in an XML tag which is a child or descendant of a <p>
	    	AuthorNode currentNode = authorAccess.getDocumentController().getNodeAtOffset(caretOffset);
	    	nodeName = currentNode.getName();
	        if(Arrays.asList(splittable).contains(nodeName)) {
	          //Found it
	          paragraphAtCaret = currentNode;
	      }
	    } catch (BadLocationException e) {
	        LOGGER.error(e, e);
	    }
	    if(paragraphAtCaret != null) {
	      //The caret is inside a paragraph.
	      try {
	        AuthorDocumentFragment rightSplit = null;
	        if(caretOffset < paragraphAtCaret.getEndOffset()) {
	          rightSplit = authorAccess.getDocumentController().createDocumentFragment(caretOffset,
	              //The end offset is inclusive so the created fragment will actually be a <p> holding inside it the content
	              paragraphAtCaret.getEndOffset());
	        } 
	        
	        String xmlToInsert;
	        if(rightSplit == null) {
	        	xmlToInsert = "<"+nodeName+" xmlns=\"http://www.tei-c.org/ns/1.0\"/>";
	        }
	        	
	        else {
	          //The right splitted <p>
	          xmlToInsert = authorAccess.getDocumentController().serializeFragmentToXML(rightSplit);
	        }
	        
	        //Insert the new content
	        authorAccess.getDocumentController().insertXMLFragment(xmlToInsert, paragraphAtCaret.getEndOffset() + 1);
	        if(rightSplit != null) {
	          //Delete the old content
	          authorAccess.getDocumentController().delete(caretOffset, paragraphAtCaret.getEndOffset() - 1);
	        }
	        //Place the caret in the empty paragraph
	        authorAccess.getEditorAccess().setCaretPosition(paragraphAtCaret.getEndOffset() + 2);
	      } catch (BadLocationException e) {
	          LOGGER.error(e, e);
	      }
	    }
	  }

	@Override
	public ArgumentDescriptor[] getArguments() {
		// TODO Auto-generated method stub
		return null;
	}


}
