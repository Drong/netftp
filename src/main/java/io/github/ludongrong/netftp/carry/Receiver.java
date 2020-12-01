package io.github.ludongrong.netftp.carry;

import java.io.PipedOutputStream;

import cn.hutool.core.io.IoUtil;
import io.github.ludongrong.netftp.IFtper;

public class Receiver {

    private PipedOutputStream out = new PipedOutputStream();

    public PipedOutputStream getOut() {
        return out;
    }

    private IFtper ftper;

    private String dir;

    private String fname;

    public Receiver(IFtper ftper, String dir, String fname) {
        super();
        this.ftper = ftper;
        this.dir = dir;
        this.fname = fname;
    }

    public Boolean call() {
        try {
            return ftper.down(dir, fname, out);
        } finally {
            IoUtil.close(out);
        }
    }
}
