package io.github.ludongrong.netftp;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

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
public interface IFtper {

    /**
     * 获取 ftp 服务地址.
     * 
     * @return ftp 服务地址
     */
    String getHost();

    /**
     * 获取 ftp 服务端口.
     * 
     * @return ftp 服务端口
     */
    int getPort();

    /**
     * 获取 ftp 服务账号.
     * 
     * @return ftp 服务账号
     */
    String getUsername();

    /**
     * 创建目录.
     * 
     * @param dst
     *            目录路径
     * @return 操作结果。true表示成功；false表示失败
     */
    public boolean createDirectory(String dst);

    /**
     * 获取指定路径下的文件列表.
     * 
     * @param dst
     *            目录路径
     * @return 文件列表
     */
    List<FtperFile> listFile(String dst);

    /**
     * 上传文件.
     * 
     * @param dst
     *            目录路径
     * @param filename
     *            文件名
     * @param is
     *            输入流
     * @return 操作结果。true表示成功；false表示失败
     */
    boolean upload(String dst, String filename, InputStream is);

    /**
     * 下载文件.
     * 
     * @param dst
     *            目录路径
     * @param fname
     *            文件名
     * @param os
     *            输出流
     * @return 操作结果。true表示成功；false表示失败
     */
    boolean down(String dst, String fname, OutputStream os);

    /**
     * 删除文件.
     * 
     * @param dst
     *            目录路径
     * @param fname
     *            文件名
     * @return 操作结果。true表示成功；false表示失败
     */
    boolean delete(String dst, String fname);

    /**
     * 移动文件.
     * 
     * @param src
     *            源目录路径
     * @param sname
     *            源文件名
     * @param dst
     *            目标目录路径
     * @param dname
     *            目标文件名
     * @return 操作结果。true表示成功；false表示失败
     */
    boolean move(String src, String sname, String dst, String dname);

    /**
     * 关闭ftp连接.
     */
    void close();

    /**
     * 克隆.
     * 
     * <p>
     * 用 ftper 自带的账号/密码重新构造个ftper.
     * 
     * @return ftp客户端
     */
    IFtper cloneFtper();

    /**
     * 搬运.
     * 
     * <p>
     * 把 A 服务器上的文件搬运到 B 服务器上.
     * 
     * <p>
     * 
     * <pre>
     * {
     *     &#64;code
     *     IFtper ftperA = FtperFactory.createFtper(ftperConfigA.build());
     *     IFtper ftperB = FtperFactory.createFtper(ftperConfigB.build());
     *     ftperA.carry(ftperB, "/test", "1.csv", "/test", "2.csv");
     * }
     * </pre>
     *
     * @param ftper
     *            目标客户端
     * @param src
     *            源服务器的目录
     * @param sname
     *            源服务器的文件名
     * @param dest
     *            目标服务器的目录
     * @param dname
     *            目标服务器的文件名
     * @return 操作结果。true表示成功；false表示失败
     */
    boolean carry(IFtper ftper, String src, String sname, String dest, String dname);
}
