package io.github.ludongrong.netftp.test;

import org.testng.annotations.DataProvider;

import io.github.ludongrong.netftp.ApacheFtper;
import io.github.ludongrong.netftp.FtperConfig;
import io.github.ludongrong.netftp.FtperConfig.ProtocolEnum;
import io.github.ludongrong.netftp.FtperException;
import io.github.ludongrong.netftp.IFtper;

/**
 * xxx.
 *
 * @author <a href="mailto:736779458@qq.com">ludongrong</a>
 * @since 2020-11-27
 */
public class TestProvider {

    @DataProvider(name = "ftperTestData")
    public Object[][] ftperTestData() throws FtperException {
        IFtper apcheFtper =
            new ApacheFtper.Builder().build(FtperConfig.withHost("127.0.0.1").withPort(21).withUsername("1")
                .withPassword("1").withPasvMode(true).withType(FtperConfig.TypeEnum.apache_common).build());

        // IFtper ftp4jFtper = new Ftp4jFtper.Builder().build(FtperConfig.withHost("127.0.0.1").withPort(21)
        // .withUsername("1").withPassword("1").withPasvMode(true).withType(FtperConfig.TypeEnum.ftp4j).build());

        // ftper = new JschFtper.Builder().build(FtperConfig.withHost("10.48.186.68").withPort(22).withUsername("root")
        // .withPassword("rSpiniwy_As1!").withType(FtperConfig.TypeEnum.jsch).build());

        return new Object[][] {new Object[] {apcheFtper}};
    }

    @DataProvider(name = "createFtperTestData")
    public Object[][] createFtperTestData() {
        FtperConfig ftpConf = FtperConfig.withHost("127.0.0.1").withPort(21).withUsername("1").withPassword("1")
            .withPasvMode(true).withProtocol(ProtocolEnum.ftp).build();

        // FtperConfig ftpsConf = FtperConfig.withHost("127.0.0.1").withPort(21).withUsername("1").withPassword("1")
        // .withPasvMode(true).withProtocol(ProtocolEnum.ftps).build();

        // FtperConfig sftpConf = FtperConfig.withHost("127.0.0.1").withPort(21).withUsername("1").withPassword("1")
        // .withPasvMode(true).withProtocol(ProtocolEnum.sftp).build();

        return new Object[][] {new Object[] {ftpConf}};
    }

    @DataProvider(name = "watchTestData")
    public Object[][] watchTestData() {
        FtperConfig ftpConfig = FtperConfig.withHost("127.0.0.1").withPort(21).withUsername("1").withPassword("1")
            .withPasvMode(true).withProtocol(ProtocolEnum.ftp).build();

        return new Object[][] {new Object[] {ftpConfig}};
    }

    @DataProvider(name = "carryTestData")
    public Object[][] carryTestData() {
        FtperConfig srcConf = FtperConfig.withHost("127.0.0.1").withPort(21).withUsername("1").withPassword("1")
            .withPasvMode(true).withProtocol(ProtocolEnum.ftp).build();

        FtperConfig destConf = FtperConfig.withHost("127.0.0.1").withPort(21).withUsername("2").withPassword("2")
            .withPasvMode(true).withProtocol(ProtocolEnum.ftp).build();

        return new Object[][] {new Object[] {srcConf, destConf}};
    }
}