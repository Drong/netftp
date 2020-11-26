package org.mpro.netftp.watche;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.mpro.netftp.util.FileHelper;
import org.mpro.netftp.util.LogHelper;

/**
 * @author 736779458@qq.com
 * @version 1.0
 * @created 22-十一月-2020 22:09:18
 */
public class DownloadAction implements Filter {

    /**
     * 本地存储目录路径
     */
    private String store;

    /**
     * 构造
     * 
     * @param store
     *            本地存储目录路径
     */
    public DownloadAction(String store) {
        this.store = store;
    }

    /**
     * 过滤
     * 
     * @param ftperFile
     *            文件
     * @param filterChain
     *            过滤链
     */
    public void doFilter(WatcheParam watcheParam, FilterChain filterChain) {

        String localDirectory = store;

        String surplus = watcheParam.getDirPath().substring(watcheParam.getSrcPath().length());
        if (surplus.length() > 0) {
            localDirectory = localDirectory + surplus;
        }

        File localFile = new File(localDirectory, watcheParam.getName());
        try {
            FileHelper.createFile(localFile);
        } catch (Exception e) {
            LogHelper.getLog().error("[{}]", localFile.getAbsolutePath(), e);
            return;
        }

        try (FileOutputStream fos = new FileOutputStream(localFile)) {
            boolean res = watcheParam.down(watcheParam.getDirPath(), watcheParam.getName(), fos);
            if (res) {
                filterChain.doFilter(watcheParam);
            }
        } catch (IOException e) {
            LogHelper.getLog().error("[{}]", localFile.getAbsolutePath(), e);
            return;
        }
    }
}