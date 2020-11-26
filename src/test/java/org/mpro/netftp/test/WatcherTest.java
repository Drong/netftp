package org.mpro.netftp.test;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.stream.Collectors;

import org.mpro.netftp.FtperConfig;
import org.mpro.netftp.FtperFactory;
import org.mpro.netftp.IFtper;
import org.mpro.netftp.watche.DeleteAction;
import org.mpro.netftp.watche.DirectoryFiler;
import org.mpro.netftp.watche.DownloadAction;
import org.mpro.netftp.watche.FileFiler;
import org.mpro.netftp.watche.FileModifyTimeFilter;
import org.mpro.netftp.watche.Filter;
import org.mpro.netftp.watche.MoveAction;
import org.mpro.netftp.watche.TagFilter;
import org.mpro.netftp.watche.Watched;
import org.mpro.netftp.watche.DirectoryWatcher;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class WatcherTest {

    private String projectPath = System.getProperty("user.dir");

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

        IFtper ftper = FtperFactory.createFtper(fBuilder.build());

        ByteArrayInputStream byteis = new ByteArrayInputStream("test".getBytes());
        for (int i = 0; i < 10; i++) {
            boolean result = ftper.upload("/test/watcher", "test" + i + ".csv", byteis);
            System.out.println(result);
        }
    }

    @Test
    public void createFtpTest() {
        IFtper ftper = FtperFactory.createFtper(fBuilder.build());
        long count = ftper.listFile("/test/watcher").stream().filter((ftperFile) -> {
            return ftperFile.getName().contains("1");
        }).collect(Collectors.counting());
        Assert.assertEquals(count == 1, true);

        Watched watched = new Watched(
            new Filter[] {new DirectoryFiler("/test/w*", true), new FileFiler("*1*", true), new DeleteAction()});
        DirectoryWatcher watcher = new DirectoryWatcher(fBuilder.build(), "/test");
        watcher.addObserver(watched);
        watcher.hit(true);

        ftper = FtperFactory.createFtper(fBuilder.build());
        count = ftper.listFile("/test/watcher").stream().filter((ftperFile) -> {
            return ftperFile.getName().contains("1");
        }).collect(Collectors.counting());
        Assert.assertEquals(count == 0, true);

        watched = new Watched(new Filter[] {new FileFiler("*.csv", true), new MoveAction("/test", false)});
        watcher = new DirectoryWatcher(fBuilder.build(), "/test");
        watcher.addObserver(watched);
        watcher.hit(true);

        count = ftper.listFile("/test").stream().collect(Collectors.counting());
        System.out.println(count);
        Assert.assertEquals(count == 10, true);

        watched = new Watched(new Filter[] {new FileFiler("*0.csv", true), new DownloadAction(projectPath)});
        watcher = new DirectoryWatcher(fBuilder.build(), "/test");
        watcher.addObserver(watched);
        watcher.hit(true);

        watched = new Watched(new Filter[] {new FileFiler("*0.csv", true), new TagFilter("/test", false),
            new DownloadAction(projectPath)});
        watcher = new DirectoryWatcher(fBuilder.build(), "/test");
        watcher.addObserver(watched);
        watcher.hit(true);

        File file = new File(projectPath, "test0.csv");
        Assert.assertEquals(file.exists(), true);
        file.deleteOnExit();

        watched =
            new Watched(new Filter[] {new FileModifyTimeFilter(System.currentTimeMillis(), true), new DeleteAction()});
        watcher = new DirectoryWatcher(fBuilder.build(), "/test");
        watcher.addObserver(watched);
        watcher.hit(true);

        count = ftper.listFile("/test").stream().collect(Collectors.counting());
        Assert.assertEquals(count == 0, true);
    }
}
