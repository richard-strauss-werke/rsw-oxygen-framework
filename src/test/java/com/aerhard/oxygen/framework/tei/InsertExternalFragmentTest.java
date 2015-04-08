package com.aerhard.oxygen.framework.tei;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.io.IOException;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import ro.sync.ecss.extensions.api.AuthorAccess;
import ro.sync.ecss.extensions.api.AuthorOperationException;

import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class InsertExternalFragmentTest {

    private InsertExternalFragment action;
    private AuthorAccess authorAccess;

    @BeforeClass
    public static void initClass() {
        Logger rootLogger = Logger.getRootLogger();
        rootLogger.setLevel(Level.INFO);
        rootLogger.addAppender(new ConsoleAppender(new PatternLayout(
                "%-6r [%p] %c - %m%n")));
    }
    
    @Before
    public void initTC() {
        authorAccess = mock(AuthorAccess.class);
        action = new InsertExternalFragment();
    }

//    @Test
//    public void performRequestTest() throws AuthorOperationException, IOException {
//
////      String url = "http://localhost:8080/exist/apps/adb/snippets/memorized?cat=latest&user=alx";
//
//        String request = action.request(url);
//
//        if (request == null) {
//            fail("request is null");
//        }
//
//        System.out.println(request);
//
//    }
    
    @Test
    public void extractTextContentText() {
        
        String[] text = {"<kk>", "j<j>j", "</kkk>"};
        
        String[] a = action.splitContentFromElement(text[0] + text[1] + text[2]);
        assertEquals(a[0], text[0] + text[2]);
        assertEquals(a[1], text[1]);
        
        System.out.println(a[0]);
        System.out.println(a[1]);
        
        a = action.splitContentFromElement("<abc/>");
        assertNull(a);

        a = action.splitContentFromElement("message");
        assertNull(a);

        
    }

}
