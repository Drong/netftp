package io.github.ludongrong.netftp.util;

import java.io.File;
import java.io.IOException;

/**
 * 文件助手.
 *
 * @author <a href="mailto:736779458@qq.com">ludongrong</a>
 * @since 2020-11-27
 */
public class FileHelper {

    /**
     * 层级文件.
     *
     * @param file
     *            文件
     * @throws IOException
     *             io异常
     */
    public static void createFile(File file) throws IOException {

        if (file.exists()) {
            return;
        }

        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        file.createNewFile();
    }
}
