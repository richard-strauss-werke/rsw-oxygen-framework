package com.aerhard.oxygen.framework.tei;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import ro.sync.ecss.extensions.api.AuthorOperationException;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;

@RunWith(MockitoJUnitRunner.class)
public class CreateDocumentTest {

    @Test
    public void performRequestTest() throws AuthorOperationException, IOException {
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
    }

    public static void main (String[] args) throws IOException, AuthorOperationException {

        CreateDocument c = new CreateDocument();

        String url = "http://localhost:8080/exist/apps/adb/edit/create?tpl=";
        String tpl = "performance";
        String user = "";

        String result = c.createDocument(null, url, tpl, user);
        System.out.println(result);

    }


}
