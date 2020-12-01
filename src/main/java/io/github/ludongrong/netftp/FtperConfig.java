package io.github.ludongrong.netftp;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

/**
 * Ftp 配置.
 *
 * @author <a href="mailto:736779458@qq.com">ludongrong</a>
 * @since 2020-11-27
 */
public class FtperConfig implements Serializable {

    private static final long serialVersionUID = -6817225902288588671L;

    /**
     * 协议.
     */
    public static enum ProtocolEnum {

        sftp, ftp, ftps;
    }

    /**
     * 实现版本.
     */
    public static enum TypeEnum {

        apache_common, ftp4j, jsch;
    }

    /** 连接地址 */
    @Getter
    @Setter
    private String host;

    /** 连接端口 */
    @Getter
    @Setter
    private int port;

    /** 账号 */
    @Getter
    @Setter
    private String username;

    /** 密码 */
    @Getter
    @Setter
    private String password;

    /** ftp 协议实现版 */
    @Getter
    @Setter
    private TypeEnum type;

    /** 协议 */
    @Getter
    @Setter
    private ProtocolEnum protocol;

    /**
     * 请求模式，有如下模式.
     * 
     * <ul>
     * <li>被动模式</li>
     * <li>主动模式</li>
     * </ul>
     * 默认 “被动模式”
     */
    @Getter
    @Setter
    private boolean pasvMode = true;

    /** 路径编码 */
    @Getter
    @Setter
    private String fencoding;

    /** 无响应最长等待时长 */
    @Getter
    @Setter
    private int timeout;

    /**
     * 配置连接地址.
     *
     * @param host
     *            连接地址
     * @return 构建者
     */
    public static Builder withHost(String host) {
        return new Builder().withHost(host);
    }

    /**
     * 配置连接端口.
     *
     * @param port
     *            连接端口
     * @return 构建者
     */
    public static Builder withPort(int port) {
        return new Builder().withPort(port);
    }

    /**
     * 配置账号.
     *
     * @param username
     *            账号
     * @return 构建者
     */
    public static Builder withUsername(String username) {
        return new Builder().withUsername(username);
    }

    /**
     * 配置密码.
     *
     * @param password
     *            密码
     * @return 构建者
     */
    public static Builder withPassword(String password) {
        return new Builder().withPassword(password);
    }

    /**
     * 配置 ftp 协议实现版.
     *
     * @param type
     *            协议实现版
     * @return 构建者
     */
    public static Builder withType(TypeEnum type) {
        return new Builder().withType(type);
    }

    /**
     * 配置请求模式.
     *
     * @param pasvMode
     *            true表示被动模式；false表示主动模式
     * @return 构建者
     */
    public static Builder withPasvMode(boolean pasvMode) {
        return new Builder().withPasvMode(pasvMode);
    }

    /**
     * 配置路径编码.
     *
     * @param fencoding
     *            编码
     * @return 构建者
     */
    public static Builder withFencoding(String fencoding) {
        return new Builder().withFencoding(fencoding);
    }

    /**
     * 无响应最长等待时长。单位（秒）.
     *
     * @param timeout
     *            等待时长
     * @return 构建者
     */
    public static Builder withTimeout(int timeout) {
        return new Builder().withTimeout(timeout);
    }

    /**
     * 构建者.
     *
     * @author <a href="mailto:736779458@qq.com">ludongrong</a>
     * @since 2020-11-27
     */
    public static class Builder {

        /** 配置 */
        protected FtperConfig ftperConfig = new FtperConfig();

        /**
         * 配置连接地址.
         *
         * @param host
         *            连接地址
         * @return 构建者
         */
        public Builder withHost(String host) {
            ftperConfig.setHost(host);
            return this;
        }

        /**
         * 配置连接端口.
         *
         * @param port
         *            连接端口
         * @return 构建者
         */
        public Builder withPort(int port) {
            ftperConfig.setPort(port);
            return this;
        }

        /**
         * 配置账号.
         *
         * @param username
         *            账号
         * @return 构建者
         */
        public Builder withUsername(String username) {
            ftperConfig.setUsername(username);
            return this;
        }

        /**
         * 配置密码.
         *
         * @param password
         *            密码
         * @return 构建者
         */
        public Builder withPassword(String password) {
            ftperConfig.setPassword(password);
            return this;
        }

        /**
         * 配置协议实现版.
         * 
         * <ul>
         * <li>apache common</li>
         * <li>ftp4j</li>
         * <li>jsch</li>
         * </ul>
         * 默认 “apache common”
         *
         * @param type
         *            实现版
         * @return 构建者
         */
        public Builder withType(TypeEnum type) {
            ftperConfig.setType(type);
            return this;
        }

        /**
         * 配置协议.
         * 
         * <ul>
         * <li>ftp 协议</li>
         * <li>sftp 协议</li>
         * </ul>
         *
         * @param protocol
         *            协议
         * @return 构建者
         */
        public Builder withProtocol(ProtocolEnum protocol) {
            ftperConfig.setProtocol(protocol);
            return this;
        }

        /**
         * 配置请求模式.
         *
         * @param pasvMode
         *            true表示被动模式；false表示主动模式
         * @return 构造这
         */
        public Builder withPasvMode(boolean pasvMode) {
            ftperConfig.setPasvMode(pasvMode);
            return this;
        }

        /**
         * 配置路径编码.
         *
         * @param fencoding
         *            编码
         * @return 构建者
         */
        public Builder withFencoding(String fencoding) {
            ftperConfig.setFencoding(fencoding);
            return this;
        }

        /**
         * 无响应最长等待时长。单位（秒）.
         *
         * @param timeout
         *            等待时长
         * @return 构建者
         */
        public Builder withTimeout(int timeout) {
            ftperConfig.setTimeout(timeout);
            return this;
        }

        /**
         * 创建.
         *
         * @return 配置
         */
        public FtperConfig build() {

            // 如果选择了 sftp 协议，那么类型必须是 jsch
            if (ftperConfig.getProtocol() == ProtocolEnum.sftp) {
                if (ftperConfig.getType() != TypeEnum.jsch) {
                    ftperConfig.setType(TypeEnum.jsch);
                }
            }

            // ftp/ftps 协议，默认类似是 apache common
            if (ftperConfig.getType() == null) {
                ftperConfig.setType(TypeEnum.apache_common);
            }

            return ftperConfig;
        }
    }
}
