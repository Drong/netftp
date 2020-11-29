package io.github.ludongrong.netftp;

import java.util.List;

import org.apache.commons.net.ftp.FTPFile;

import cn.hutool.core.util.StrUtil;
import lombok.Getter;
import lombok.Setter;

/**
 * ftp文件.
 *
 * @author <a href="mailto:736779458@qq.com">ludongrong</a>
 * @since 2020-11-27
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

    /**
     * 从ftp文件列表中找到指定文件.
     *
     * @param ftpFiles
     *            文件列表
     * @param name
     *            文件名
     * @return ftp文件
     */
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

    /**
     * 获取ftp文件绝对路径.
     *
     * @return 绝对路径
     */
    public String getAbsolutePath() {
        return getAbsolutePath(dirPath, getName());
    }

    /**
     * 获取ftp文件绝对路径.
     *
     * @param src
     *            目录路径
     * @param fname
     *            文件名
     * @return 绝对路径
     */
    public static String getAbsolutePath(String src, String fname) {
        if (src.endsWith(StrUtil.SLASH)) {
            return src + fname;
        } else {
            return src + StrUtil.SLASH + fname;
        }
    }

}