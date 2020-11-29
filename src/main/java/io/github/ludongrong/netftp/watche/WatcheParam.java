package io.github.ludongrong.netftp.watche;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.Calendar;
import java.util.List;

import io.github.ludongrong.netftp.FtperFile;
import io.github.ludongrong.netftp.IFtper;
import lombok.Getter;

/**
 * 观察参数.
 *
 * @author <a href="mailto:736779458@qq.com">ludongrong</a>
 * @since 2020-11-27
 */
public class WatcheParam implements Serializable, IFtper {

    private static final long serialVersionUID = 648930620138876396L;

    /**
     * 构造.
     *
     * @param ftperFile
     *            文件
     * @param srcPath
     *            扫描根目录
     * @param ftper
     *            ftp客户端
     */
    public WatcheParam(FtperFile ftperFile, String srcPath, IFtper ftper) {
        super();
        this.ftperFile = ftperFile;
        this.srcPath = srcPath;
        this.ftper = ftper;
    }

    @Getter
    private String srcPath;

    private FtperFile ftperFile;

    private IFtper ftper;

    /**
     * 获取文件目录.
     *
     * @return 文件目录
     */
    public String getDirPath() {
        return ftperFile.getDirPath();
    }

    /**
     * 设置文件目录.
     *
     * @param dirPath
     *            文件目录
     */
    public void setDirPath(String dirPath) {
        ftperFile.setDirPath(dirPath);
    }

    /**
     * 获取文件名.
     *
     * @return 文件名
     */
    public String getName() {
        return ftperFile.getName();
    }

    /**
     * 设置文件名.
     *
     * @param name
     *            文件名
     */
    public void setName(String name) {
        ftperFile.setName(name);
    }

    /**
     * 获取绝对路径.
     *
     * @return 绝对路径
     */
    public String getAbsolutePath() {
        return ftperFile.getAbsolutePath();
    }

    /**
     * 判断是否是目录.
     *
     * @return true表示是目录；false表示不是目录
     */
    public boolean isDirectory() {
        return ftperFile.isDirectory();
    }

    /**
     * 设置文件是否存在.
     *
     * @param exists
     *            true表示存储；false表示不存在
     */
    public void setExists(boolean exists) {
        ftperFile.setExists(exists);
    }

    /**
     * 获取文件最后修改时间.
     *
     * @return 文件最后修改时间
     */
    public Calendar getLastModifyTime() {
        return ftperFile.getTimestamp();
    }

    /**
     * @see io.github.ludongrong.netftp.IFtper#getHost()
     */
    @Override
    public String getHost() {
        return ftper.getHost();
    }

    /**
     * @see io.github.ludongrong.netftp.IFtper#getPort()
     */
    @Override
    public int getPort() {
        return ftper.getPort();
    }

    /**
     * @see io.github.ludongrong.netftp.IFtper#getUsername()
     */
    @Override
    public String getUsername() {
        return ftper.getUsername();
    }

    /**
     * @see io.github.ludongrong.netftp.IFtper#createDirectory(java.lang.String)
     */
    @Override
    public boolean createDirectory(String dst) {
        return ftper.createDirectory(dst);
    }

    /**
     * @see io.github.ludongrong.netftp.IFtper#listFile(java.lang.String)
     */
    @Override
    public List<FtperFile> listFile(String dst) {
        return ftper.listFile(dst);
    }

    /**
     * @see io.github.ludongrong.netftp.IFtper#upload(java.lang.String, java.lang.String, java.io.InputStream)
     */
    @Override
    public boolean upload(String dst, String filename, InputStream is) {
        return ftper.upload(dst, filename, is);
    }

    /**
     * @see io.github.ludongrong.netftp.IFtper#down(java.lang.String, java.lang.String, java.io.OutputStream)
     */
    @Override
    public boolean down(String dst, String fname, OutputStream os) {
        return ftper.down(dst, fname, os);
    }

    /**
     * @see io.github.ludongrong.netftp.IFtper#move(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public boolean move(String src, String sname, String dst, String dname) {
        return ftper.move(src, sname, dst, dname);
    }

    /**
     * @see io.github.ludongrong.netftp.IFtper#delete(java.lang.String, java.lang.String)
     */
    @Override
    public boolean delete(String dst, String fname) {
        return ftper.delete(dst, fname);
    }

    /**
     * @see io.github.ludongrong.netftp.IFtper#close()
     */
    @Override
    public void close() {
        ftper.close();
    }

    /**
     * @see io.github.ludongrong.netftp.IFtper#cloneFtper()
     */
    @Override
    public IFtper cloneFtper() {
        return ftper.cloneFtper();
    }
}