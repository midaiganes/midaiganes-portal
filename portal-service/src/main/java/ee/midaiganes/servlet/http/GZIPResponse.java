package ee.midaiganes.servlet.http;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.zip.GZIPOutputStream;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ee.midaiganes.servlet.io.ServletOutputStreamImpl;

public class GZIPResponse extends HttpServletResponseWrapper {
    private static final Logger log = LoggerFactory.getLogger(GZIPResponse.class);
    private boolean outputCalled;
    private boolean writerCalled;
    private ServletOutputStream os;
    private PrintWriter pw;
    private GZIPOutputStream gos;
    private boolean sendRedirectCalled;

    public GZIPResponse(HttpServletResponse response) {
        super(response);
    }

    @Override
    public void sendRedirect(String location) throws IOException {
        super.sendRedirect(location);
        sendRedirectCalled = true;
    }

    @Override
    public boolean isCommitted() {
        return sendRedirectCalled || super.isCommitted();
    }

    @Override
    public void flushBuffer() throws IOException {
        flushBuffer(false);
    }

    public void flushBuffer(boolean finish) throws IOException {
        log.debug("flushBuffer; finish = {}; sendRedirectCalled = {}", Boolean.valueOf(finish), Boolean.valueOf(sendRedirectCalled));
        if (!sendRedirectCalled) {
            if (writerCalled) {
                pw.flush();
            }
            if (gos != null) {
                if (finish) {
                    gos.finish();
                } else {
                    gos.flush();
                }
            }
            super.flushBuffer();
        }
    }

    @Override
    public ServletOutputStream getOutputStream() throws IOException {
        if (writerCalled) {
            throw new IllegalStateException();
        }
        if (!outputCalled) {
            initOutputStream();
            outputCalled = true;
        }
        return os;
    }

    @Override
    public PrintWriter getWriter() throws IOException {
        if (outputCalled) {
            throw new IllegalStateException();
        }
        if (!writerCalled) {
            initWriter();
            writerCalled = true;
        }
        return pw;
    }

    private void initOutputStream() throws IOException {
        initGZIPOutputStream();
        os = new ServletOutputStreamImpl(gos);
    }

    private void initWriter() throws IOException {
        initGZIPOutputStream();
        String encoding = super.getCharacterEncoding();
        log.debug("encoding = '{}'", encoding);
        pw = new PrintWriter(new OutputStreamWriter(gos, encoding), false);
    }

    private void initGZIPOutputStream() throws IOException {
        gos = new GZIPOutputStream(super.getOutputStream(), 1024 * 1024);
    }
}