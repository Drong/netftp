package io.github.ludongrong.netftp.watche;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import io.github.ludongrong.netftp.util.FileHelper;
import io.github.ludongrong.netftp.util.LogHelper;

/**
 * 下载.
 *
 * @author <a href="mailto:736779458@qq.com">ludongrong</a>
 * @since 2020-11-27
 */
public class DownloadAction implements Filter {

    /**
     * 本地存储目录路径
     */
    private String store;

    /**
     * 构造.
     *
     * @param store
     *            本地存储目录路径
     */
    public DownloadAction(String store) {
        this.store = store;
    }

    /**
    * @see io.github.ludongrong.netftp.watche.Filter#doFilter(io.github.ludongrong.netftp.watche.WatcheParam, io.github.ludongrong.netftp.watche.FilterChain)
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