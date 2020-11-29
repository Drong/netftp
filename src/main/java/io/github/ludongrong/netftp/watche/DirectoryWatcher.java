package io.github.ludongrong.netftp.watche;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Optional;
import java.util.Stack;

import io.github.ludongrong.netftp.FtperConfig;
import io.github.ludongrong.netftp.FtperFactory;
import io.github.ludongrong.netftp.FtperFile;
import io.github.ludongrong.netftp.IFtper;

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

    /**
     * 执行一次目录观察.
     *
     * @param recursion
     *            递归下钻
     */
    public void hit(boolean recursion) {

        IFtper ftper = FtperFactory.createFtper(ftperConfig);

        Stack<FtperFile> st = new Stack<FtperFile>();
        st.addAll(Optional.ofNullable(ftper.listFile(srcPath)).orElse(new ArrayList<FtperFile>()));

        while (st.empty() == false) {
            FtperFile ftperFile = st.pop();

            setChanged();
            notifyObservers(new WatcheParam(ftperFile, srcPath, ftper));

            if (recursion) {
                if (ftperFile.isDirectory()) {
                    st.addAll(Optional.ofNullable(ftper.listFile(ftperFile.getAbsolutePath()))
                        .orElse(new ArrayList<FtperFile>()));
                }
            }
        }
    }
}
