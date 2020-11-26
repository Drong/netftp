package org.mpro.netftp.watche;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.Calendar;
import java.util.List;

import org.mpro.netftp.FtperFile;
import org.mpro.netftp.IFtper;

import lombok.Getter;

/**
 * @author Think
 * @version 1.0
 * @created 28-九月-2020 22:45:42
 */
public class WatcheParam implements Serializable, IFtper {

    private static final long serialVersionUID = 648930620138876396L;

    public WatcheParam(FtperFile ftperFile, String srcPath, IFtper ftper) {
        super();
        this.ftperFile = ftperFile;
        this.srcPath = srcPath;
        this.ftper = ftper;
    }

    @Getter
    private String srcPath;

    @Getter
    private FtperFile ftperFile;

    @Getter
    private IFtper ftper;

    public String getDirPath() {
        return ftperFile.getDirPath();
    }

    public void setDirPath(String dirPath) {
        ftperFile.setDirPath(dirPath);
    }

    public String getName() {
        return ftperFile.getName();
    }

    public void setName(String name) {
        ftperFile.setName(name);
    }

    public String getAbsolutePath() {
        return ftperFile.getAbsolutePath();
    }

    public boolean isDirectory() {
        return ftperFile.isDirectory();
    }

    public void setExists(boolean exists) {
        ftperFile.setExists(exists);
    }

    public Calendar getLastModifyTime() {
        return ftperFile.getTimestamp();
    }

    @Override
    public String getHost() {
        return ftper.getHost();
    }

    @Override
    public int getPort() {
        return ftper.getPort();
    }

    @Override
    public String getUsername() {
        return ftper.getUsername();
    }

    @Override
    public boolean createDirectory(String dst) {
        return ftper.createDirectory(dst);
    }

    @Override
    public List<FtperFile> listFile(String dst) {
        return ftper.listFile(dst);
    }

    @Override
    public boolean upload(String dst, String filename, InputStream is) {
        return ftper.upload(dst, filename, is);
    }

    @Override
    public boolean down(String dst, String fname, OutputStream os) {
        return ftper.down(dst, fname, os);
    }

    @Override
    public boolean move(String src, String sname, String dst, String dname) {
        return ftper.move(src, sname, dst, dname);
    }

    @Override
    public boolean delete(String dst, String fname) {
        return ftper.delete(dst, fname);
    }

    @Override
    public void close() {
        ftper.close();
    }

    @Override
    public IFtper cloneFtper() {
        return ftper.cloneFtper();
    }
}