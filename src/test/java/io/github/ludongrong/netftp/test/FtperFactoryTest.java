package io.github.ludongrong.netftp.test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import org.testng.Assert;
import org.testng.annotations.Test;

import io.github.ludongrong.netftp.FtperConfig;
import io.github.ludongrong.netftp.FtperFactory;
import io.github.ludongrong.netftp.IFtper;

public class FtperFactoryTest {

    @Test(enabled = false, dataProvider = "dirData", dataProviderClass = FtperFactoryProvider.class)
    public void createFtpTest(String dst) {
        FtperConfig ftperConfig = FtperConfig.withHost("127.0.0.1").withPort(21)
            .withUsername("1")
            .withPassword("1")
            .withPasvMode(true)
            .withProtocol(FtperConfig.ProtocolEnum.ftp)
            .build();

        IFtper ftper = FtperFactory.createFtper(ftperConfig);

        ByteArrayInputStream byteis = new ByteArrayInputStream("test".getBytes());
        ByteArrayOutputStream byteos = new ByteArrayOutputStream();

        Assert.assertEquals(ftper.createDirectory(dst), true);
        Assert.assertEquals(ftper.upload(dst, "test.csv", byteis), true);
        Assert.assertEquals(ftper.down(dst, "test.csv", byteos), true);
        Assert.assertEquals(new String(byteos.toByteArray()).equals("test"), true);
        Assert.assertEquals(ftper.move(dst, "test.csv", dst, "test.txt"), true);
        Assert.assertEquals(ftper.delete(dst, "test.txt"), true);
        // Assert.assertEquals(ftper.delete(dst), true);
    }

    @Test(dataProvider = "dirData", dataProviderClass = FtperFactoryProvider.class)
    public void createFtpsTest(String dst) {
        FtperConfig.Builder fBuilder = new FtperConfig.Builder();
        fBuilder.withHost("127.0.0.1");
        fBuilder.withPort(990);
        fBuilder.withUsername("1");
        fBuilder.withPassword("1");
        fBuilder.withPasvMode(true);
        fBuilder.withProtocol(FtperConfig.ProtocolEnum.ftps);

        IFtper ftper = FtperFactory.createFtper(fBuilder.build());

        ByteArrayInputStream byteis = new ByteArrayInputStream("test".getBytes());
        ByteArrayOutputStream byteos = new ByteArrayOutputStream();

        Assert.assertEquals(ftper.createDirectory(dst), true);
        Assert.assertEquals(ftper.upload(dst, "test.csv", byteis), true);
        Assert.assertEquals(ftper.down(dst, "test.csv", byteos), true);
        Assert.assertEquals(new String(byteos.toByteArray()).equals("test"), true);
        Assert.assertEquals(ftper.move(dst, "test.csv", dst, "test.txt"), true);
        Assert.assertEquals(ftper.delete(dst, "test.txt"), true);
        // Assert.assertEquals(ftper.delete(dst), true);
    }

    @Test(enabled = false, dataProvider = "dirData", dataProviderClass = FtperFactoryProvider.class)
    public void createSFtpTest(String dst) {
        FtperConfig.Builder fBuilder = new FtperConfig.Builder();
        fBuilder.withHost("127.0.0.1");
        fBuilder.withPort(22);
        fBuilder.withUsername("1");
        fBuilder.withPassword("1");
        fBuilder.withPasvMode(true);
        fBuilder.withProtocol(FtperConfig.ProtocolEnum.sftp);

        IFtper ftper = FtperFactory.createFtper(fBuilder.build());

        ByteArrayInputStream byteis = new ByteArrayInputStream("test".getBytes());
        ByteArrayOutputStream byteos = new ByteArrayOutputStream();

        Assert.assertEquals(ftper.createDirectory(dst), true);
        Assert.assertEquals(ftper.upload(dst, "test.csv", byteis), true);
        Assert.assertEquals(ftper.down(dst, "test.csv", byteos), true);
        Assert.assertEquals(new String(byteos.toByteArray()).equals("test"), true);
        Assert.assertEquals(ftper.move(dst, "test.csv", dst, "test.txt"), true);
        Assert.assertEquals(ftper.delete(dst, "test.txt"), true);
        // Assert.assertEquals(ftper.delete(dst), true);
    }

}
