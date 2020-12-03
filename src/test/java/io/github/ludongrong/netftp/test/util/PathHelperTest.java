package io.github.ludongrong.netftp.test.util;

import org.testng.Assert;
import org.testng.annotations.Test;

import io.github.ludongrong.netftp.util.PathHelper;

public class PathHelperTest {

    @Test
    public void formatTest() {
        Assert.assertEquals(PathHelper.format("/").equals("/"), true);
        Assert.assertEquals(PathHelper.format("/////").equals("/"), true);
        Assert.assertEquals(PathHelper.format("/d/").equals("/d"), true);
        Assert.assertEquals(PathHelper.format("d/").equals("d"), true);
    }
}
