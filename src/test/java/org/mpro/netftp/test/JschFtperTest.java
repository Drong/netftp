package org.mpro.netftp.test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import org.mpro.netftp.FtperConfig;
import org.mpro.netftp.IFtper;
import org.mpro.netftp.JschFtper;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class JschFtperTest {

    private IFtper ftper;

    @BeforeClass
    public void beforeClass() {
        FtperConfig.Builder fBuilder = new FtperConfig.Builder();
        fBuilder.withHost("10.48.186.68");
        fBuilder.withPort(22);
        fBuilder.withUsername("root");
        fBuilder.withPassword("rSpiniwy_As1!");
        fBuilder.withType(FtperConfig.TypeEnum.jsch);

        FtperConfig ftperConfig = fBuilder.build();

        JschFtper.Builder builder = new JschFtper.Builder();
        ftper = builder.build(ftperConfig);
    }

    @AfterClass
    public void afterClass() {
        ftper.close();
    }

    @Test(dataProvider = "cdIfMkdirTestData", dataProviderClass = StaticProvider.class)
    public void cdIfMkdirTest(String dst) {
        Assert.assertEquals(ftper.createDirectory(dst), true);
    }

    @Test(dataProvider = "deleteTestData", dataProviderClass = StaticProvider.class)
    public void deleteDirectoryTest(String dst, String fname) {
        Assert.assertEquals(ftper.createDirectory(dst + "/" + fname), true);
        Assert.assertEquals(ftper.delete(dst, fname), true);
    }

    @Test(dataProvider = "uploadTestData", dependsOnMethods = {"deleteDirectoryTest"},
        dataProviderClass = StaticProvider.class)
    public void uploadTest(String dst, String fname) {
        ByteArrayInputStream byteis = new ByteArrayInputStream("test".getBytes());

        Assert.assertEquals(ftper.createDirectory(dst), true);
        Assert.assertEquals(ftper.upload(dst, fname, byteis), true);
    }

    @Test(dataProvider = "uploadTestData", dependsOnMethods = {"uploadTest"}, dataProviderClass = StaticProvider.class)
    public void downloadTest(String dst, String fname) {
        ByteArrayOutputStream byteos = new ByteArrayOutputStream();
        Assert.assertEquals(ftper.down(dst, fname, byteos), true);

        boolean res = new String(byteos.toByteArray()).equals("test");
        Assert.assertEquals(res, true);
    }

    @Test(dataProvider = "moveTestData", dependsOnMethods = {"downloadTest"}, dataProviderClass = StaticProvider.class)
    public void moveTest(String dst, String fname, String mname) {
        Assert.assertEquals(ftper.move(dst, fname, dst, mname), true);
    }

    @Test(dataProvider = "moveTestData", dependsOnMethods = {"moveTest"}, dataProviderClass = StaticProvider.class)
    public void deleteFileTest(String dst, String fname, String mname) {
        Assert.assertEquals(ftper.delete(dst, mname), true);
    }

}
