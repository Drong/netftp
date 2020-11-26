package org.mpro.netftp.watche;

/**
 * 过滤器
 * 
 * @author 736779458@qq.com
 * @version 1.0
 * @updated 24-十一月-2020 21:34:50
 */
public interface Filter {

    /**
     * 过滤
     * 
     * @param watcheParam
     *            传参
     * @param filterChain
     *            过滤链
     */
    public void doFilter(WatcheParam watcheParam, FilterChain filterChain);

}