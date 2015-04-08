package com.aerhard.oxygen.framework.inplace;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.aerhard.oxygen.framework.inplace.EditDialog;
import com.aerhard.oxygen.framework.inplace.Editable;

import ro.sync.ecss.extensions.api.access.AuthorWorkspaceAccess;

public class DateDialogTest {

    @BeforeClass
    public static void initClass() {
        Logger rootLogger = Logger.getRootLogger();
        rootLogger.setLevel(Level.INFO);
        rootLogger.addAppender(new ConsoleAppender(new PatternLayout(
                "%-6r [%p] %c - %m%n")));
    }

    EditDialog dialog;

    private AuthorWorkspaceAccess workspace;

    @Before
    public void initTC() throws Exception {
        workspace = mock(AuthorWorkspaceAccess.class);
    }

    @Test
    public void getData() throws Exception {
        
        
        
        String[] precisionValues = { "", "medium" };
        String precisionValueLabels[] = { "", "circa" };
        String precisionValueLabelsShort[] = { "", "ca." };

        Editable[] editables = {
                new Editable("when", "date.when", "", null, null, null),
                new Editable("cert", "date.precision", "", precisionValues,
                        precisionValueLabels, precisionValueLabelsShort),
                new Editable("#text", "date.text", "", null, null, null) };

        editables[0].setValue("2000");
        editables[1].setValue("medium");
        editables[2].setValue("a");
        
        dialog = new EditDialog(workspace, "", editables);
        
        Boolean e = dialog.submitData();
        System.out.println(e);
        
        assertTrue(editables[0] != null);
        assertTrue(editables[1] != null);
        assertTrue(editables[2] != null);

        assertEquals(editables[0].getValue(), "2000");
        assertEquals(editables[1].getValue(), "medium");
        assertEquals(editables[2].getValue(), "a");
        
    }    
    

}
