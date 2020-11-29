package io.github.ludongrong.netftp.watche;

import java.io.File;

/**
 * 标签过滤器.
 *
 * @author <a href="mailto:736779458@qq.com">ludongrong</a>
 * @since 2020-11-27
 */
public class TagFilter implements Filter {

    /** 本地标签存放目录路径 */
    private String store;

    /** 拦截目录 */
    private boolean intercepDirectory;

    /**
     * 构造.
     *
     * @param store
     *            本地存储目录
     * @param intercepDirectory
     *            是否要为文件类型是目录类型的文件做标签
     */
    public TagFilter(String store, boolean intercepDirectory) {
        this.store = store;
        this.intercepDirectory = intercepDirectory;
    }

    /**
     * 获取 tag 文件.
     *
     * @param watcheParam
     *            参数
     * @param filterChain
     *            过滤链
     * @return tag本地存储文件
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
     * @see io.github.ludongrong.netftp.watche.Filter#doFilter(io.github.ludongrong.netftp.watche.WatcheParam,
     *      io.github.ludongrong.netftp.watche.FilterChain)
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