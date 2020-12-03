package io.github.ludongrong.netftp.watche;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Optional;
import java.util.Stack;

import io.github.ludongrong.netftp.FtperConfig;
import io.github.ludongrong.netftp.FtperException;
import io.github.ludongrong.netftp.FtperFactory;
import io.github.ludongrong.netftp.FtperFile;
import io.github.ludongrong.netftp.IFtper;
import io.github.ludongrong.netftp.util.LogHelper;

/**
 * 远程目录观察者.
 *
 * @author <a href="mailto:736779458@qq.com">ludongrong</a>
 * @since 2020-11-27
 */
public class DirectoryWatcher extends Observable {

    /**
     * 源目录路径
     */
    private String srcPath;

    /**
     * ftp 配置
     */
    private FtperConfig ftperConfig;

    /**
     * 构造.
     *
     * @param ftperConfig
     *            配置
     * @param srcPath
     *            源目录路径
     */
    public DirectoryWatcher(FtperConfig ftperConfig, String srcPath) {
        this.srcPath = srcPath;
        this.ftperConfig = ftperConfig;
    }

    private List<FtperFile> listFile(IFtper ftper, String path) {
        return Optional.ofNullable(ftper.listFile(path)).orElse(new ArrayList<FtperFile>());
    }

    /**
     * 执行一次目录观察.
     * 
     * <p>
     * 需要考虑执行时间长的问题.
     *
     * @param recursion
     *            递归下钻
     */
    public void hit(boolean recursion) {

        IFtper ftper;
        try {
            ftper = FtperFactory.createFtper(ftperConfig);
        } catch (FtperException e) {
            LogHelper.getLog().error(e.getMessage());
            return;
        }

        Stack<FtperFile> stack = new Stack<FtperFile>();
        stack.addAll(listFile(ftper, srcPath));

        try {
            while (stack.empty() == false) {
                FtperFile ftperFile = stack.pop();

                setChanged();
                notifyObservers(new WatcheParam(ftperFile, srcPath, ftper));

                if (recursion) {
                    if (ftperFile.isDirectory()) {
                        stack.addAll(listFile(ftper, ftperFile.getAbsolutePath()));
                    }
                }
            }
        } finally {
            FtperFactory.close(ftper);
        }
    }
}
