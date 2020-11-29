package io.github.ludongrong.netftp;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

import cn.hutool.core.util.StrUtil;
import io.github.ludongrong.netftp.jsch.LSES;
import io.github.ludongrong.netftp.util.PathHelper;
import lombok.Getter;
import lombok.Setter;

/**
 * sftp 协议 jsch 实现版.
 *
 * @author <a href="mailto:736779458@qq.com">ludongrong</a>
 * @since 2020-11-27
 */
public class JschFtper extends AbstsactFtper {

    @Getter
    @Setter
    private ChannelSftp channelSftp;

    /**
     * 构造.
     *
     * @param channelSftp
     *            客户端
     * @param ftperConfig
     *            配置
     */
    public JschFtper(ChannelSftp channelSftp, FtperConfig ftperConfig) {
        super();
        this.channelSftp = channelSftp;
        this.ftperConfig = ftperConfig;
    }

    /**
     * @inheritDoc
     */
    protected List<FtperFile> ls(String dst) throws LsException {

        LSES lses = new LSES();

        return ls(dst, lses);
    }

    /**
     * 获取文件列表.
     *
     * @param dst
     *            目录路径
     * @param lses
     *            列表抽象
     * @return 文件列表
     * @throws LsException
     *             获取异常
     */
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
     * @see io.github.ludongrong.netftp.AbstsactFtper#listFunc(java.lang.String, io.github.ludongrong.netftp.Proccesser)
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
     * @see io.github.ludongrong.netftp.AbstsactFtper#changeWorkingDirectory(java.lang.String)
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
     * @see io.github.ludongrong.netftp.AbstsactFtper#mkdir(java.lang.String)
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
     * @see io.github.ludongrong.netftp.AbstsactFtper#rmdir(java.lang.String)
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
     * @see io.github.ludongrong.netftp.AbstsactFtper#rm(java.lang.String)
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
     * @see io.github.ludongrong.netftp.AbstsactFtper#put(java.lang.String, java.lang.String, java.io.InputStream)
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
     * @see io.github.ludongrong.netftp.AbstsactFtper#get(io.github.ludongrong.netftp.FtperFile, java.io.OutputStream)
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
     * @see io.github.ludongrong.netftp.AbstsactFtper#rename(io.github.ludongrong.netftp.FtperFile, java.lang.String, java.lang.String)
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
     * @see io.github.ludongrong.netftp.IFtper#close()
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
     * 关闭连接.
     *
     * @param session
     *            ssh会话
     */
    private static void close(Session session) {
        if (session != null) {
            session.disconnect();
        }
        session = null;
    }

    /**
     * 构建者.
     *
     * @author <a href="mailto:736779458@qq.com">ludongrong</a>
     * @since 2020-11-27
     */
    public static class Builder {

        /**
         * 构建.
         *
         * @param ftperConfig
         *            配置
         * @return 客户端
         */
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

    /**
     * @see io.github.ludongrong.netftp.IFtper#cloneFtper()
     */
    @Override
    public IFtper cloneFtper() {
        return new Builder().build(ftperConfig);
    }
}
