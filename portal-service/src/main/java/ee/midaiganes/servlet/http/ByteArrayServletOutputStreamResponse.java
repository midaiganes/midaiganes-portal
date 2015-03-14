package ee.midaiganes.servlet.http;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import ee.midaiganes.servlet.io.ServletOutputStreamImpl;

public class ByteArrayServletOutputStreamResponse extends HttpServletResponseWrapper {

    private final ByteArrayServletOutputStream basos;

    public ByteArrayServletOutputStreamResponse(HttpServletResponse response) {
        super(response);
        basos = new ByteArrayServletOutputStream();
    }

    @Override
    public ServletOutputStream getOutputStream() {
        return basos;
    }

    public static class ByteArrayServletOutputStream extends ServletOutputStreamImpl {
        private final ByteArrayOutputStream baos;

        public ByteArrayServletOutputStream() {
            this(new ByteArrayOutputStream());
        }

        public ByteArrayServletOutputStream(ByteArrayOutputStream baos) {
            super(baos);
            this.baos = baos;
        }

        public String getContentAsString(String encoding) throws UnsupportedEncodingException {
            return baos.toString(encoding);
        }

        public ByteArrayOutputStream getByteArrayOutputStream() {
            return baos;
        }
    }
}
