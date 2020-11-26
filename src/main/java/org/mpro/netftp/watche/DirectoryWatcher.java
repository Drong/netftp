package org.mpro.netftp.watche;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Optional;
import java.util.Stack;

import org.mpro.netftp.FtperConfig;
import org.mpro.netftp.FtperFactory;
import org.mpro.netftp.FtperFile;
import org.mpro.netftp.IFtper;

/**
 * 监控 ftp 服务下的某个目录。
 * 
 * @author 736779458@qq.com
 * @version 1.0
 * @updated 22-十一月-2020 22:53:36
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
     * 构造
     * 
     * @param ftperConfig
     *            ftp 配置
     * @param srcDirectoryPath
     *            源目录路径
     */
    public DirectoryWatcher(FtperConfig ftperConfig, String srcPath) {
        this.srcPath = srcPath;
        this.ftperConfig = ftperConfig;
    }

    /**
     * 监控
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
