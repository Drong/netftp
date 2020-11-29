package io.github.ludongrong.netftp.watche;

/**
 * 过滤器.
 *
 * @author <a href="mailto:736779458@qq.com">ludongrong</a>
 * @since 2020-11-27
 */
public interface Filter {

    /**
     * 过滤.
     *
     * @param watcheParam
     *            参数
     * @param filterChain
     *            过滤链
     */
    public void doFilter(WatcheParam watcheParam, FilterChain filterChain);
}