package org.mpro.netftp;

import java.util.List;

import org.apache.commons.net.ftp.FTPFile;

import cn.hutool.core.util.StrUtil;
import lombok.Getter;
import lombok.Setter;

/**
 * 文件
 * 
 * @author 736779458@qq.com
 * @version 1.0
 * @created 28-九月-2020 22:45:42
 */
public class FtperFile extends FTPFile {

    private static final long serialVersionUID = -2824465580419813985L;

    @Getter
    @Setter
    private String dirPath;

    @Getter
    @Setter
    private int resid;

    @Getter
    @Setter
    private boolean exists;

    public static FtperFile matchSelector(List<FtperFile> ftpFiles, String name) {
        FtperFile resFile = null;
        for (FtperFile ftpFile : ftpFiles) {
            if (ftpFile.getName().equals(name)) {
                resFile = ftpFile;
                break;
            }
        }
        return resFile;
    }

    public String getAbsolutePath() {
        return getAbsolutePath(dirPath, getName());
    }

    public static String getAbsolutePath(String src, String fname) {
        if (src.endsWith(StrUtil.SLASH)) {
            return src + fname;
        } else {
            return src + StrUtil.SLASH + fname;
        }
    }

}