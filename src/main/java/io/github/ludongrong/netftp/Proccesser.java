package io.github.ludongrong.netftp;

import java.util.List;

/**
 * 层级处理者.
 *
 * @author <a href="mailto:736779458@qq.com">ludongrong</a>
 * @since 2020-11-27
 */
public interface Proccesser<T> {

    /**
     * 每个层级处理.
     *
     * <p>
     * 比如路径/11/22/33,那么路径会被切割成三层。
     * 第一层：目录路径/；文件名11
     * 第二层：目录路径/11；文件名22
     * 第三层：目录路径/11/22；文件名33
     *
     * @param src
     *            目录路径
     * @param fname
     *            文件名
     * @param exists
     *            true表示层级存在；false表示层级不存在
     * @return true表示继续下钻；false表示停止下钻
     */
    boolean pre(String src, String fname, boolean exists);

    /**
     * 最后一层执行.
     *
     * @param ftpFiles
     *            文件列表
     */
    void last(List<FtperFile> ftpFiles);

    /**
     * 最后输出结果.
     *
     * @return 结果
     */
    T result();
}