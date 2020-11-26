package org.mpro.netftp;

/**
 * ftper 工厂
 * 
 * @author 736779458@qq.com
 * @version 1.0
 * @updated 06-十一月-2020 15:15:00
 */
public class FtperFactory {

    /**
     * 创建 Ftper
     * 
     * @param ftperConfig
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