package ee.midaiganes.io;

import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.ServletOutputStream;

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
}
