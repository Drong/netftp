package io.github.ludongrong.netftp.watche;

/**
 * 删除.
 *
 * @author <a href="mailto:736779458@qq.com">ludongrong</a>
 * @since 2020-11-27
 */
public class DeleteAction implements Filter {

    /**
    * @see io.github.ludongrong.netftp.watche.Filter#doFilter(io.github.ludongrong.netftp.watche.WatcheParam, io.github.ludongrong.netftp.watche.FilterChain)
    */
    public void doFilter(WatcheParam watcheParam, FilterChain filterChain) {

        boolean res = watcheParam.delete(watcheParam.getDirPath(), watcheParam.getName());
        if (res) {
            watcheParam.setExists(false);
            filterChain.doFilter(watcheParam);
        }
    }
}