package io.github.ludongrong.netftp;

/**
 * ftper 工厂.
 *
 * @author <a href="mailto:736779458@qq.com">ludongrong</a>
 * @since 2020-11-27
 */
public class FtperFactory {

    /**
     * 创建.
     *
     * @param ftperConfig
     *            配置
     * @return 客户端
     */
    static public IFtper createFtper(FtperConfig ftperConfig) {

        switch (ftperConfig.getType()) {
            case apache_common:
                return new ApacheFtper.Builder().build(ftperConfig);
            case ftp4j:
                return new Ftp4jFtper.Builder().build(ftperConfig);
            case jsch:
                return new JschFtper.Builder().build(ftperConfig);
            default:
                throw new IllegalStateException("Unsupported type [" + ftperConfig.getType() + "]");
        }
    }
}