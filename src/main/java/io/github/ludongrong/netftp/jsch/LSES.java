package io.github.ludongrong.netftp.jsch;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.jcraft.jsch.ChannelSftp.LsEntry;
import com.jcraft.jsch.ChannelSftp.LsEntrySelector;
import com.jcraft.jsch.SftpATTRS;

import cn.hutool.core.util.StrUtil;
import io.github.ludongrong.netftp.FtperFile;
import lombok.Getter;
import lombok.Setter;

/**
 * jsch ls 获取文件列表后遍历文件被调用.
 *
 * @author <a href="mailto:736779458@qq.com">ludongrong</a>
 * @since 2020-11-27
 */
public class LSES implements LsEntrySelector {

    /** 文件列表 */
    @Getter
    private List<FtperFile> files = new ArrayList<FtperFile>();

    /** 路径 */
    @Setter
    private String dirPath;

    /**
     * 初始化文件列表.
     *
     */
    public void init() {
        files = new ArrayList<FtperFile>();
    }

    /**
     * 清除文件列表.
     *
     */
    public void clear() {
        files.clear();
    }

    /**
     * @see com.jcraft.jsch.ChannelSftp.LsEntrySelector#select(com.jcraft.jsch.ChannelSftp.LsEntry)
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