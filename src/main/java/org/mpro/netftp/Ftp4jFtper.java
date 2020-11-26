package org.mpro.netftp;

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

import org.mpro.netftp.util.PathHelper;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import it.sauronsoftware.ftp4j.FTPAbortedException;
import it.sauronsoftware.ftp4j.FTPClient;
import it.sauronsoftware.ftp4j.FTPDataTransferException;
import it.sauronsoftware.ftp4j.FTPException;
import it.sauronsoftware.ftp4j.FTPIllegalReplyException;
import it.sauronsoftware.ftp4j.FTPListParseException;
import lombok.Getter;
import lombok.Setter;

/**
 * Ftp 协议 ftp4j 实现版
 * 
 * @author 736779458@qq.com
 * @version 1.0
 * @updated 11-十一月-2020 7:26:39
 */
public class Ftp4jFtper extends AbstsactFtper {

    @Getter
    @Setter
    private FTPClient ftpClient;

    /**
     * 根据配置构造 Ftp4jFtper
     * 
     * @param ftpClient
     * @param host
     */
    public Ftp4jFtper(FTPClient ftpClient, FtperConfig ftperConfig) {
        super();
        this.ftpClient = ftpClient;
        this.ftperConfig = ftperConfig;
    }

    /**
     * 获取文件列表
     * 
     * @param dst
     * @exception LsException
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
     * 按路径层级一层一层进入并获取最终文件路径的文件列表
     * 
     * @param dst
     * @param proccesser
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
     * 切换工作目录
     * 
     * @param fname
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
     * 创建目录
     * 
     * @param fname
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
     * 删除目录
     * 
     * @param fname
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
     * 删除文件
     * 
     * @param fname
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
     * 下载文件
     * 
     * @param matchFile
     * @param os
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
            ftpClient.rename(matchFile.getAbsolutePath(), dstf);
            return true;
        } catch (IllegalStateException | IOException | FTPIllegalReplyException | FTPException e) {
            return false;
        }
    }

    /**
     * 关闭ftp连接
     */
    public void close() {

        close(ftpClient);
    }

    /**
     * 关闭ftp连接
     * 
     * @param ftpClient
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

    public static class Builder {

        private int fileType = FTPClient.TYPE_BINARY;

        /**
         * client.setType(FTPClient.TYPE_TEXTUAL);
         * client.setType(FTPClient.TYPE_BINARY);
         * client.setType(FTPClient.TYPE_AUTO);
         */
        public Builder withFileType(int fileType) {
            this.fileType = fileType;
            return this;
        }

        private FTPClient createFtpClient(FtperConfig ftperConfig) {

            FTPClient client = new FTPClient();
            client.setSecurity(FTPClient.SECURITY_FTP);
            return client;
        }

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

    @Override
    public IFtper cloneFtper() {
        return new Builder().build(ftperConfig);
    }

}