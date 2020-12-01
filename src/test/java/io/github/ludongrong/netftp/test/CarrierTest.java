package io.github.ludongrong.netftp.test;

import java.io.ByteArrayInputStream;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import io.github.ludongrong.netftp.FtperConfig;
import io.github.ludongrong.netftp.FtperFactory;
import io.github.ludongrong.netftp.IFtper;

public class CarrierTest {

    FtperConfig.Builder fBuilder;

    @BeforeClass
    public void beforeClass() {
        fBuilder = new FtperConfig.Builder();
        fBuilder.withHost("127.0.0.1");
        fBuilder.withPort(21);
        fBuilder.withUsername("1");
        fBuilder.withPassword("1");
        fBuilder.withPasvMode(true);
        fBuilder.withProtocol(FtperConfig.ProtocolEnum.ftp);
    }

    @Test
    public void createFtpTest() {
        ByteArrayInputStream byteis = new ByteArrayInputStream("test".getBytes());

        IFtper ftper = FtperFactory.createFtper(fBuilder.build());
        ftper.upload("/test", "1.csv", byteis);

        IFtper sendFtper = ftper.cloneFtper();

        Assert.assertEquals(ftper.carry(sendFtper, "/test", "1.csv", "/test", "2.csv"), true);
    }
}
