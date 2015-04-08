package com.aerhard.oxygen.framework.inplace;

import com.aerhard.oxygen.framework.inplace.RendButton;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.junit.BeforeClass;
import org.junit.Test;

import com.aerhard.oxygen.framework.inplace.EditDialog;

public class RendButtonTest {

    @BeforeClass
    public static void initClass() {
        Logger rootLogger = Logger.getRootLogger();
        rootLogger.setLevel(Level.INFO);
        rootLogger.addAppender(new ConsoleAppender(new PatternLayout(
                "%-6r [%p] %c - %m%n")));
    }


    @Test
    public void regexTest() throws Exception {

        RendButton b = new RendButton();

        String matchingString = b.matchingString;
        String regex = "indent-left\\(" + matchingString + "chars\\)";

        System.out.println(regex);

        String inputString = "abcabc "
                + (regex + "").replace("\\", "").replace(matchingString, "2")
                + " abcabc";
        System.out.println(inputString);

        String replaceMatchingString = b.replaceMatchingString;

        String replaceRegex = regex.replace(matchingString, replaceMatchingString);

        // Pattern p = Pattern.compile(s);
        System.out.println(regex);
        String x = "xxx" + inputString + "yyy";
        System.out.println(replaceMatchingString);
        System.out.println(replaceRegex);

        System.out.println(x.replaceAll("indent-left\\(.*?\\)", ""));

        System.out.println(x.replaceAll(replaceRegex, ""));
        System.out.println("".replaceAll(replaceRegex, ""));

    }

}
