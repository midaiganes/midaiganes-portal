package ee.midaiganes.servlet.http;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import ee.midaiganes.servlet.http.ByteArrayServletOutputStreamResponse.ByteArrayServletOutputStream;

public class ByteArrayServletOutputStreamAndWriterResponse extends HttpServletResponseWrapper {
	private final ByteArrayServletOutputStream basos;
	private PrintWriter writer;
	private boolean outputStreamCalled = false;
	private boolean writerCalled = false;

	public ByteArrayServletOutputStreamAndWriterResponse(HttpServletResponse response) {
		super(response);
		basos = new ByteArrayServletOutputStream();
	}

	@Override
	public ServletOutputStream getOutputStream() throws IOException {
		if (writerCalled) {
			throw new IllegalStateException("getWriter already called");
		}
		outputStreamCalled = true;
		return basos;
	}

	@Override
	public PrintWriter getWriter() throws IOException {
		if (outputStreamCalled) {
			throw new IllegalStateException("getOutputStream already called");
		}
		if (!writerCalled) {
			initWriter();
		}
		writerCalled = true;
		return writer;
	}

	public byte[] getBytes() {
		if (writer != null) {
			writer.close();
		}
		return basos.getBytes();
	}

	private void initWriter() throws IOException {
		try {
			writer = new PrintWriter(getOutputStreamWriter(), true);
		} catch (UnsupportedEncodingException e) {
			throw new IOException(e);
		}
	}

	private OutputStreamWriter getOutputStreamWriter() throws UnsupportedEncodingException {
		return new OutputStreamWriter(basos, getCharacterEncoding());
	}
}
