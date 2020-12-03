package io.github.ludongrong.netftp.test;

import org.testng.Assert;
import org.testng.annotations.Test;

import io.github.ludongrong.netftp.FtperConfig;
import io.github.ludongrong.netftp.FtperException;
import io.github.ludongrong.netftp.FtperFactory;
import io.github.ludongrong.netftp.IFtper;

public class FtperFactoryTest {

    @Test(dataProvider = "createFtperTestData", dataProviderClass = TestProvider.class)
    public void createFtperTest(FtperConfig ftperConfig) throws FtperException {
        IFtper ftper = FtperFactory.createFtper(ftperConfig);
        Assert.assertEquals(ftper.createDirectory("/createFtper/我"), true);
        Assert.assertEquals(ftper.delete("/createFtper", "我"), true);
        FtperFactory.close(ftper);
    }
}
