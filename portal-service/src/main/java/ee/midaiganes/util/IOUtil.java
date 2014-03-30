package ee.midaiganes.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.io.Writer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.WillClose;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.io.CharStreams;

public class IOUtil {
    private static final Logger log = LoggerFactory.getLogger(IOUtil.class);

    public static String toString(InputStream is, String encoding) throws IOException {
        StringWriter sw = new StringWriter();
        copy(is, sw, encoding);
        return sw.toString();
    }

    public static void close(@WillClose @Nullable AutoCloseable closeable) {
        try {
            if (closeable != null) {
                closeable.close();
            }
        } catch (Exception e) {
            log.debug(e.getMessage(), e);
        }
    }

    private static void copy(InputStream is, @Nonnull Writer out, String encoding) throws IOException {
        try (InputStreamReader reader = new InputStreamReader(is, encoding)) {
            CharStreams.copy(reader, out);
        }
    }

}
