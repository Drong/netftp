package io.github.ludongrong.netftp.watche;

/**
 * 过滤链.
 *
 * @author <a href="mailto:736779458@qq.com">ludongrong</a>
 * @since 2020-11-27
 */
public interface FilterChain {

    /**
     * 过滤.
     *
     * @param watcheParam
     *            参数
     */
    public void doFilter(WatcheParam watcheParam);

}