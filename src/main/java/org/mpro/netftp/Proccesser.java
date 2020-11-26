package org.mpro.netftp;

import java.util.List;

/**
 * 层级处理者的抽象
 * 
 * @author 736779458@qq.com
 * @version 1.0
 * @updated 25-十一月-2020 15:29:23
 */
public interface Proccesser<T> {

    /**
     * 每个层级都会触发
     * 比如路径/11/22/33,那么路径会被切割成三层。
     * 第一层：目录路径/；文件名11
     * 第二层：目录路径/11；文件名22
     * 第三层：目录路径/11/22；文件名33
     * 
     * @return true表示继续下钻；false表示停止下钻
     * 
     * @param src
     *            目录路径
     * @param fname
     *            文件名
     * @param exists
     *            true表示存在；false表示不存在
     */
    boolean pre(String src, String fname, boolean exists);

    /**
     * 最后一层执行
     *
     * @param ftpFiles
     *            文件列表
     */
    void last(List<FtperFile> ftpFiles);

    /**
     * 最后输出结果
     *
     * @return 动态类型
     */
    T result();
}