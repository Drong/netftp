package io.github.ludongrong.netftp;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;

import org.apache.commons.net.ftp.FTPFile;

import io.github.ludongrong.netftp.util.LogHelper;
import io.github.ludongrong.netftp.util.PathHelper;

/**
 * Ftp 协议操作抽象，操作行为包括.
 * 
 * <ul>
 * <li>创建目录</li>
 * <li>获取文件列表</li>
 * <li>上传文件</li>
 * <li>下载文件</li>
 * <li>移动文件</li>
 * </ul>
 *
 * @author <a href="mailto:736779458@qq.com">ludongrong</a>
 * @since 2020-11-27
 */
public abstract class AbstsactFtper implements IFtper {

    /** 配置 */
    protected FtperConfig ftperConfig;

    /**
     * 判断文件是否存在.
     * 
     * @param matchFile
     *            文件
     * @return true 表示存在；false 表示不存在
     */
    protected boolean exists(FTPFile matchFile) {

        boolean exists;
        if (matchFile == null) {
            exists = false;
        } else {
            exists = true;
        }
        return exists;
    }

    /**
     * 判断文件类型是文件的文件是否存在.
     * 
     * @param matchFile
     *            文件
     * @return true 表示存在；false 表示不存在
     */
    protected boolean existsFile(FTPFile matchFile) {

        boolean exists;
        if (matchFile == null) {
            exists = false;
        } else if (matchFile.isDirectory()) {
            exists = false;
        } else {
            exists = true;
        }
        return exists;
    }

    /**
     * 获取文件列表.
     * 
     * @param dst
     *            目录路径
     * @return 文件列表
     * @throws LsException
     *             获取异常
     */
    protected abstract List<FtperFile> ls(String dst) throws LsException;

    /**
     * 按路径层级一层一层进入并获取最终指定文件路径下的文件列表.
     * 
     * @param <T>
     *            结果类型
     * @param dst
     *            目录路径
     * @param proccesser
     *            层处理者
     * @return 结果
     */
    protected abstract <T extends Proccesser<?>> T listFunc(String dst, T proccesser);

    /**
     * @see io.github.ludongrong.netftp.IFtper#listFile(java.lang.String)
     */
    @Override
    public List<FtperFile> listFile(String dst) {

        PathHelper.check(dst);

        return listFunc(dst, new Proccesser<List<FtperFile>>() {

            List<FtperFile> result = null;

            @Override
            public boolean pre(String src, String fname, boolean exists) {
                return exists;
            }

            @Override
            public void last(List<FtperFile> ftpFiles) {
                result = ftpFiles;
            }

            @Override
            public List<FtperFile> result() {
                return result;
            }
        }).result();
    }

    /**
     * 切换工作目录.
     * 
     * @param dst
     *            目录路径
     * @return 操作结果。true表示成功；false表示失败
     */
    protected abstract boolean changeWorkingDirectory(String dst);

    /**
     * 创建目录.
     * 
     * @param dst
     *            目录路径
     * @return 操作结果。true表示成功；false表示失败
     */
    protected abstract boolean mkdir(String dst);

    /**
     * @see io.github.ludongrong.netftp.IFtper#createDirectory(java.lang.String)
     */
    @Override
    public boolean createDirectory(String dst) {

        PathHelper.check(dst);

        return listFunc(dst, new Proccesser<Boolean>() {

            boolean result = false;

            @Override
            public boolean pre(String src, String fname, boolean exists) {
                if (exists == false) {
                    return mkdir(FtperFile.getAbsolutePath(src, fname));
                }
                return exists;
            }

            @Override
            public void last(List<FtperFile> ftpFiles) {
                result = changeWorkingDirectory(dst);
            }

            @Override
            public Boolean result() {
                return result;
            }
        }).result();
    }

    /**
     * 删除目录.
     * 
     * @param dst
     *            目录路径
     * @return 操作结果。true表示成功；false表示失败
     */
    protected abstract boolean rmdir(String dst);

    /**
     * 删除文件.
     * 
     * @param fname
     *            文件路径
     * @return 操作结果。true表示成功；false表示失败
     */
    protected abstract boolean rm(String fname);

    /**
     * 删除目录下文件类型是文件的文件.
     * 
     * @param rlist
     *            文件列表
     * @return 操作结果。true表示成功；false表示失败
     */
    private boolean deleteFileRetainDirectory_(List<FtperFile> rlist) {

        Iterator<FtperFile> iterator = rlist.iterator();
        while (iterator.hasNext()) {
            FtperFile item = iterator.next();
            if (item.isDirectory() == false) {
                if (rm(item.getAbsolutePath()) == false) {
                    return false;
                }
                iterator.remove();
            }
        }
        return true;
    }

    /**
     * 删除文件
     * 
     * @param matchFile
     *            文件
     * @return 操作结果。true表示成功；false表示失败
     */
    protected boolean delete(FtperFile matchFile) {

        Stack<FtperFile> st = new Stack<FtperFile>();
        st.push(matchFile);

        boolean res = false;

        while (st.empty() == false) {
            FtperFile ftpFileBo = st.pop();
            if (ftpFileBo.isDirectory()) {
                List<FtperFile> rlist;
                try {
                    rlist = ls(ftpFileBo.getAbsolutePath());
                } catch (LsException e) {
                    return false;
                }

                // 第二次执行删除。但是还有子集目录。
                if (ftpFileBo.getResid() > 1) {
                    return false;
                }

                res = deleteFileRetainDirectory_(rlist);
                if (res == false) {
                    return false;
                }

                if (rlist.isEmpty()) {
                    if (rmdir(ftpFileBo.getAbsolutePath()) == false) {
                        return false;
                    }
                } else {
                    ftpFileBo.setResid(ftpFileBo.getResid() + 1);
                    st.push(ftpFileBo);
                    st.addAll(rlist);
                }
            } else {
                if (rm(ftpFileBo.getAbsolutePath()) == false) {
                    return false;
                }
            }
        }

        return true;
    }

    /**
     * @see io.github.ludongrong.netftp.IFtper#delete(java.lang.String, java.lang.String)
     */
    @Override
    public boolean delete(String dst, String fname) {

        PathHelper.checkfname(fname);

        List<FtperFile> flist = listFile(dst);
        if (flist == null) {
            LogHelper.getLog().error(">>>eee {} >>> Unable to find file. {}", getHost(),
                FtperFile.getAbsolutePath(dst, fname));
            return Boolean.FALSE;
        }

        FtperFile matchFile = FtperFile.matchSelector(flist, fname);
        if (matchFile == null) {
            LogHelper.getLog().error(">>>eee {} >>> Unable to delete the target file. {}", getHost(),
                FtperFile.getAbsolutePath(dst, fname));
            return true;
        }

        return delete(matchFile);
    }

    /**
     * 上传文件.
     * 
     * @param dst
     *            目录路径
     * @param fname
     *            文件名
     * @param is
     *            输入流
     * @return 操作结果。true表示成功；false表示失败
     */
    protected abstract boolean put(String dst, String fname, InputStream is);

    /**
     * @see io.github.ludongrong.netftp.IFtper#upload(java.lang.String, java.lang.String, java.io.InputStream)
     */
    @Override
    public boolean upload(String dst, String fname, InputStream is) {

        PathHelper.check(dst);

        boolean res = listFunc(dst, new Proccesser<Boolean>() {

            boolean result = false;

            @Override
            public boolean pre(String src, String fname, boolean exists) {
                if (exists == false) {
                    String dirp = FtperFile.getAbsolutePath(src, fname);
                    exists = mkdir(dirp);
                    if (exists == false) {
                        LogHelper.getLog().error(">>>eee {} >>> Unable to create directory. {}", getHost(), dirp);
                    }
                }
                return exists;
            }

            @Override
            public void last(List<FtperFile> ftpFiles) {
                FtperFile matchFile = FtperFile.matchSelector(ftpFiles, fname);
                if (matchFile != null) {
                    result = delete(matchFile);
                    if (result == false) {
                        LogHelper.getLog().error(">>>eee {} >>> Unable to delete the target file. {}", getHost(),
                            matchFile.getAbsolutePath());
                    }
                } else {
                    result = true;
                }
            }

            @Override
            public Boolean result() {
                return result;
            }
        }).result();

        if (res == false) {
            return false;
        }

        return put(dst, fname, is);
    }

    /**
     * 下载文件.
     * 
     * @param matchFile
     *            文件
     * @param os
     *            输出流
     * @return 操作结果。true表示成功；false表示失败
     */
    protected abstract boolean get(FtperFile matchFile, OutputStream os);

    /**
     * @see io.github.ludongrong.netftp.IFtper#down(java.lang.String, java.lang.String, java.io.OutputStream)
     */
    @Override
    public boolean down(String dst, String fname, OutputStream os) {

        List<FtperFile> flist = listFile(dst);
        if (flist == null) {
            LogHelper.getLog().error(">>>eee {} >>> Unable to find file. {}", getHost(),
                FtperFile.getAbsolutePath(dst, fname));
            return Boolean.FALSE;
        }

        PathHelper.checkfname(fname);

        FtperFile matchFile = FtperFile.matchSelector(flist, fname);
        if (existsFile(matchFile) == false) {
            LogHelper.getLog().error(">>>eee {} >>> The destination file could not be found. {}", getHost(),
                FtperFile.getAbsolutePath(dst, fname));
            return false;
        }

        return get(matchFile, os);
    }

    /**
     * 移动文件.
     * 
     * @param matchFile
     *            文件
     * @param dst
     *            目录路径
     * @param dname
     *            文件名
     * @return 操作结果。true表示成功；false表示失败
     */
    protected abstract boolean rename(FtperFile matchFile, String dst, String dname);

    /**
     * @see io.github.ludongrong.netftp.IFtper#move(java.lang.String, java.lang.String, java.lang.String,
     *      java.lang.String)
     */
    @Override
    public boolean move(String src, String sname, String dst, String dname) {

        List<FtperFile> slist = listFile(src);
        if (slist == null) {
            LogHelper.getLog().error(">>>eee {} >>> Unable to find file. {}", getHost(),
                FtperFile.getAbsolutePath(src, sname));
            return Boolean.FALSE;
        }

        List<FtperFile> dlist = listFile(dst);
        if (dlist == null) {
            LogHelper.getLog().error(">>>eee {} >>> Unable to find file. {}", getHost(),
                FtperFile.getAbsolutePath(dst, dname));
            return Boolean.FALSE;
        }

        PathHelper.checkfname(sname);
        PathHelper.checkfname(dname);

        if (src.equalsIgnoreCase(dst)) {
            if (sname.equalsIgnoreCase(dname)) {
                LogHelper.getLog().error(">>>eee {} >>> The source path and the target path must be different. {}",
                    getHost(), FtperFile.getAbsolutePath(src, sname));
                return true;
            }
        }

        FtperFile matchFile = FtperFile.matchSelector(dlist, dname);
        if (exists(matchFile)) {
            boolean res = delete(matchFile);
            if (res == false) {
                LogHelper.getLog().error(">>>eee {} >>> The destination file could not be deleted. {}", getHost(),
                    FtperFile.getAbsolutePath(dst, dname));
                return res;
            }
        }

        matchFile = FtperFile.matchSelector(slist, sname);
        if (exists(matchFile) == false) {
            LogHelper.getLog().error(">>>eee {} >>> The source file does not exist. {}", getHost(),
                FtperFile.getAbsolutePath(src, sname));
            return false;
        }

        return rename(matchFile, dst, dname);
    }

    /**
     * @see io.github.ludongrong.netftp.IFtper#getHost()
     */
    @Override
    public String getHost() {
        return ftperConfig.getHost();
    }

    /**
     * @see io.github.ludongrong.netftp.IFtper#getPort()
     */
    @Override
    public int getPort() {
        return ftperConfig.getPort();
    }

    /**
     * @see io.github.ludongrong.netftp.IFtper#getUsername()
     */
    @Override
    public String getUsername() {
        return ftperConfig.getUsername();
    }
}