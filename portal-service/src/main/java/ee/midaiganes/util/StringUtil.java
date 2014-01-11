package ee.midaiganes.util;

import java.util.regex.Pattern;

public class StringUtil {
    private static final Pattern NUMBER_PATTERN = Pattern.compile("^[1-9][0-9]*$");

    public static boolean isEmpty(String str) {
        return str == null || StringPool.EMPTY.equals(str.trim());
    }

    public static boolean isNumber(String str) {
        return !isEmpty(str) && NUMBER_PATTERN.matcher(str).matches();
    }

    public static String repeat(String str, String separator, int count) {
        int separatorLength = length(separator);
        if (separatorLength != 0) {
            StringBuilder sb = new StringBuilder(((str.length() + separatorLength) * count) - separatorLength);
            for (int i = 0; i < count; i++) {
                if (i != 0) {
                    sb.append(separator);
                }
                sb.append(str);
            }
            return sb.toString();
        }
        return repeat(str, count);
    }

    public static String repeat(String str, int count) {
        StringBuilder sb = new StringBuilder(str.length() * count);
        for (int i = 0; i < count; i++) {
            sb.append(str);
        }
        return sb.toString();
    }

    public static int length(String str) {
        return str == null ? 0 : str.length();
    }

}
