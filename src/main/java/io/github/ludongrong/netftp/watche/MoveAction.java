package io.github.ludongrong.netftp.watche;

/**
 * 迁移.
 *
 * @author <a href="mailto:736779458@qq.com">ludongrong</a>
 * @since 2020-11-27
 */
public class MoveAction implements Filter {

    /** 目标目录路径 */
    private String store;

    /** 附加剩余路径 */
    private boolean attach;

    /**
     * 构造.
     * 
     * <p>
     * 剩余路径是去除调扫描根路径后的路径，第一个字符不包含/
     * 
     * <p>
     * 假如扫描的根路径/test；扫描到的文件路径是/test/1/2.csv，那么剩余路径是1/2.csv
     *
     * @param store
     *            目标目录路径
     * @param attach
     *            是否附加剩余路径。true表示附加；false表示不附加
     */
    public MoveAction(String store, boolean attach) {
        this.store = store;
        this.attach = attach;
    }

    /**
    * @see io.github.ludongrong.netftp.watche.Filter#doFilter(io.github.ludongrong.netftp.watche.WatcheParam, io.github.ludongrong.netftp.watche.FilterChain)
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