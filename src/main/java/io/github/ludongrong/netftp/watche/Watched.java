package io.github.ludongrong.netftp.watche;

import java.util.Observable;
import java.util.Observer;

import lombok.Getter;
import lombok.Setter;

/**
 * 扫描监控观察者.
 *
 * @author <a href="mailto:736779458@qq.com">ludongrong</a>
 * @since 2020-11-27
 */
public class Watched implements Observer {

    /** 过滤器 */
    @Getter
    @Setter
    private Filter[] filters;

    /**
     * 构造.
     *
     * @param filters
     *            过滤器
     */
    public Watched(Filter[] filters) {
        this.filters = filters;
    }

    /**
     * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
     */
    @Override
    public void update(Observable o, Object arg) {
        FilterChainFactory.getInstance().createFilterChain(filters).doFilter((WatcheParam)arg);
    }
}