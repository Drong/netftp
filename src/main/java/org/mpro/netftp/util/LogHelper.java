package org.mpro.netftp.util;

import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;

/**
 * 日志工具
 * @author 736779458@qq.com
 * @version 1.0
 * @updated 11-十一月-2020 17:11:11
 */
public class LogHelper {

    private static final Log log = LogFactory.get("netftp");

    /**
     * 获取日志对象
     */
    public static Log getLog() {
        return log;
    }

}