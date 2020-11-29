package io.github.ludongrong.netftp.test;

import org.testng.annotations.DataProvider;

/**
 * xxx.
 *
 * @author <a href="mailto:736779458@qq.com">ludongrong</a>
 * @since 2020-11-27
 */
public class FtperFactoryProvider {

    @DataProvider(name = "dirData")
    public Object[][] dirData() {
        return new Object[][] {new Object[] {"/data/我factory"}, new Object[] {"/data/我factory/1/cdmkdir"},
            new Object[] {"/data/我factory/1/a"}, new Object[] {"/data/我factory"}};
    }

}