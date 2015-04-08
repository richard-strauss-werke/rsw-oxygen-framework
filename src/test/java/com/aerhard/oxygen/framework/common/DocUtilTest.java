package com.aerhard.oxygen.framework.common;

import static org.junit.Assert.*;

import org.junit.Test;

import static org.mockito.Mockito.mock;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.junit.Before;
import org.junit.BeforeClass;
import ro.sync.ecss.extensions.api.access.AuthorWorkspaceAccess;

public class DocUtilTest {

    @BeforeClass
    public static void initClass() {
        Logger rootLogger = Logger.getRootLogger();
        rootLogger.setLevel(Level.INFO);
        rootLogger.addAppender(new ConsoleAppender(new PatternLayout(
                "%-6r [%p] %c - %m%n")));
    }

    private AuthorWorkspaceAccess workspace;

    @Before
    public void initTC() throws Exception {
        workspace = mock(AuthorWorkspaceAccess.class);
    }

    private void run(String val1, String val2, Boolean pass) {

        String[] input = { val1, val2 };
        String[] result;
        result = DocUtil.readAttributeProperties("@" + input[0] + "=\"" + input[1]
                + "\"");

        assertEquals(result[0], input[0]);
        assertEquals(result[1], input[1]);

        System.out.println(result[0]);
        System.out.println(result[1]);

    }

    @Test
    public void getTplAttributeNameTest() throws Exception {

        run("test", "abc", true);
        run("", "", true);
        run("url", "${selection}", true);

        // String[] precisionValues = { "", "medium" };
        // String precisionValueLabels[] = { "", "circa" };
        // String precisionValueLabelsShort[] = { "", "ca." };
        //
        // Editable[] editables = {
        // new Editable("when", "date.when", "", null, null, null),
        // new Editable("cert", "date.precision", "", precisionValues,
        // precisionValueLabels, precisionValueLabelsShort),
        // new Editable("#text", "date.text", "", null, null, null) };
        //
        // editables[0].setValue("2000");
        // editables[1].setValue("medium");
        // editables[2].setValue("a");
        //
        // dialog = new EditDialog(workspace, "", editables);
        //
        //
        // System.out.println(e);
        //
        // assertTrue(editables[0] != null);
        // assertTrue(editables[1] != null);
        // assertTrue(editables[2] != null);
        //
        // assertEquals(editables[0].getValue(), "2000");
        // assertEquals(editables[1].getValue(), "medium");
        // assertEquals(editables[2].getValue(), "a");

    }

}
