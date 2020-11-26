package org.mpro.netftp.watche;

import java.io.File;

/**
 * @author 736779458@qq.com
 * @version 1.0
 * @created 22-十一月-2020 22:09:16
 */
public class TagFilter implements Filter {

    /**
     * 本地标签存放目录路径
     */
    private String store;

    /**
     * 拦截目录
     */
    private boolean intercepDirectory;

    /**
     * 构造
     * 
     * @param store
     *            本地标签存放目录路径
     * @param intercepDirectory
     *            拦截目录
     */
    public TagFilter(String store, boolean intercepDirectory) {
        this.store = store;
        this.intercepDirectory = intercepDirectory;
    }

    /**
     * 获取 tag 文件
     * 
     * @param watcheParam
     *            传参
     * @param filterChain
     *            过滤链
     */
    public File getTagFile(WatcheParam watcheParam, FilterChain filterChain) {

        String localDirectory = store;

        String surplus = watcheParam.getDirPath().substring(watcheParam.getSrcPath().length());
        if (surplus.length() > 0) {
            localDirectory = localDirectory + surplus;
        }

        return new File(localDirectory, watcheParam.getName());
    }

    /**
     * 过滤
     * 
     * @param watcheParam
     *            传参
     * @param filterChain
     *            过滤链
     */
    public void doFilter(WatcheParam watcheParam, FilterChain filterChain) {

        if (intercepDirectory || watcheParam.isDirectory() == false) {
            File tagfile = getTagFile(watcheParam, filterChain);
            if (tagfile.exists()) {
                return;
            }
        }

        filterChain.doFilter(watcheParam);
    }
}