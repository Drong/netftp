package org.mpro.netftp.watche;

import org.mpro.netftp.util.AntPathMatcher;

/**
 * @author 736779458@qq.com
 * @version 1.0
 * @created 22-十一月-2020 22:09:14
 */
public class DirectoryFiler implements Filter {

    /**
     * 遵循 ant 规则
     * 
     * <ol>
     * <li>? 匹配任何单字符，必须要有1个</li>
     * <li>* 匹配0或者任意数量的字符</li>
     * <li>** 匹配0或者更多的目录</li>
     * </ol>
     */
    private String regulation;

    /**
     * 方向
     * <ul>
     * <li>true 表示正向过滤</li>
     * <li>false 表示反向过滤</li>
     * </ul>
     */
    private boolean direction;

    /**
     * 构造
     * 
     * @param regulation
     *            规则
     * @param direction
     *            方向
     */
    public DirectoryFiler(String regulation, boolean direction) {
        this.regulation = regulation;
        this.direction = direction;
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

        AntPathMatcher matcher = new AntPathMatcher();
        boolean result = matcher.match(regulation, watcheParam.getDirPath());
        if (direction ? result : !result) {
            filterChain.doFilter(watcheParam);
        }
    }
}