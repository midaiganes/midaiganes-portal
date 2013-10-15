package ee.midaiganes.io;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;

public class UnsyncByteArrayOutputStream extends OutputStream {
	private byte[] bytes;
	private int size;

	public UnsyncByteArrayOutputStream() {
		this(1024);
	}

	public UnsyncByteArrayOutputStream(int bufferSize) {
		this.bytes = new byte[bufferSize];
		this.size = 0;
	}

	@Override
	public void write(int b) throws IOException {
		if (size + 1 > bytes.length) {
			int n = bytes.length << 1;
			if (n < 0) {
				n = bytes.length + 1;
			}
			bytes = Arrays.copyOf(bytes, n);
		}
		bytes[size++] = (byte) b;
	}

	@Override
	public void write(byte b[], int off, int len) throws IOException {
		if (size + len > bytes.length) {
			int n = bytes.length << 1;
			if (n < 0 || n - (size + len) < 0) {
				n = bytes.length + len;
			}
			bytes = Arrays.copyOf(bytes, n);
		}
		System.arraycopy(b, off, bytes, size, len);
		size += len;
	}

	public byte[] getBytes() {
		return Arrays.copyOf(bytes, size);
	}

	public byte[] getAllBytes() {
		return bytes;
	}

	public int getSize() {
		return size;
	}
}
