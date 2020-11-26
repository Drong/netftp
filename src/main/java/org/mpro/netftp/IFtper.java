package org.mpro.netftp;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

/**
 * Ftp 协议操作抽象，操作行为包括：
 * <ul>
 * <li>包含创建目录</li>
 * <li>获取文件列表</li>
 * <li>上传文件</li>
 * <li>下载文件</li>
 * <li>移动文件</li>
 * </ul>
 * 
 * @author 736779458@qq.com
 * @version 1.0
 * @updated 11-十一月-2020 7:30:22
 */
public interface IFtper {

    String getHost();

    int getPort();

    String getUsername();

    /**
     * 如果目录不存在，则创建目录。
     * 
     * @param dst
     */
    public boolean createDirectory(String dst);

    /**
     * 获取指定路径下的文件列表
     * 
     * @param dst
     */
    List<FtperFile> listFile(String dst);

    /**
     * 上传文件
     * 
     * @param dst
     * @param filename
     * @param is
     */
    boolean upload(String dst, String filename, InputStream is);

    /**
     * 下载文件
     * 
     * @param dst
     * @param fname
     * @param os
     */
    boolean down(String dst, String fname, OutputStream os);

    /**
     * 删除文件
     * 
     * @param dst
     * @param fname
     */
    boolean delete(String dst, String fname);

    /**
     * 移动文件
     * 
     * @param src
     * @param sname
     * @param dst
     * @param dname
     */
    boolean move(String src, String sname, String dst, String dname);

    /**
     * 关闭ftp连接
     */
    void close();

    /**
     * 克隆 ftper
     */
    IFtper cloneFtper();
}
