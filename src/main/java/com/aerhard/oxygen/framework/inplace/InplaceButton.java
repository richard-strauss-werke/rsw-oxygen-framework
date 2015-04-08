package com.aerhard.oxygen.framework.inplace;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.text.BadLocationException;

import org.apache.log4j.Logger;

import ro.sync.annotations.api.API;
import ro.sync.annotations.api.APIType;
import ro.sync.annotations.api.SourceType;
import ro.sync.ecss.extensions.api.AuthorAccess;
import ro.sync.ecss.extensions.api.CursorType;
import ro.sync.ecss.extensions.api.access.AuthorUtilAccess;
import ro.sync.ecss.extensions.api.editor.AbstractInplaceEditor;
import ro.sync.ecss.extensions.api.editor.AuthorInplaceContext;
import ro.sync.ecss.extensions.api.editor.EditingEvent;
import ro.sync.ecss.extensions.api.editor.InplaceEditorArgumentKeys;
import ro.sync.ecss.extensions.api.editor.InplaceRenderer;
import ro.sync.ecss.extensions.api.editor.RendererLayoutInfo;
import ro.sync.ecss.extensions.api.node.AuthorElement;
import ro.sync.exml.view.graphics.Dimension;
import ro.sync.exml.view.graphics.Font;
import ro.sync.exml.view.graphics.Point;
import ro.sync.exml.view.graphics.Rectangle;


/**
 * Superclass of {@link EditButton}; based on the oXygen SDK
 * 
 * @author Costi 
 * @author Adriana
 * @author Alexander Erhard (adjustments)
 */
@API(type = APIType.EXTENDABLE, src = SourceType.PUBLIC)
public class InplaceButton extends AbstractInplaceEditor implements InplaceRenderer {

    /**
     * Logger for logging.
     */
    private static final Logger LOGGER = Logger.getLogger(InplaceButton.class.getName());    

    
	/**
	 * <code>true</code> if the platform is Eclipse.
	 */
	private static final Boolean IS_ECLIPSE = Boolean.valueOf(System
			.getProperty("com.oxygenxml.is.eclipse.plugin"));

	/**
	 * <code>true</code> if the platform is Windows.
	 */
	private static final boolean IS_WIN32 = System.getProperty("os.name")
			.toUpperCase().startsWith("WIN");

	/**
	 * The vertical gap of the panel layout.
	 */
	private final static int VGAP = 0;

	/**
	 * The horizontal gap of the panel layout.
	 */
	private final static int HGAP = 5;

	/**
	 * Browse URL button.
	 */
	private final JButton nameBtn;

	/**
	 * URL chooser panel.
	 */
	private final JPanel nameChooserPanel;

	/**
	 * <code>true</code> if we are during browse.
	 */
	private boolean isBrowsing = false;

	/**
	 * Author Util.
	 */
	private AuthorUtilAccess utilAccess;

	/**
	 * The default font.
	 */
	private final java.awt.Font defaultFont;

	/**
	 * Constructor.
	 */
	public InplaceButton() {
	    
		nameChooserPanel = new JPanel(new BorderLayout(HGAP, VGAP));
		nameBtn = new JButton();
		if (IS_WIN32) {
			// WE ARE ON WINDOWS
			getNameBtn().setBorder(BorderFactory.createEmptyBorder(3, 6, 3, 5));
		}
		nameChooserPanel.add(getNameBtn(), BorderLayout.CENTER);
		nameChooserPanel.setOpaque(false);

		getNameBtn().addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
					// ESC must cancel the edit.
					e.consume();
					
					cancelEditing();
				}
			}
		});

		FocusListener focusListener = new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				if (e.getOppositeComponent() != getNameBtn()
				// && e.getOppositeComponent() != urlTextField
						&& !isBrowsing) {
					// The focus is outside the components of this editor.
					fireEditingStopped(new EditingEvent(getNameBtn().getText(),
							e.getOppositeComponent() == null));
				}
			}
		};

		getNameBtn().addFocusListener(focusListener);
		defaultFont = getNameBtn().getFont();
	}

	/**
	 * @see ro.sync.ecss.extensions.api.Extension#getDescription()
	 */
	@Override
	public String getDescription() {
		return "A sample implementation that provides a browse button associated with a text field.";
	}

	// /////////////////////////// RENDERER METHODS //////////////////////

	/**
	 * @see ro.sync.ecss.extensions.api.editor.InplaceRenderer#getRendererComponent(ro.sync.ecss.extensions.api.editor.AuthorInplaceContext)
	 */
	@Override
	public Object getRendererComponent(AuthorInplaceContext context) {
		// The renderer will be reused so we must make sure it's properly
		// initialized.
		prepareComponents(context, false);
		return nameChooserPanel;
	}

	/**
	 * @see ro.sync.ecss.extensions.api.editor.InplaceRenderer#getRenderingInfo(ro.sync.ecss.extensions.api.editor.AuthorInplaceContext)
	 */
	@Override
	public RendererLayoutInfo getRenderingInfo(AuthorInplaceContext context) {
		// The renderer will be reused so we must make sure it's properly
		// initialized.
		prepareComponents(context, false);

		return computeRenderingInfo(context);
	}

	/**
	 * Compute the dimension of the editor.
	 * 
	 * @param context
	 *            The current context.
	 * 
	 * @return Layout information.
	 */
	private RendererLayoutInfo computeRenderingInfo(AuthorInplaceContext context) {
		final java.awt.Dimension preferredSize = getNameBtn()
				.getPreferredSize();
		int width = (int) (HGAP + getNameBtn().getPreferredSize().getWidth());

		// Get height correction
		int correction = 0;
		if (IS_ECLIPSE) {
			// When using the renderer for Eclipse, MAC OS with just an icon,
			// the SWING button is smaller than the SWT and when imposing the
			// size to
			// the SWT one the SWT button looks bad.
			correction = 5;
		}

		return new RendererLayoutInfo(getNameBtn().getBaseline(
				preferredSize.width, preferredSize.height), new Dimension(
				width, preferredSize.height + correction));
	}

	/**
	 * @see ro.sync.ecss.extensions.api.editor.InplaceRenderer#getTooltipText(ro.sync.ecss.extensions.api.editor.AuthorInplaceContext,
	 *      int, int)
	 */
	@Override
	public String getTooltipText(AuthorInplaceContext context, int x, int y) {
		// The renderer will be reused so we must make sure it's properly
		// initialized.
		prepareComponents(context, false);

		return "Zum Ändern klicken.";
	}

	public void performAction(AuthorInplaceContext context,
          AuthorAccess authorAccess) {
	}        
	

	// /////////////////////////// EDITOR METHODS //////////////////////
	/**
	 * @see ro.sync.ecss.extensions.api.editor.InplaceEditor#getEditorComponent(ro.sync.ecss.extensions.api.editor.AuthorInplaceContext,
	 *      ro.sync.exml.view.graphics.Rectangle,
	 *      ro.sync.exml.view.graphics.Point)
	 */
	@Override
	public Object getEditorComponent(final AuthorInplaceContext context,
			Rectangle allocation, Point mouseLocation) {
		prepareComponents(context, true);

		final AuthorAccess authorAccess = context.getAuthorAccess();

		getNameBtn().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				isBrowsing = true;

				try {
					performAction(context, authorAccess);
				} finally {
					isBrowsing = false;
				}
			}
		});

		return nameChooserPanel;
	}

	/**
	 * @see ro.sync.ecss.extensions.api.editor.InplaceEditor#getScrollRectangle()
	 */
	@Override
	public Rectangle getScrollRectangle() {
		return null;
	}

	/**
	 * @see ro.sync.ecss.extensions.api.editor.InplaceEditor#requestFocus()
	 */
	@Override
	public void requestFocus() {
		getNameBtn().requestFocus();
	}

	/**
	 * @see ro.sync.ecss.extensions.api.editor.InplaceEditor#getValue()
	 */
	@Override
	public Object getValue() {
		String text = getNameBtn().getText();
		try {
			URL clearedURL = utilAccess.removeUserCredentials(new URL(text));
			return clearedURL.toExternalForm();
		} catch (MalformedURLException e) {
		    LOGGER.error(e, e);
			return text;
		}
	}

	/**
	 * @see ro.sync.ecss.extensions.api.editor.InplaceEditor#stopEditing()
	 */
	@Override
	public void stopEditing() {
		stopEditing(false);
	}

	public void stopEditing(boolean onEnter) {
		String text = getNameBtn().getText();

		if (onEnter) {
			fireNextEditLocationRequested();
		} else {
			fireEditingStopped(new EditingEvent(text));
		}
	}

	/**
	 * @see ro.sync.ecss.extensions.api.editor.InplaceEditor#cancelEditing()
	 */
	@Override
	public void cancelEditing() {
		fireEditingCanceled();
	}

	public void setBtnText(AuthorInplaceContext context) {
		AuthorElement element = context.getElem();
		String elementContent = null;

		try {
			elementContent = element.getTextContent();
		} catch (BadLocationException e) {
		    LOGGER.error(e, e);
		}

		if (elementContent == null || elementContent.isEmpty()) {
			elementContent = "[wählen]";
		}

		getNameBtn().setText(elementContent);

	}

	/**
	 * Prepare UI components.
	 * 
	 * @param context
	 *            The current context.
	 */
	private void prepareComponents(AuthorInplaceContext context,
			boolean forEditing) {
		utilAccess = context.getAuthorAccess().getUtilAccess();

		context.getArguments().get(InplaceEditorArgumentKeys.PROPERTY_COLOR);

		setBtnText(context);

		// // We don't want an UNDO to reset the initial text.
		// UndoManager undoManager = (UndoManager)
		// urlTextField.getDocument().getProperty(UNDO_MANAGER_PROPERTY);
		// if (undoManager != null) {
		// undoManager.discardAllEdits();
		// }

		Font font = (Font) context.getArguments().get(
				InplaceEditorArgumentKeys.FONT);
		if (font != null) {
			java.awt.Font currentFont = new java.awt.Font(font.getName(),
					font.getStyle(), font.getSize());
			getNameBtn().setFont(currentFont);
		} else {
			getNameBtn().setFont(defaultFont);
		}

		Point relMousePos = context.getRelativeMouseLocation();
		boolean rollover = false;
		if (relMousePos != null) {
			RendererLayoutInfo renderInfo = computeRenderingInfo(context);
			nameChooserPanel.setSize(renderInfo.getSize().width,
					renderInfo.getSize().height);
			// Unless we do the layout we can't determine the component under
			// the mouse.
			nameChooserPanel.doLayout();

			Component componentAt = nameChooserPanel.getComponentAt(
					relMousePos.x, relMousePos.y);
			rollover = componentAt == getNameBtn();
		}

		getNameBtn().getModel().setRollover(rollover);
	}

	/**
	 * @see ro.sync.ecss.extensions.api.editor.InplaceRenderer#getCursorType(ro.sync.ecss.extensions.api.editor.AuthorInplaceContext,
	 *      int, int)
	 */
	@Override
	public CursorType getCursorType(AuthorInplaceContext context, int x, int y) {
		return CursorType.CURSOR_NORMAL;
	}

	/**
	 * @see ro.sync.ecss.extensions.api.editor.InplaceRenderer#getCursorType(int,
	 *      int)
	 */
	@Override
	public CursorType getCursorType(int x, int y) {
		return null;
	}

	public JButton getNameBtn() {
		return nameBtn;
	}
}