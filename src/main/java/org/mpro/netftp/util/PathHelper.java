package org.mpro.netftp.util;

import cn.hutool.core.util.StrUtil;

/**
 * 路径工具
 * 
 * @author 736779458@qq.com
 * @version 1.0
 * @updated 11-十一月-2020 17:15:53
 */
public class PathHelper {

    /**
     * 格式化成标准路径s
     * <ul>
     * <li>/</li>
     * <li>/item</li>
     * </ul>
     * <ul>
     * <li>/item/item</li>
     * <li>/item/item...</li>
     * </ul>
     * 
     * @param path
     *            路径
     */
    public static String format(String path) {

        String reg0 = "\\\\+";
        String reg = "\\\\+|/+";

        String temp = path.trim().replaceAll(reg0, "/");
        temp = temp.replaceAll(reg, "/");

        if (temp.length() > 1 && temp.endsWith("/")) {
            temp = temp.substring(0, temp.length() - 1);
        }

        return temp;
    }

    /**
     * 校验路径 以/开头。
     * 
     * @param path
     *            路径
     */
    public static void check(String path) {

        if (StrUtil.isBlank(path)) {
            new IllegalArgumentException("empty");
        }

        if (path.startsWith(StrUtil.SLASH) == false) {
            new IllegalArgumentException("slash");
        }
    }

    /**
     * 校验文件名。 不能含有分割符（/和\）
     * 
     * @param fname
     *            文件名
     */
    public static void checkfname(String fname) {

        if (StrUtil.isBlank(fname)) {
            throw new IllegalArgumentException(">>>eee>>> fname empty");
        } else if (fname.indexOf(StrUtil.SLASH) > 0) {
            throw new IllegalArgumentException(">>>eee>>> dir");
        }
    }

    /**
     * 切割路径
     * 
     * @param srcdirpath
     *            路径
     */
    public static String[] split(String srcdirpath) {

        if (srcdirpath.startsWith(StrUtil.SLASH)) {
            srcdirpath = srcdirpath.substring(1);
        }

        int itemSize = 1;

        String[] splits = null;
        if (srcdirpath.length() > 0) {
            splits = srcdirpath.split(StrUtil.SLASH);
            itemSize += splits.length;
        }

        String[] items = new String[itemSize];
        if (itemSize > 1) {
            System.arraycopy(splits, 0, items, 1, splits.length);
        }

        items[0] = StrUtil.SLASH;

        return items;
    }

    /**
     * 切割出父级目录和文件名
     * 
     * @param path
     *            路径
     */
    public static String[] pset(String path) {

        int li = path.lastIndexOf(StrUtil.SLASH);
        if (li == 0) {
            throw new IllegalArgumentException("srcpath equals " + StrUtil.SLASH);
        }

        String dir = path.substring(0, li);
        String fname = path.substring(li + 1);

        return new String[] {dir, fname};
    }
}