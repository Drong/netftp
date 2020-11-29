package io.github.ludongrong.netftp.test;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.stream.Collectors;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import io.github.ludongrong.netftp.FtperConfig;
import io.github.ludongrong.netftp.FtperFactory;
import io.github.ludongrong.netftp.IFtper;
import io.github.ludongrong.netftp.watche.DeleteAction;
import io.github.ludongrong.netftp.watche.DirectoryFiler;
import io.github.ludongrong.netftp.watche.DirectoryWatcher;
import io.github.ludongrong.netftp.watche.DownloadAction;
import io.github.ludongrong.netftp.watche.FileFiler;
import io.github.ludongrong.netftp.watche.FileModifyTimeFilter;
import io.github.ludongrong.netftp.watche.Filter;
import io.github.ludongrong.netftp.watche.MoveAction;
import io.github.ludongrong.netftp.watche.TagFilter;
import io.github.ludongrong.netftp.watche.Watched;

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
