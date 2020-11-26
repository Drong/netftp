package org.mpro.netftp;

/**
 * 获取文件列表时抛出的异常
 *
 * @author 736779458@qq.com
 * @version 1.0
 * @date 2020年11月25日 下午3:12:47
 */
public class LsException extends Exception {

    private static final long serialVersionUID = 3292535034178877408L;

    public LsException(String message, Throwable cause) {
        super(message, cause);
    }

    public LsException(String message) {
        super(message);
    }
}
