package org.mpro.netftp.watche;

/**
 * @author 736779458@qq.com
 * @version 1.0
 * @created 22-十一月-2020 22:09:19
 */
public class DeleteAction implements Filter {

    /**
     * 删除远程文件
     * 
     * @param watcheParam
     *            传参
     * @param filterChain
     *            过滤器
     */
    public void doFilter(WatcheParam watcheParam, FilterChain filterChain) {

        boolean res = watcheParam.delete(watcheParam.getDirPath(), watcheParam.getName());
        if (res) {
            watcheParam.setExists(false);
            filterChain.doFilter(watcheParam);
        }
    }
}