package ee.midaiganes.servlet.io;

import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;

public class ServletOutputStreamImpl extends ServletOutputStream {
    private final OutputStream out;

    public ServletOutputStreamImpl(OutputStream out) {
        this.out = out;
    }

    @Override
    public void write(int b) throws IOException {
        out.write(b);
    }

    @Override
    public void write(byte[] bytes) throws IOException {
        out.write(bytes);
    }

    @Override
    public void write(byte[] bytes, int off, int len) throws IOException {
        out.write(bytes, off, len);
    }

    @Override
    public boolean isReady() {
        throw new RuntimeException("not implemented");// TODO
    }

    @Override
    public void setWriteListener(WriteListener writeListener) {
        throw new RuntimeException("not implemented");// TODO
    }
}
