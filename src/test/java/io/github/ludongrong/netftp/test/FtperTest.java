package io.github.ludongrong.netftp.test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.stream.Collectors;

import org.testng.Assert;
import org.testng.annotations.Test;

import io.github.ludongrong.netftp.FtperConfig;
import io.github.ludongrong.netftp.FtperException;
import io.github.ludongrong.netftp.FtperFactory;
import io.github.ludongrong.netftp.FtperFile;
import io.github.ludongrong.netftp.IFtper;

public class FtperTest {

    @Test(dataProvider = "ftperTestData", dataProviderClass = TestProvider.class)
    public void ftperTest(IFtper ftper) {

        Assert.assertEquals(ftper.createDirectory("/删除测试"), true);
        Assert.assertEquals(ftper.createDirectory("/删除测试/1"), true);
        Assert.assertEquals(ftper.createDirectory("/删除测试/2"), true);
        Assert.assertEquals(ftper.createDirectory("/删除测试/2"), true);
        Assert.assertEquals(ftper.createDirectory("/删除测试/3/4/5/6"), true);

        ByteArrayInputStream byteis = new ByteArrayInputStream("test".getBytes());
        Assert.assertEquals(ftper.upload("/上传测试", "上传.csv", byteis), true);

        ByteArrayOutputStream byteos = new ByteArrayOutputStream();
        Assert.assertEquals(ftper.down("/上传测试", "上传.csv", byteos), true);
        boolean res = new String(byteos.toByteArray()).equals("test");
        Assert.assertEquals(res, true);

        Assert.assertEquals(ftper.move("/上传测试", "上传.csv", "/移动测试/1/a", "move.csv"), true);
        List<FtperFile> ftperFiles = ftper.listFile("/移动测试/1/a").stream().filter(t -> {
            return t.getName().contains("move");
        }).collect(Collectors.toList());
        Assert.assertEquals(ftperFiles.size() == 1, true);

        Assert.assertEquals(ftper.delete("/移动测试/1/a", "move.csv"), true);
        Assert.assertEquals(ftper.delete("/删除测试", "3"), true);
        Assert.assertEquals(ftper.delete("/", "删除测试"), true);
        FtperFactory.close(ftper);
    }

    @Test(dependsOnMethods = {"ftperTest"}, expectedExceptions = RuntimeException.class, dataProvider = "carryTestData",
        dataProviderClass = TestProvider.class)
    public void carryErrorTest(FtperConfig srcConf, FtperConfig destConf) throws FtperException {

        IFtper srcFtper = FtperFactory.createFtper(srcConf);
        Assert.assertEquals(srcFtper.upload("/carryTest测试", "上传.csv", new ByteArrayInputStream("test".getBytes())),
            true);

        IFtper destFtper = srcFtper.cloneFtper();
        srcFtper.carry(destFtper, "/carryTest测试", "上传.csv", "/删除测试", "carry.csv");
    }

    @Test(dependsOnMethods = {"carryErrorTest"}, dataProvider = "carryTestData", dataProviderClass = TestProvider.class)
    public void carryTest(FtperConfig srcConf, FtperConfig destConf) throws FtperException {

        IFtper srcFtper = FtperFactory.createFtper(srcConf);
        Assert.assertEquals(srcFtper.upload("/carryTest测试", "上传.csv", new ByteArrayInputStream("test".getBytes())),
            true);
        IFtper destFtper = FtperFactory.createFtper(destConf);
        Assert.assertEquals(srcFtper.carry(destFtper, "/carryTest测试", "上传.csv", "/删除测试", "carry.csv"), true);

        List<FtperFile> ftperFiles = destFtper.listFile("/删除测试").stream().filter(t -> {
            return t.getName().contains("carry");
        }).collect(Collectors.toList());
        Assert.assertEquals(ftperFiles.size() == 1, true);

        Assert.assertEquals(srcFtper.delete("/", "carryTest测试"), true);
        Assert.assertEquals(destFtper.delete("/", "删除测试"), true);

        FtperFactory.close(srcFtper);
        FtperFactory.close(destFtper);
    }
}
