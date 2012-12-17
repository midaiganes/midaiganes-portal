package ee.midaiganes.servlet.http;

import java.io.ByteArrayOutputStream;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import ee.midaiganes.io.ServletOutputStreamImpl;

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

	public byte[] getBytes() {
		return basos.getBytes();
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

		public byte[] getBytes() {
			return baos.toByteArray();
		}

		public ByteArrayOutputStream getByteArrayOutputStream() {
			return baos;
		}
	}
}
