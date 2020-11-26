package org.mpro.netftp.watche;

/**
 * @author 736779458@qq.com
 * @version 1.0
 * @created 22-十一月-2020 22:09:37
 */
public interface FilterChain {

    /**
     * 过滤
     * 
     * @param watcheParam
     *            传参
     */
    public void doFilter(WatcheParam watcheParam);

}