package ee.midaiganes.util;

import java.io.IOException;

public class StringEscapeUtil {
    public static <A extends Appendable> A escapeXml(A w, String s) throws IOException {
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c == '<') {
                w.append("&lt;");
            } else if (c == '>') {
                w.append("&gt;");
            } else if (c == '"') {
                w.append("&quot;");
            } else if (c == '&') {
                w.append("&amp;");
            } else if (c == '\'') {
                w.append("&apos;");
            } else {
                w.append(c);
            }
        }
        return w;
    }
}
