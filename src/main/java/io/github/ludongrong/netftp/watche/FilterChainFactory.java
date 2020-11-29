package io.github.ludongrong.netftp.watche;

/**
 * 过滤链工厂.
 *
 * @author <a href="mailto:736779458@qq.com">ludongrong</a>
 * @since 2020-11-27
 */
public class FilterChainFactory {

    /** 过滤器工厂实例 */
    private static FilterChainFactory factory;

    /**
     * 获取过滤器工厂实例.
     *
     * @return 过滤器工厂实例
     */
    public static FilterChainFactory getInstance() {
        if (factory == null)
            factory = new FilterChainFactory();

        return factory;
    }

    /**
     * 创建过滤器链.
     *
     * @param filters
     *            过滤器
     * @return 过滤链
     */
    public FilterChain createFilterChain(Filter[] filters) {
        Chain filterChain = new Chain();
        for (int i = 0; i < filters.length; ++i) {
            filterChain.addFilter(filters[i]);
        }
        return filterChain;
    }
}