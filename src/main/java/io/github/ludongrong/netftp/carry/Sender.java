package io.github.ludongrong.netftp.carry;

import java.io.PipedInputStream;
import java.util.concurrent.Callable;

import cn.hutool.core.io.IoUtil;
import io.github.ludongrong.netftp.IFtper;

public class Sender implements Callable<Boolean> {

    private PipedInputStream in = new PipedInputStream();

    public PipedInputStream getIn() {
        return in;
    }

    private IFtper ftper;

    private String dir;

    private String fname;

    public Sender(IFtper ftper, String dir, String fname) {
        super();
        this.ftper = ftper;
        this.dir = dir;
        this.fname = fname;
    }

    @Override
    public Boolean call() throws Exception {
        try {
            return ftper.upload(dir, fname, in);
        } finally {
            IoUtil.close(in);
        }
    }
}
