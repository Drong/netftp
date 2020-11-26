package org.mpro.netftp;

import lombok.Getter;
import lombok.Setter;

/**
 * ftp 操作信号
 * 
 * @author 736779458@qq.com
 * @version 1.0
 * @updated 11-十一月-2020 7:59:42
 */
public class FtperOper extends FtperConfig {

    private static final long serialVersionUID = -5403873159225569092L;

    @Getter
    @Setter
    private String srcPath;

    @Getter
    @Setter
    private String destPath;
    /**
     * 操作
     */
    @Getter
    @Setter
    private int method;
    /**
     * 源文件
     */
    @Getter
    @Setter
    private String src;
    /**
     * 目标文件
     */
    @Getter
    @Setter
    private String dest;

}