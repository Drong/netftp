package org.mpro.netftp.watche;

/**
 * @author 736779458@qq.com
 * @version 1.0
 * @created 21-十一月-2020 15:25:29
 */
public class Chain implements FilterChain {

    /**
     * 滤器存储数组
     */
    private Filter[] filters;

    /**
     * 过滤器数组下标-用来标记执行滤器
     */
    private int pos = 0;

    /**
     * 过滤器数组下标-用来标记添加滤器
     */
    private int n = 0;

    /**
     * 构造
     */
    public Chain() {
        super();
        filters = new Filter[10];
    }

    /**
     * 过滤器存储-添加
     * 
     * @param filter
     *            过滤器
     */
    public void addFilter(Filter filter) {
        // 到达存储数组的上限
        // 扩展过滤器存储数组
        if (this.n == this.filters.length) {
            Filter[] newFilters = new Filter[this.n + 10];

            System.arraycopy(this.filters, 0, newFilters, 0, this.n);
            this.filters = newFilters;
        }

        this.filters[(this.n++)] = filter;
    }

    /**
     * 过滤器存储-释放
     */
    public void release() {
        // 把存储的过滤器赋值NULL
        for (int i = 0; i < this.n; ++i)
            this.filters[i] = null;

        // 已添加过滤器个数赋值0
        this.n = 0;

        // 已执行过滤器数组下标赋值0
        this.pos = 0;
    }

    /**
     * 过滤器存储-再次使用
     */
    public void reuse() {
        // 已执行过滤器数组下标赋值0
        this.pos = 0;
    }

    /**
     * 过滤
     * 
     * @param watcheParam
     *            传参
     */
    @Override
    public void doFilter(WatcheParam watcheParam) {
        if (this.pos < this.n) {
            Filter filter = this.filters[(this.pos++)];
            filter.doFilter(watcheParam, this);
        }
    }
}