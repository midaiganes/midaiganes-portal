package ee.midaiganes.servlet.http;

import java.io.Closeable;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

public class WrappedOutputHttpServletResponse extends HttpServletResponseWrapper implements Closeable {
    private final PrintWriter printWriter;

    public WrappedOutputHttpServletResponse(HttpServletResponse response, Writer writer) {
        super(response);
        printWriter = new PrintWriter(writer);
    }

    @Override
    public PrintWriter getWriter() {
        return printWriter;
    }

    @Override
    public ServletOutputStream getOutputStream() throws IOException {
        throw new IOException();
    }

    @Override
    public void flushBuffer() throws IOException {
        printWriter.flush();
        super.flushBuffer();
    }

    @Override
    public void close() throws IOException {
        flushBuffer();
    }
}
