package org.mpro.netftp.test;

import org.testng.annotations.DataProvider;

/**
 * @author Think
 * @version 1.0
 * @created 03-十一月-2020 11:32:55
 */
public class StaticProvider {

    public StaticProvider() {

    }
    
    @DataProvider(name = "cdIfMkdirTestData")
    public Object[][] cdIfMkdirTestData() {
        return new Object[][] { new Object[] { "/root/我/cdmkdir" }, 
            new Object[] { "/root/我/a" },
            new Object[] { "/root/我/a/b" } };
    }
    
    @DataProvider(name = "deleteTestData")
    public Object[][] deleteTestData() {
        return new Object[][] { new Object[] { "/root/我", "cdmkdir" },
            new Object[] { "/root", "我" } };
    }
    
    @DataProvider(name = "uploadTestData")
    public Object[][] uploadTestData() {
        return new Object[][] { new Object[] { "/root", "test.csv" } };
    }
    
    @DataProvider(name = "moveTestData")
    public Object[][] moveTestData() {
        return new Object[][] { new Object[] { "/root", "test.csv", "test1.csv" } };
    }

}