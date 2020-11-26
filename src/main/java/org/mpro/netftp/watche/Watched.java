package org.mpro.netftp.watche;

import java.util.Observable;
import java.util.Observer;

/**
 * @author 736779458@qq.com
 * @version 1.0
 * @created 16-十一月-2020 18:17:20
 */
public class Watched implements Observer {

    /**
     * 过滤器
     */
    private Filter[] filters;

    /**
     * 构造
     * 
     * @param filters
     *            过滤器
     */
    public Watched(Filter[] filters) {
        this.filters = filters;
    }

    @Override
    public void update(Observable o, Object arg) {
        FilterChainFactory.getInstance().createFilterChain(filters).doFilter((WatcheParam)arg);
    }

    /**
     * 过滤器 get
     */
    public Filter[] getFilters() {
        return filters;
    }

    /**
     * 过滤器 set
     * 
     * @param newVal
     *            过滤器
     */
    public void setFilters(Filter[] newVal) {
        filters = newVal;
    }
}