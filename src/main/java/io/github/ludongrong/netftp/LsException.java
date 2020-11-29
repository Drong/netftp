package io.github.ludongrong.netftp;

/**
 * 获取列表异常.
 *
 * @author <a href="mailto:736779458@qq.com">ludongrong</a>
 * @since 2020-11-27
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
