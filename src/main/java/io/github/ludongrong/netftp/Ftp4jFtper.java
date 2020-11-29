package io.github.ludongrong.netftp;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import io.github.ludongrong.netftp.util.PathHelper;
import it.sauronsoftware.ftp4j.FTPAbortedException;
import it.sauronsoftware.ftp4j.FTPClient;
import it.sauronsoftware.ftp4j.FTPDataTransferException;
import it.sauronsoftware.ftp4j.FTPException;
import it.sauronsoftware.ftp4j.FTPIllegalReplyException;
import it.sauronsoftware.ftp4j.FTPListParseException;
import lombok.Getter;
import lombok.Setter;

/**
 * Ftp 协议 ftp4j 实现版.
 *
 * @author <a href="mailto:736779458@qq.com">ludongrong</a>
 * @since 2020-11-27
 */
public class Ftp4jFtper extends AbstsactFtper {

    /** 客户端 */
    @Getter
    @Setter
    private FTPClient ftpClient;

    /**
     * 构造.
     *
     * @param ftpClient
     *            客户端
     * @param ftperConfig
     *            配置
     */
    public Ftp4jFtper(FTPClient ftpClient, FtperConfig ftperConfig) {
        super();
        this.ftpClient = ftpClient;
        this.ftperConfig = ftperConfig;
    }

    /**
     * @see io.github.ludongrong.netftp.AbstsactFtper#ls(java.lang.String)
     */
    @Override
    protected List<FtperFile> ls(String dst) throws LsException {

        List<FtperFile> reslist = new ArrayList<FtperFile>();

        try {
            it.sauronsoftware.ftp4j.FTPFile[] ftpFiles = ftpClient.list(dst);
            for (it.sauronsoftware.ftp4j.FTPFile ftpFile : ftpFiles) {
                Calendar calendar = Calendar.getInstance();
                if (ftpFile.getModifiedDate() != null) {
                    calendar.setTime(ftpFile.getModifiedDate());
                }

                FtperFile resfile = new FtperFile();
                resfile.setName(ftpFile.getName());
                resfile.setType(ftpFile.getType() == it.sauronsoftware.ftp4j.FTPFile.TYPE_DIRECTORY ? 1 : 0);
                resfile.setSize(ftpFile.getSize());
                resfile.setTimestamp(calendar);
                resfile.setDirPath(dst);
                resfile.setExists(true);
                reslist.add(resfile);
            }
        } catch (IllegalStateException e) {
            throw new LsException("", e);
        } catch (IOException e) {
            throw new LsException("", e);
        } catch (FTPIllegalReplyException e) {
            throw new LsException("", e);
        } catch (FTPException e) {
            throw new LsException("", e);
        } catch (FTPDataTransferException | FTPAbortedException | FTPListParseException e) {
            throw new LsException("", e);
        }

        return reslist;
    }

    /**
     * @see io.github.ludongrong.netftp.AbstsactFtper#listFunc(java.lang.String, io.github.ludongrong.netftp.Proccesser)
     */
    @Override
    protected <T extends Proccesser<?>> T listFunc(String dst, T proccesser) {

        String[] items = PathHelper.split(dst);

        String dir = StrUtil.SLASH;

        List<FtperFile> ftpFiles = new ArrayList<FtperFile>();
        try {
            ftpFiles.addAll(ls(StrUtil.SLASH));
        } catch (LsException e) {
            return proccesser;
        }

        int i = 1;
        for (; i < items.length; i++) {

            String fname = items[i];

            FtperFile matchFile = FtperFile.matchSelector(ftpFiles, fname);
            ftpFiles.clear();

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
                    ftpFiles.addAll(ls(matchFile.getAbsolutePath()));
                } catch (LsException e) {
                    break;
                }
            }

            dir = FtperFile.getAbsolutePath(dir, fname);
        }

        if (i == items.length) {
            ftpFiles.stream().forEach(obj -> {
                obj.setDirPath(dst);
                obj.setExists(true);
            });

            proccesser.last(ftpFiles);
        }

        return proccesser;
    }

    /**
     * @see io.github.ludongrong.netftp.AbstsactFtper#changeWorkingDirectory(java.lang.String)
     */
    @Override
    protected boolean changeWorkingDirectory(String fname) {
        try {
            ftpClient.changeDirectory(fname);
            return true;
        } catch (IllegalStateException | IOException | FTPIllegalReplyException | FTPException e) {
            return false;
        }
    }

    /**
     * @see io.github.ludongrong.netftp.AbstsactFtper#mkdir(java.lang.String)
     */
    protected boolean mkdir(String fname) {
        try {
            ftpClient.createDirectory(fname);
            return true;
        } catch (IllegalStateException | IOException | FTPIllegalReplyException | FTPException e) {
            return false;
        }
    }

    /**
     * @see io.github.ludongrong.netftp.AbstsactFtper#rmdir(java.lang.String)
     */
    @Override
    protected boolean rmdir(String fname) {
        try {
            ftpClient.deleteDirectory(fname);
            return true;
        } catch (IllegalStateException | IOException | FTPIllegalReplyException | FTPException e) {
            return false;
        }
    }

    /**
     * @see io.github.ludongrong.netftp.AbstsactFtper#rm(java.lang.String)
     */
    @Override
    protected boolean rm(String fname) {
        try {
            ftpClient.deleteFile(fname);
            return true;
        } catch (IllegalStateException | IOException | FTPIllegalReplyException | FTPException e) {
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
            ftpClient.upload(pname, is, 0, 0, null);
            ftpClient.rename(pname, fname);
            return true;
        } catch (IllegalStateException | IOException | FTPIllegalReplyException | FTPException e) {
            return false;
        } catch (FTPDataTransferException e) {
            return false;
        } catch (FTPAbortedException e) {
            return false;
        }
    }

    /**
     * @see io.github.ludongrong.netftp.AbstsactFtper#get(io.github.ludongrong.netftp.FtperFile, java.io.OutputStream)
     */
    @Override
    protected boolean get(FtperFile matchFile, OutputStream os) {

        try {
            ftpClient.download(matchFile.getAbsolutePath(), os, 0, null);
            return true;
        } catch (IllegalStateException | IOException | FTPIllegalReplyException | FTPException
            | FTPDataTransferException | FTPAbortedException e) {
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
            ftpClient.rename(matchFile.getAbsolutePath(), dstf);
            return true;
        } catch (IllegalStateException | IOException | FTPIllegalReplyException | FTPException e) {
            return false;
        }
    }

    /**
     * @see io.github.ludongrong.netftp.IFtper#close()
     */
    public void close() {

        close(ftpClient);
    }

    /**
     * 关闭连接.
     *
     * @param ftpClient
     *            客户端
     */
    private static void close(FTPClient ftpClient) {

        if (ObjectUtil.isNull(ftpClient)) {
            return;
        }

        try {
            ftpClient.disconnect(true);
        } catch (IllegalStateException | IOException | FTPIllegalReplyException | FTPException e) {
            e.printStackTrace();
        }
    }

    /**
     * 构建者.
     *
     * @author <a href="mailto:736779458@qq.com">ludongrong</a>
     * @since 2020-11-27
     */
    public static class Builder {

        /** 文件传输类型 */
        private int fileType = FTPClient.TYPE_BINARY;

        /**
         * 设置文件传输类型.
         * 
         * <p>
         * client.setType(FTPClient.TYPE_TEXTUAL);
         * client.setType(FTPClient.TYPE_BINARY);
         * client.setType(FTPClient.TYPE_AUTO);
         *
         * @param fileType
         *            文件传输类型
         * @return 构建者
         */
        public Builder withFileType(int fileType) {
            this.fileType = fileType;
            return this;
        }

        /**
         * 创建 ftp 客户端.
         *
         * @param ftperConfig
         *            配置
         * @return ftp 客户端
         */
        private FTPClient createFtpClient(FtperConfig ftperConfig) {

            FTPClient client = new FTPClient();
            client.setSecurity(FTPClient.SECURITY_FTP);
            return client;
        }

        /**
         * 创建 ftps 客户端.
         *
         * @param ftperConfig
         *            配置
         * @return ftps 客户端
         */
        private FTPClient createFtpsClient(FtperConfig ftperConfig) {

            TrustManager[] trustManager = new TrustManager[] {new X509TrustManager() {
                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }

                public void checkClientTrusted(X509Certificate[] certs, String authType) {}

                public void checkServerTrusted(X509Certificate[] certs, String authType) {}
            }};
            SSLContext sslContext = null;
            try {
                sslContext = SSLContext.getInstance("SSL");
                sslContext.init(null, trustManager, new SecureRandom());
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            } catch (KeyManagementException e) {
                e.printStackTrace();
            }
            SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

            FTPClient client = new FTPClient();
            client.setSSLSocketFactory(sslSocketFactory);
            client.setSecurity(FTPClient.SECURITY_FTPS);

            return client;
        }

        /**
         * 创建.
         *
         * @param ftperConfig
         *            配置
         * @return 客户端
         */
        public Ftp4jFtper build(FtperConfig ftperConfig) {

            FTPClient ftpClient = null;

            switch (ftperConfig.getProtocol()) {
                case ftp:
                    ftpClient = createFtpClient(ftperConfig);
                    break;
                case ftps:
                    ftpClient = createFtpsClient(ftperConfig);
                    break;
                default:
                    throw new IllegalStateException("Unsupported protocol");
            }

            try {
                ftpClient.connect(ftperConfig.getHost(), ftperConfig.getPort());
            } catch (IllegalStateException | IOException | FTPIllegalReplyException | FTPException e) {
                throw new IllegalArgumentException(tip(ftperConfig) + " transfer mode exception !", e);
            }

            try {
                ftpClient.login(ftperConfig.getUsername(), ftperConfig.getPassword());
            } catch (IllegalStateException | IOException | FTPIllegalReplyException | FTPException e) {
                throw new IllegalArgumentException(tip(ftperConfig) + " transfer mode exception !", e);
            }

            // 模式
            ftpClient.setPassive(ftperConfig.isPasvMode());

            // 文件类型
            ftpClient.setType(fileType);

            ftpClient.setAutoNoopTimeout(30000);
            ftpClient.getConnector().setReadTimeout(ftperConfig.getTimeout() * 1000);

            return new Ftp4jFtper(ftpClient, ftperConfig);
        }

        private String tip(FtperConfig ftperConfig) {
            return "FTP host[" + ftperConfig.getHost() + "] user[" + ftperConfig.getUsername() + "] ";
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