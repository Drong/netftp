package org.mpro.netftp.watche;

/**
 * @author 736779458@qq.com
 * @version 1.0
 * @created 22-十一月-2020 22:09:38
 */
public class FilterChainFactory {

    /**
     * 过滤器工厂实例
     */
    private static FilterChainFactory factory;

    /**
     * 获取过滤器工厂实例
     */
    public static FilterChainFactory getInstance() {
        if (factory == null)
            factory = new FilterChainFactory();

        return factory;
    }

    /**
     * 创建过滤器链
     * 
     * @param filters
     *            过滤器
     */
    public FilterChain createFilterChain(Filter[] filters) {
        Chain filterChain = new Chain();
        for (int i = 0; i < filters.length; ++i) {
            filterChain.addFilter(filters[i]);
        }
        return filterChain;
    }
}