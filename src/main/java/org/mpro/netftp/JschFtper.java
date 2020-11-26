package org.mpro.netftp;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.mpro.netftp.jsch.LSES;
import org.mpro.netftp.util.PathHelper;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

import cn.hutool.core.util.StrUtil;
import lombok.Getter;
import lombok.Setter;

/**
 * SFtp 协议 jsch 实现版
 * 
 * @author 736779458@qq.com
 * @version 1.0
 * @updated 11-十一月-2020 7:26:36
 */
public class JschFtper extends AbstsactFtper {

    @Getter
    @Setter
    private ChannelSftp channelSftp;

    /**
     * 根据配置构造 JschFtper
     * 
     * @param channelSftp
     * @param host
     */
    public JschFtper(ChannelSftp channelSftp, FtperConfig ftperConfig) {
        super();
        this.channelSftp = channelSftp;
        this.ftperConfig = ftperConfig;
    }

    /**
     * 获取文件列表
     * 
     * @param dst
     * @exception LsException
     */
    protected List<FtperFile> ls(String dst) throws LsException {

        LSES lses = new LSES();

        return ls(dst, lses);
    }

    private List<FtperFile> ls(String dst, LSES lses) throws LsException {

        try {
            channelSftp.ls(dst, lses);
        } catch (SftpException e) {
            throw new LsException("", e);
        }

        List<FtperFile> rlist = new ArrayList<FtperFile>();
        lses.getFiles().stream().forEach(obj -> {
            obj.setDirPath(dst);
            obj.setExists(true);
            rlist.add(obj);
        });

        return rlist;
    }

    /**
     * 按路径层级一层一层进入并获取最终文件路径的文件列表
     * 
     * @param dst
     * @param proccesser
     *            proccesser
     */
    protected <T extends Proccesser<?>> T listFunc(String dst, T proccesser) {

        String[] items = PathHelper.split(dst);

        String dir = StrUtil.SLASH;

        LSES lses = new LSES();
        try {
            ls(dir, lses);
        } catch (LsException e) {
            return proccesser;
        }

        int i = 1;
        for (; i < items.length; i++) {

            String fname = items[i];

            FtperFile matchFile = FtperFile.matchSelector(lses.getFiles(), fname);
            lses.clear();

            boolean edir;

            if (matchFile != null) {
                if (matchFile.isDirectory() == false) {
                    break;
                }
                edir = true;
            } else {
                edir = false;
            }

            boolean go = proccesser.pre(dir, fname, edir);
            if (go == false) {
                break;
            }

            if (edir) {
                try {
                    ls(matchFile.getAbsolutePath(), lses);
                } catch (LsException e) {
                    break;
                }
            }

            dir = FtperFile.getAbsolutePath(dir, fname);
        }

        if (i == items.length) {
            lses.getFiles().stream().forEach(obj -> {
                obj.setDirPath(dst);
                obj.setExists(true);
            });
            proccesser.last(lses.getFiles());
        }

        return proccesser;
    }

    /**
     * 切换工作目录
     * 
     * @param fname
     */
    @Override
    protected boolean changeWorkingDirectory(String fname) {
        try {
            channelSftp.cd(fname);
            return true;
        } catch (SftpException e) {
            return false;
        }
    }

    /**
     * 创建目录
     * 
     * @param fname
     */
    @Override
    protected boolean mkdir(String fname) {
        try {
            channelSftp.mkdir(fname);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 删除目录
     * 
     * @param fname
     */
    protected boolean rmdir(String fname) {
        try {
            channelSftp.rmdir(fname);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 删除文件
     * 
     * @param fname
     */
    protected boolean rm(String fname) {
        try {
            channelSftp.rm(fname);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 上传文件
     * 
     * @param dst
     * @param fname
     * @param is
     */
    @Override
    protected boolean put(String dst, String fname, InputStream is) {

        String pname = fname + ".part";

        try {
            channelSftp.put(is, pname);
            channelSftp.rename(pname, fname);
            return true;
        } catch (SftpException e) {
            return false;
        }
    }

    /**
     * 下载文件
     * 
     * @param matchFile
     * @param os
     */
    @Override
    protected boolean get(FtperFile matchFile, OutputStream os) {

        try {
            channelSftp.get(matchFile.getAbsolutePath(), os);
            return true;
        } catch (SftpException e) {
            return false;
        }
    }

    /**
     * 移动文件
     * 
     * @param matchFile
     * @param dst
     * @param dname
     */
    @Override
    protected boolean rename(FtperFile matchFile, String dst, String dname) {

        String dstf = FtperFile.getAbsolutePath(dst, dname);

        try {
            channelSftp.rename(matchFile.getAbsolutePath(), dstf);
            return true;
        } catch (SftpException e) {
            return false;
        }
    }

    /**
     * 关闭ftp连接
     */
    @Override
    public void close() {
        if (channelSftp == null) {
            return;
        }

        try {
            close(channelSftp.getSession());
        } catch (JSchException e) {
            // logger.debug("exception close session", e);
        }
    }

    /**
     * 关闭ftp连接
     * 
     * @param session
     *            ChannelSftp 会话
     */
    private static void close(Session session) {
        if (session != null) {
            session.disconnect();
        }
        session = null;
    }

    public static class Builder {

        public JschFtper build(FtperConfig ftperConfig) {

            Properties conf = new Properties();
            conf.put("StrictHostKeyChecking", "no");

            Session session = null;

            JSch jsch = new JSch();
            try {
                session = jsch.getSession(ftperConfig.getUsername(), ftperConfig.getHost(), ftperConfig.getPort());
                session.disconnect();
                session.setPassword(ftperConfig.getPassword());
                session.setConfig(conf);
                session.setTimeout(ftperConfig.getTimeout() * 1000);
                session.connect();

                ChannelSftp channelSftp = (ChannelSftp)session.openChannel("sftp");
                channelSftp.connect();

                return new JschFtper(channelSftp, ftperConfig);
            } catch (JSchException e) {
                if (null != session) {
                    session.disconnect();
                }
                throw new IllegalArgumentException(
                    "FTP host[" + ftperConfig.getHost() + "] user[" + ftperConfig.getUsername() + "] ", e);
            }
        }
    }

    @Override
    public IFtper cloneFtper() {
        return new Builder().build(ftperConfig);
    }

}
