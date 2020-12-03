package io.github.ludongrong.netftp;

import lombok.Getter;
import lombok.Setter;

/**
 * 异常.
 *
 * @author <a href="mailto:736779458@qq.com">ludongrong</a>
 * @since 2020-12-02
 */
public class FtperException extends Exception {

    private static final long serialVersionUID = 5626644047113997653L;

    static final public String CONNECT_CODE = "404";

    static final public String CONNECT_DESCRIPTION = "Unable to connect; host[{0}] user[{1}] ";

    static final public String LOGIN_CODE = "300";

    static final public String LOGIN_DESCRIPTION = "Unable to login; host[{0}] user[{1}]";

    static final public String IO_CODE = "350";

    static final public String IO_DESCRIPTION = "Unable IO operation; host[{0}] user[{1}]";

    static final public String CONFIG_CODE = "355";

    static final public String CONFIG_DESCRIPTION = "Unable to configure; host[{0}] user[{1}]";
    
    static final public String NOTFIND_CODE = "99";

    static final public String NOTFIND_DESCRIPTION = "Cannot find; host[{0}] user[{1}]";

    @Getter
    @Setter
    private String code;

    public FtperException(String code, String description) {
        super(description);
        this.code = code;
    }
}