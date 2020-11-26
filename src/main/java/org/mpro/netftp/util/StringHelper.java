package org.mpro.netftp.util;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class StringHelper {

    public static int countOccurrencesOf(String str, String sub) {
        if (str == null || sub == null || str.length() == 0 || sub.length() == 0) {
            return 0;
        }
        int count = 0;
        int pos = 0;
        int idx;
        while ((idx = str.indexOf(sub, pos)) != -1) {
            ++count;
            pos = idx + sub.length();
        }
        return count;
    }

    public static boolean isBlank(String str) {
        return str == null || str.trim().length() == 0;
    }

    public static boolean isNotBlank(String str) {
        return !isBlank(str);
    }

    public static boolean isEmpty(String str) {
        return str == null || str.length() == 0;
    }

    public static boolean isNotEmpty(String str) {
        return !isEmpty(str);
    }

    public static String[] tokenizeToStringArray(String str, String seperators) {
        if (str == null)
            return new String[0];
        StringTokenizer tokenlizer = new StringTokenizer(str, seperators);
        List<Object> result = new ArrayList<Object>();

        while (tokenlizer.hasMoreElements()) {
            Object s = tokenlizer.nextElement();
            result.add(s);
        }
        return (String[])result.toArray(new String[result.size()]);
    }
}
