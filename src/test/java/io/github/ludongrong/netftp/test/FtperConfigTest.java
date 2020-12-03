package io.github.ludongrong.netftp.test;

import java.util.Arrays;

import org.testng.Assert;
import org.testng.annotations.Test;

import io.github.ludongrong.netftp.FtperConfig;
import io.github.ludongrong.netftp.FtperConfig.ProtocolEnum;
import io.github.ludongrong.netftp.FtperException;

public class FtperConfigTest {

    @Test
    public void checkAliveTest() throws FtperException {
        FtperConfig conf = FtperConfig.withHost("127.0.0.1").withPort(21).withUsername("1").withPassword("1")
            .withPasvMode(true).withProtocol(ProtocolEnum.ftp).build();
        System.out.println(Arrays.toString(conf.checkAlive("/")));
        System.out.println(Arrays.toString(conf.checkAlive("/a")));
        Assert.assertEquals(conf.checkAlive("/")[0].equals("0"), true);
    }
}
