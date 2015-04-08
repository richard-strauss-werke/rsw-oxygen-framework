package com.aerhard.oxygen.framework.inplace;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Test;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class NameButtonTest {


    @BeforeClass
    public static void initClass() {
        Logger rootLogger = Logger.getRootLogger();
        rootLogger.setLevel(Level.ERROR);
        rootLogger.addAppender(new ConsoleAppender(new PatternLayout(
                "%-6r [%p] %c - %m%n")));
    }

    @Test
    public void testOpenDialog() {
        
        String a = "aaa\"${url}\"aa";
        
        System.out.println(a);
        
        String b = a.replaceAll("\\$\\{url\\}", "hello");
        
        System.out.println(b);
        
        a= "@abc=\"dd\"";
        
        b = a.substring(1, a.indexOf("="));
        System.out.println(b);
        
        b="";
        System.out.println(a);
        
        Pattern p = Pattern.compile("@(.*?)=\\\"(.*)\\\"");
        Matcher m = p.matcher(a);

        String c="";
        
        if (m.find()) {
            b = m.group(1);
            c = m.group(2);
        }
        
        System.out.println("Result b " +b);
        System.out.println("Result c " +c);
        
        
    }

    
}


