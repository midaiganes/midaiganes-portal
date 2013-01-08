package ee.midaiganes.util;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IOUtil {
	private static final Logger log = LoggerFactory.getLogger(IOUtil.class);

	public static String toString(InputStream is, String encoding) throws IOException {
		StringWriter sw = new StringWriter();
		copy(is, sw, encoding);
		return sw.toString();
	}

	public static long copy(InputStream is, OutputStream os) throws IOException {
		byte[] buf = new byte[8192];
		int n = 0;
		long count = 0;
		while (-1 != (n = is.read(buf))) {
			os.write(buf, 0, n);
			count += n;
		}
		return count;
	}

	public static void close(Closeable closeable) {
		try {
			if (closeable != null) {
				closeable.close();
			}
		} catch (IOException e) {
			log.debug(e.getMessage(), e);
		}
	}

	private static void copy(InputStream is, Writer out, String encoding) throws IOException {
		try (InputStreamReader reader = new InputStreamReader(is, encoding)) {
			copy(reader, out);
		}
	}

	private static long copy(Reader input, Writer out) throws IOException {
		char[] buf = new char[1024];
		long count = 0;
		int n = 0;
		while (-1 != (n = input.read(buf))) {
			out.write(buf, 0, n);
			count += n;
		}
		return count;
	}

}
