package org.mpro.netftp.watche;

import java.io.File;

import org.mpro.netftp.util.LogHelper;

/**
 * @author 736779458@qq.com
 * @version 1.0
 * @created 22-十一月-2020 22:09:16
 */
public class TagCreateFilter extends TagFilter {

    /**
     * 构造
     */
    public TagCreateFilter(String store, boolean intercepDirectory) {
        super(store, intercepDirectory);
    }

    /**
     * 过滤
     * 
     * @param watcheParam
     *            传参
     * @param filterChain
     *            过滤链
     */
    public void doFilter(WatcheParam watcheParam, FilterChain filterChain) {

        File tagFile = getTagFile(watcheParam, filterChain);
        if (tagFile.exists() == false) {
            try {
                if (watcheParam.isDirectory() == false) {
                    File tagParent = tagFile.getParentFile();
                    if (tagParent.exists() == false) {
                        tagParent.mkdirs();
                    }

                    tagFile.createNewFile();
                } else {
                    tagFile.mkdirs();
                }
            } catch (Exception e) {
                LogHelper.getLog().error("[{}]", tagFile.getAbsolutePath(), e);
            }
        }

        filterChain.doFilter(watcheParam);
    }
}