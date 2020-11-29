package io.github.ludongrong.netftp.watche;

import io.github.ludongrong.netftp.util.AntPathMatcher;

/**
 * 目录过滤器.
 *
 * @author <a href="mailto:736779458@qq.com">ludongrong</a>
 * @since 2020-11-27
 */
public class DirectoryFiler implements Filter {

    /** 规则 */
    private String regulation;

    /** 方向 */
    private boolean direction;

    /**
     * 构造.
     * 
     * <p>
     * 规则遵循 ant 规则
     * <ol>
     * <li>? 匹配任何单字符，必须要有1个</li>
     * <li>* 匹配0或者任意数量的字符</li>
     * <li>** 匹配0或者更多的目录</li>
     * </ol>
     * 
     * <p>
     * 方向
     * <ul>
     * <li>true 表示正向过滤</li>
     * <li>false 表示反向过滤</li>
     * </ul>
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
    * @see io.github.ludongrong.netftp.watche.Filter#doFilter(io.github.ludongrong.netftp.watche.WatcheParam, io.github.ludongrong.netftp.watche.FilterChain)
    */
    public void doFilter(WatcheParam watcheParam, FilterChain filterChain) {

        AntPathMatcher matcher = new AntPathMatcher();
        boolean result = matcher.match(regulation, watcheParam.getDirPath());
        if (direction ? result : !result) {
            filterChain.doFilter(watcheParam);
        }
    }
}