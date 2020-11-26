package org.mpro.netftp.jsch;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.mpro.netftp.FtperFile;

import com.jcraft.jsch.ChannelSftp.LsEntry;
import com.jcraft.jsch.ChannelSftp.LsEntrySelector;
import com.jcraft.jsch.SftpATTRS;

import cn.hutool.core.util.StrUtil;
import lombok.Getter;
import lombok.Setter;

/**
 * jsch ls 获取文件列表后遍历文件被调用
 * 
 * @author 736779458@qq.com
 * @version 1.0
 * @updated 11-十一月-2020 17:30:12
 */
public class LSES implements LsEntrySelector {

    /**
     * 文件缓存
     */
    @Getter
    private List<FtperFile> files = new ArrayList<FtperFile>();

    /**
     * 路径
     */
    @Setter
    private String dirPath;

    /**
     * 初始化文件缓存
     */
    public void init() {
        files = new ArrayList<FtperFile>();
    }

    /**
     * 清除文件列表
     */
    public void clear() {
        files.clear();
    }

    /**
     * 遍历文件时被调用
     * 
     * @param entry
     */
    @Override
    public int select(LsEntry entry) {
        String fileName = entry.getFilename();
        if (StrUtil.equals(".", fileName)) {
            return CONTINUE;
        } else if (StrUtil.equals("..", fileName)) {
            return CONTINUE;
        }

        SftpATTRS attrs = entry.getAttrs();
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(attrs.getMTime() * 1000L);

        FtperFile ftpFile = new FtperFile();
        ftpFile.setName(entry.getFilename());
        ftpFile.setType(attrs.isDir() ? 1 : 0);
        ftpFile.setSize(attrs.getSize());
        ftpFile.setTimestamp(calendar);
        ftpFile.setDirPath(dirPath);
        ftpFile.setExists(true);
        files.add(ftpFile);

        return CONTINUE;
    }

}