package io.github.ludongrong.netftp.util;

import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;

/**
 * 日历助手.
 *
 * @author <a href="mailto:736779458@qq.com">ludongrong</a>
 * @since 2020-11-27
 */
public class LogHelper {

    private static final Log log = LogFactory.get("netftp");

    /**
     * 获取日志对象.
     *
     * @return 日志对象
     */
    public static Log getLog() {
        return log;
    }
}