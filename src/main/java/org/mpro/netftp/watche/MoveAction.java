package org.mpro.netftp.watche;

/**
 * @author 736779458@qq.com
 * @version 1.0
 * @created 22-十一月-2020 22:09:16
 */
public class MoveAction implements Filter {

    /**
     * 目标目录路径
     */
    private String store;

    /**
     * 附加剩余路径
     */
    private boolean attach;

    /**
     * 构造
     * 
     * @param store
     *            目标目录路径
     */
    public MoveAction(String store, boolean attach) {
        this.store = store;
        this.attach = attach;
    }

    /**
     * 执行 move 操作。
     * 
     * @param watcheParam
     *            传参
     * @param filterChain
     *            过滤链
     */
    public void doFilter(WatcheParam watcheParam, FilterChain filterChain) {

        String targetDirectory = store;

        if (attach) {
            String surplus = watcheParam.getDirPath().substring(watcheParam.getSrcPath().length());
            if (surplus.length() > 0) {
                targetDirectory = targetDirectory + surplus;
            }
        }

        boolean res =
            watcheParam.move(watcheParam.getDirPath(), watcheParam.getName(), targetDirectory, watcheParam.getName());
        if (res) {
            watcheParam.setDirPath(targetDirectory);
            filterChain.doFilter(watcheParam);
        }
    }
}