package io.github.ludongrong.netftp.watche;

import java.util.Calendar;

/**
 * 文件最后修改时间过滤器.
 *
 * @author <a href="mailto:736779458@qq.com">ludongrong</a>
 * @since 2020-11-27
 */
public class FileModifyTimeFilter implements Filter {

    /** 参考时间 */
    private long compare;

    /**
     * 方向
     * <ul>
     * <li>true 表示正向过滤，过滤参考时间以前项</li>
     * <li>false 表示反向过滤，过滤参考时间以后项</li>
     * </ul>
     */
    private boolean direction;

    /**
     * 构造.
     * 
     * <p>
     * 方向
     * <ul>
     * <li>true 表示正向过滤</li>
     * <li>false 表示反向过滤</li>
     * </ul>
     * 
     * @param compare
     *            参考时间
     * @param direction
     *            方向
     */
    public FileModifyTimeFilter(long compare, boolean direction) {
        this.compare = compare;
        this.direction = direction;
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

        Calendar lastModifyTime = watcheParam.getLastModifyTime();
        if (lastModifyTime != null) {
            boolean result = lastModifyTime.getTimeInMillis() < compare;
            if (direction ? result : !result) {
                filterChain.doFilter(watcheParam);
            }
        }
    }
}