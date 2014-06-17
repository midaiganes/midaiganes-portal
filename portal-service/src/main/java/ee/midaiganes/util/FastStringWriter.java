package ee.midaiganes.util;

import java.io.IOException;
import java.io.Writer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FastStringWriter extends Writer {
    private static final Logger log = LoggerFactory.getLogger(FastStringWriter.class);
    private final StringBuilder sb = new StringBuilder(1024);

    @Override
    public void close() throws IOException {
        log.debug("close ignored");
    }

    @Override
    public void flush() throws IOException {
        log.debug("flush ignored");
    }

    @Override
    public void write(char[] cbuf, int off, int len) throws IOException {
        sb.append(cbuf, off, len);
    }

    @Override
    public void write(String s) {
        sb.append(s);
    }

    @Override
    public Writer append(char c) {
        sb.append(c);
        return this;
    }

    @Override
    public Writer append(CharSequence csq) {
        sb.append(csq);
        return this;
    }

    public String getString() {
        return sb.toString();
    }
}
