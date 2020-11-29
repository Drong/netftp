package io.github.ludongrong.netftp;

import lombok.Getter;
import lombok.Setter;

/**
 * ftp 操作信号.
 *
 * @author <a href="mailto:736779458@qq.com">ludongrong</a>
 * @since 2020-11-27
 */
public class FtperOper extends FtperConfig {

    private static final long serialVersionUID = -5403873159225569092L;

    /** 操作 */
    @Getter
    @Setter
    private int method;

    /** 源文件 */
    @Getter
    @Setter
    private String src;

    /** 目标文件 */
    @Getter
    @Setter
    private String dest;
}