package org.mpro.netftp;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPConnectionClosedException;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.commons.net.ftp.FTPSClient;
import org.apache.commons.net.ftp.parser.ParserInitializationException;
import org.apache.commons.net.util.TrustManagerUtils;
import org.mpro.netftp.util.PathHelper;

import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import lombok.Getter;
import lombok.Setter;

/**
 * Ftp 协议 apache common 实现版
 * 
 * @author 736779458@qq.com
 * @version 1.0
 * @updated 25-十一月-2020 15:08:11
 */
public class ApacheFtper extends AbstsactFtper {

    /**
     * ftp 客户端
     */
    @Getter
    @Setter
    private FTPClient ftpClient;

    /**
     * 根据配置构造 ApacheFtper
     * 
     * @param ftpClient
     *            ftp 客户端
     * @param ftperConfig
     *            ftp 配置
     */
    public ApacheFtper(FTPClient ftpClient, FtperConfig ftperConfig) {
        super();
        this.ftpClient = ftpClient;
        this.ftperConfig = ftperConfig;
    }

    /**
     * 获取文件列表
     * 
     * @param dst
     *            目录路径
     * @exception LsException
     *                异常
     */
    @Override
    protected List<FtperFile> ls(String dst) throws LsException {

        List<FtperFile> flist = new ArrayList<FtperFile>();

        String fnameEncoding;
        try {
            fnameEncoding = new String(dst.getBytes(), FTP.DEFAULT_CONTROL_ENCODING);
        } catch (UnsupportedEncodingException e1) {
            return flist;
        }

        try {
            FTPFile[] ftpFiles = ftpClient.listFiles(fnameEncoding);

            for (FTPFile ftpFile : ftpFiles) {
                if (StrUtil.isEmpty(ftpFile.getName())) {
                    continue;
                }

                byte[] buf = ftpFile.getName().getBytes(FTP.DEFAULT_CONTROL_ENCODING);
                String name = new String(buf, CharsetUtil.UTF_8);

                FtperFile resfile = new FtperFile();
                resfile.setName(name);
                resfile.setType(ftpFile.getType() == it.sauronsoftware.ftp4j.FTPFile.TYPE_DIRECTORY ? 1 : 0);
                resfile.setSize(ftpFile.getSize());
                resfile.setTimestamp(ftpFile.getTimestamp());
                resfile.setDirPath(dst);
                resfile.setExists(true);
                flist.add(resfile);
            }
        } catch (UnsupportedEncodingException e) {
            throw new LsException("", e);
        } catch (FTPConnectionClosedException e) {
            throw new LsException("", e);
        } catch (IOException e) {
            throw new LsException("", e);
        } catch (ParserInitializationException e) {
            throw new LsException("", e);
        }

        return flist;
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
     * 获取指定路径下的文件列表
     * 
     * @param dst
     *            目录路径
     */
    @Override
    public List<FtperFile> listFile(String dst) {

        PathHelper.check(dst);

        try {
            return ls(dst);
        } catch (LsException e) {
            return null;
        }
    }

    /**
     * 切换工作目录
     * 
     * @param dst
     *            目录路径
     */
    @Override
    protected boolean changeWorkingDirectory(String dst) {
        String fnameEncoding;
        try {
            fnameEncoding = new String(dst.getBytes(), FTP.DEFAULT_CONTROL_ENCODING);
        } catch (UnsupportedEncodingException e1) {
            return false;
        }

        try {
            return ftpClient.changeWorkingDirectory(fnameEncoding);
        } catch (FTPConnectionClosedException e) {
            return false;
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * 创建目录
     * 
     * @param fname
     *            目录路径
     */
    @Override
    protected boolean mkdir(String fname) {
        String fnameEncoding;
        try {
            fnameEncoding = new String(fname.getBytes(), FTP.DEFAULT_CONTROL_ENCODING);
        } catch (UnsupportedEncodingException e1) {
            return false;
        }

        try {
            return ftpClient.makeDirectory(fnameEncoding);
        } catch (FTPConnectionClosedException e) {
            return false;
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * 删除目录
     * 
     * @param fname
     *            目录路径
     */
    @Override
    protected boolean rmdir(String fname) {
        String fnameEncoding;
        try {
            fnameEncoding = new String(fname.getBytes(), FTP.DEFAULT_CONTROL_ENCODING);
        } catch (UnsupportedEncodingException e1) {
            return false;
        }

        try {
            return ftpClient.removeDirectory(fnameEncoding);
        } catch (FTPConnectionClosedException e) {
            return false;
        } catch (IOException e) {
            return false;
        } catch (ParserInitializationException e) {
            return false;
        }
    }

    /**
     * 删除文件
     * 
     * @param fname
     *            目录路径
     */
    @Override
    protected boolean rm(String fname) {
        String fnameEncoding;
        try {
            fnameEncoding = new String(fname.getBytes(), FTP.DEFAULT_CONTROL_ENCODING);
        } catch (UnsupportedEncodingException e1) {
            return false;
        }

        try {
            return ftpClient.deleteFile(fnameEncoding);
        } catch (FTPConnectionClosedException e) {
            return false;
        } catch (IOException e) {
            return false;
        } catch (ParserInitializationException e) {
            return false;
        }
    }

    /**
     * 上传文件
     * 
     * @param dst
     *            目录路径
     * @param fname
     *            文件名
     * @param is
     *            输入流
     */
    @Override
    protected boolean put(String dst, String fname, InputStream is) {

        String pname = fname + ".part";

        String fnameEncoding;
        try {
            fnameEncoding = new String(FtperFile.getAbsolutePath(dst, fname).getBytes(), FTP.DEFAULT_CONTROL_ENCODING);
        } catch (UnsupportedEncodingException e) {
            return false;
        }

        String pnameEncoding;
        try {
            pnameEncoding = new String(FtperFile.getAbsolutePath(dst, pname).getBytes(), FTP.DEFAULT_CONTROL_ENCODING);
        } catch (UnsupportedEncodingException e1) {
            return false;
        }

        try {
            boolean res = ftpClient.storeFile(pnameEncoding, is);
            if (res) {
                res = ftpClient.rename(pnameEncoding, fnameEncoding);
            }
            return res;
        } catch (FTPConnectionClosedException e) {
            return false;
        } catch (IOException e) {
            return false;
        } catch (ParserInitializationException e) {
            return false;
        }
    }

    /**
     * 下载文件
     * 
     * @param matchFile
     *            文件
     * @param os
     *            输出流
     */
    @Override
    protected boolean get(FtperFile matchFile, OutputStream os) {

        String srcEncoding;
        try {
            srcEncoding = new String(matchFile.getAbsolutePath().getBytes(), FTP.DEFAULT_CONTROL_ENCODING);
        } catch (UnsupportedEncodingException e) {
            return false;
        }

        try {
            return ftpClient.retrieveFile(srcEncoding, os);
        } catch (FTPConnectionClosedException e) {
            return false;
        } catch (IOException e) {
            return false;
        } catch (ParserInitializationException e) {
            return false;
        }
    }

    /**
     * 移动文件
     * 
     * @param matchFile
     *            文件
     * @param dst
     *            目录路径
     * @param dname
     *            文件名
     */
    @Override
    protected boolean rename(FtperFile matchFile, String dst, String dname) {

        String srcEncoding;
        try {
            srcEncoding = new String(matchFile.getAbsolutePath().getBytes(), FTP.DEFAULT_CONTROL_ENCODING);
        } catch (UnsupportedEncodingException e) {
            return false;
        }

        String dstEncoding;
        try {
            dstEncoding = new String(FtperFile.getAbsolutePath(dst, dname).getBytes(), FTP.DEFAULT_CONTROL_ENCODING);
        } catch (UnsupportedEncodingException e) {
            return false;
        }

        try {
            return ftpClient.rename(srcEncoding, dstEncoding);
        } catch (FTPConnectionClosedException e) {
            return false;
        } catch (IOException e) {
            return false;
        } catch (ParserInitializationException e) {
            return false;
        }
    }

    /**
     * 关闭ftp连接
     */
    @Override
    public void close() {

        close(ftpClient);
    }

    /**
     * 关闭ftp连接
     * 
     * @param ftpClient
     *            ftp 客户端
     */
    private static void close(FTPClient ftpClient) {

        if (ObjectUtil.isNull(ftpClient)) {
            return;
        }

        try {
            ftpClient.logout();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            ftpClient.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static class Builder {

        private int fileType = FTP.BINARY_FILE_TYPE;

        /**
         * 设置文件类型
         * 
         * @param fileType
         */
        public Builder withFileType(int fileType) {
            this.fileType = fileType;
            return this;
        }

        /**
         * 设置文件类型
         * 
         * @param ftpClient
         *            ftp 客户端
         * @param ftperConfig
         *            ftp 配置
         */
        private void setFileType(FTPClient ftpClient, FtperConfig ftperConfig) {
            try {
                boolean res = ftpClient.setFileType(fileType);
                if (!res) {
                    close(ftpClient);
                    throw new IllegalArgumentException(tip(ftperConfig) + " set binary fail !");
                }
            } catch (Exception e) {
                close(ftpClient);
                throw new IllegalArgumentException(tip(ftperConfig) + " set binary exception !", e);
            }
        }

        /**
         * 设置传输模式
         * 
         * @param ftpClient
         *            ftp 客户端
         * @param ftperConfig
         *            ftp 配置
         */
        private void setFileTransferMode(FTPClient ftpClient, FtperConfig ftperConfig) {
            try {
                boolean transferModel = ftpClient.setFileTransferMode(FTPClient.STREAM_TRANSFER_MODE);
                if (!transferModel) {
                    close(ftpClient);
                    throw new IllegalArgumentException(tip(ftperConfig) + " transfer mode fail !");
                }
            } catch (Exception e) {
                close(ftpClient);
                throw new IllegalArgumentException(tip(ftperConfig) + " transfer mode exception !", e);
            }
        }

        /**
         * 创建 ftp
         * 
         * @param ftperConfig
         */
        private FTPClient createFtpClient(FtperConfig ftperConfig) {

            FTPClient ftpClient = new FTPClient();

            try {
                ftpClient.connect(ftperConfig.getHost(), ftperConfig.getPort());
            } catch (Exception e) {
                throw new IllegalStateException(tip(ftperConfig) + " connect fail.", e);
            }

            // 未连接
            if (FTPReply.isPositiveCompletion(ftpClient.getReplyCode()) == false) {
                close(ftpClient);
                throw new IllegalStateException(tip(ftperConfig) + "Not Connect");
            }

            boolean login;
            try {
                login = ftpClient.login(ftperConfig.getUsername(), ftperConfig.getPassword());
            } catch (Exception e) {
                close(ftpClient);
                throw new IllegalStateException(tip(ftperConfig) + " login exception !", e);
            }

            if (!login) {
                close(ftpClient);
                throw new IllegalStateException(tip(ftperConfig) + " login fail !");
            }

            return ftpClient;
        }

        /**
         * 创建 ftps
         * 
         * @param ftperConfig
         */
        private FTPSClient createFtpsClient(FtperConfig ftperConfig) {

            FTPSClient ftpsClient = new FTPSClient(true);
            ftpsClient.setTrustManager(TrustManagerUtils.getAcceptAllTrustManager());

            try {
                ftpsClient.connect(ftperConfig.getHost(), ftperConfig.getPort());
            } catch (Exception e) {
                throw new IllegalStateException(tip(ftperConfig) + " connect fail.", e);
            }

            // 未连接
            if (FTPReply.isPositiveCompletion(ftpsClient.getReplyCode()) == false) {
                close(ftpsClient);
                throw new IllegalStateException(tip(ftperConfig) + "Not Connect");
            }

            boolean login;
            try {
                login = ftpsClient.login(ftperConfig.getUsername(), ftperConfig.getPassword());
            } catch (Exception e) {
                close(ftpsClient);
                throw new IllegalStateException(tip(ftperConfig) + " login exception !", e);
            }

            if (!login) {
                close(ftpsClient);
                throw new IllegalStateException(tip(ftperConfig) + " login fail !");
            }

            try {
                // Set protection buffer size
                ftpsClient.execPBSZ(0);
                // Set data channel protection to private
                ftpsClient.execPROT("P");
            } catch (IOException e) {
                throw new IllegalStateException(tip(ftperConfig) + " execPROT fail !");
            }

            return ftpsClient;
        }

        /**
         * 创建
         * 
         * @param ftperConfig
         */
        public ApacheFtper build(FtperConfig ftperConfig) {

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

            // 设置被动模式
            if (ftperConfig.isPasvMode()) {
                ftpClient.enterLocalPassiveMode();
            }

            // 设置传输模式
            setFileTransferMode(ftpClient, ftperConfig);

            // 设置文件类型（二进制）
            setFileType(ftpClient, ftperConfig);

            ftpClient.setControlKeepAliveTimeout(30);
            ftpClient.setControlKeepAliveReplyTimeout(ftperConfig.getTimeout() * 1000);

            return new ApacheFtper(ftpClient, ftperConfig);
        }

        /**
         * 日志
         * 
         * @param ftperConfig
         */
        private String tip(FtperConfig ftperConfig) {
            return "FTP host[" + ftperConfig.getHost() + "] user[" + ftperConfig.getUsername() + "] ";
        }
    }

    /* 
    * @see org.mpro.netftp.IFtper#cloneFtper() 
    */
    @Override
    public ApacheFtper cloneFtper() {
        return new Builder().build(ftperConfig);
    }

}
