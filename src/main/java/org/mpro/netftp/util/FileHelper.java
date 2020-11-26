package org.mpro.netftp.util;

import java.io.File;
import java.io.IOException;

public class FileHelper {

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
