package org.mpro.netftp.test;

import org.testng.annotations.DataProvider;

/**
 * @author Think
 * @version 1.0
 * @created 03-十一月-2020 11:32:55
 */
public class FtperFactoryProvider {

    @DataProvider(name = "dirData")
    public Object[][] dirData() {
        return new Object[][] { new Object[] { "/data/我factory" }, new Object[] { "/data/我factory/1/cdmkdir" },
                new Object[] { "/data/我factory/1/a" }, new Object[] { "/data/我factory" } };
    }

}