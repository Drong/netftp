package io.github.ludongrong.netftp.util;

import java.text.MessageFormat;

import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;

/**
 * 提示.
 *
 * @author <a href="mailto:736779458@qq.com">ludongrong</a>
 * @since 2020-11-27
 */
public class TipHelper {

    /**
     * 获取日志对象.
     *
     * @return 日志对象
     */
    public static String checkParam_NonNull(String param) {
        return MessageFormat.format("The param must have a non-null {}.", param);
    }
}