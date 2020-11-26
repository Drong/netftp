package org.mpro.netftp;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

/**
 * Ftp 协议配置
 * 
 * @author 736779458@qq.com
 * @version 1.0
 * @updated 25-十一月-2020 16:13:53
 */
public class FtperConfig implements Serializable {

    private static final long serialVersionUID = -6817225902288588671L;

    public static enum ProtocolEnum {

        sftp, ftp, ftps;
    }

    public static enum TypeEnum {

        apache_common, ftp4j, jsch;
    }

    /**
     * 连接地址
     */
    @Getter
    @Setter
    private String host;

    /**
     * 连接端口
     */
    @Getter
    @Setter
    private int port;

    /**
     * 账号
     */
    @Getter
    @Setter
    private String username;

    /**
     * 密码
     */
    @Getter
    @Setter
    private String password;

    /**
     * ftp 协议实现版，有如下实现版：
     * <ul>
     * <li>apache common</li>
     * <li>ftp4j</li>
     * <li>jsch</li>
     * </ul>
     * 默认 “apache common”
     */
    @Getter
    @Setter
    private TypeEnum type;

    /**
     * 协议，有如下协议：
     * <ul>
     * <li>ftp 协议</li>
     * <li>sftp 协议</li>
     * </ul>
     */
    @Getter
    @Setter
    private ProtocolEnum protocol;

    /**
     * 请求模式，有如下模式：
     * <ul>
     * <li>被动模式</li>
     * <li>主动模式</li>
     * </ul>
     * 默认 “被动模式”
     */
    @Getter
    @Setter
    private boolean pasvMode = true;

    /**
     * 路径编码
     */
    @Getter
    @Setter
    private String fencoding;

    /**
     * 无响应最长等待时长。
     */
    @Getter
    @Setter
    private int timeout;

    /**
     * 配置连接地址
     * 
     * @param host
     *            host
     */
    public static Builder withHost(String host) {
        return new Builder().withHost(host);
    }

    /**
     * 配置连接端口
     * 
     * @param port
     *            port
     */
    public static Builder withPort(int port) {
        return new Builder().withPort(port);
    }

    /**
     * 配置账号
     * 
     * @param username
     *            username
     */
    public static Builder withUsername(String username) {
        return new Builder().withUsername(username);
    }

    /**
     * 配置密码
     * 
     * @param password
     *            password
     */
    public static Builder withPassword(String password) {
        return new Builder().withPassword(password);
    }

    /**
     * 配置 ftp 协议实现版
     * 
     * @param type
     *            type
     */
    public static Builder withType(TypeEnum type) {
        return new Builder().withType(type);
    }

    /**
     * 配置请求模式
     * 
     * @param pasvMode
     *            pasvMode
     */
    public static Builder withPasvMode(boolean pasvMode) {
        return new Builder().withPasvMode(pasvMode);
    }

    /**
     * 配置路径编码
     * 
     * @param fencoding
     *            fencoding
     */
    public static Builder withFencoding(String fencoding) {
        return new Builder().withFencoding(fencoding);
    }

    /**
     * 无响应最长等待时长。单位（秒）。
     * 
     * @param timeout
     *            timeout
     */
    public static Builder withTimeout(int timeout) {
        return new Builder().withTimeout(timeout);
    }

    /**
     * Ftp 协议配置构建器
     * 
     * @author Think
     * @version 1.0
     * @updated 25-十一月-2020 16:13:53
     */
    public static class Builder {

        /**
         * Ftp 协议配置
         */
        protected FtperConfig ftperConfig = new FtperConfig();

        /**
         * 配置连接地址
         * 
         * @param host
         */
        public Builder withHost(String host) {
            ftperConfig.setHost(host);
            return this;
        }

        /**
         * 配置连接端口
         * 
         * @param port
         */
        public Builder withPort(int port) {
            ftperConfig.setPort(port);
            return this;
        }

        /**
         * 配置账号
         * 
         * @param username
         */
        public Builder withUsername(String username) {
            ftperConfig.setUsername(username);
            return this;
        }

        /**
         * 配置密码
         * 
         * @param password
         */
        public Builder withPassword(String password) {
            ftperConfig.setPassword(password);
            return this;
        }

        /**
         * 配置 ftp 协议实现版
         * 
         * @param type
         */
        public Builder withType(TypeEnum type) {
            ftperConfig.setType(type);
            return this;
        }

        /**
         * 配置 ftp 协议
         * 
         * @param protocol
         */
        public Builder withProtocol(ProtocolEnum protocol) {
            ftperConfig.setProtocol(protocol);
            return this;
        }

        /**
         * 配置请求模式
         * 
         * @param pasvMode
         */
        public Builder withPasvMode(boolean pasvMode) {
            ftperConfig.setPasvMode(pasvMode);
            return this;
        }

        /**
         * 配置路径编码
         * 
         * @param fencoding
         */
        public Builder withFencoding(String fencoding) {
            ftperConfig.setFencoding(fencoding);
            return this;
        }

        /**
         * 无响应最长等待时长。单位（秒）。
         * 
         * @param timeout
         */
        public Builder withTimeout(int timeout) {
            ftperConfig.setTimeout(timeout);
            return this;
        }

        /**
         * 构建 Ftp 协议配置
         */
        public FtperConfig build() {

            // 如果选择了 sftp 协议，那么类型必须是 jsch
            if (ftperConfig.getProtocol() == ProtocolEnum.sftp) {
                if (ftperConfig.getType() != TypeEnum.jsch) {
                    ftperConfig.setType(TypeEnum.jsch);
                }
            }

            return ftperConfig;
        }
    }

}
