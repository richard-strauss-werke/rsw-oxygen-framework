package com.aerhard.oxygen.framework;

import com.aerhard.oxygen.framework.tei.InsertExternalFragmentTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.aerhard.oxygen.framework.common.DocUtilTest;
import com.aerhard.oxygen.framework.inplace.DateDialogTest;
import com.aerhard.oxygen.framework.inplace.NameButtonTest;
import com.aerhard.oxygen.framework.inplace.RendButtonTest;

@RunWith(Suite.class)
@SuiteClasses({ InsertExternalFragmentTest.class, NameButtonTest.class,
        DateDialogTest.class, RendButtonTest.class, DocUtilTest.class })
public class AllTests {

}
