package com.jean.redis.client.util;

import java.util.Collection;
import java.util.Iterator;
import java.util.StringTokenizer;

public abstract class StringUtils {

    public static boolean isEmpty(final CharSequence value) {
        return value == null || value.length() == 0;
    }

    public static boolean isNotEmpty(final CharSequence cs) {
        return !isEmpty(cs);
    }

    public static String[] splitString(String string, String s) {
        StringTokenizer tokenizer = new StringTokenizer(string, s);
        String[] strings = new String[tokenizer.countTokens()];
        for (int i = 0; i < strings.length; ++i) {
            strings[i] = tokenizer.nextToken();
        }
        return strings;
    }

    public static String trimWhitespace(String string) {
        if (string == null) {
            return null;
        }
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < string.length(); ++i) {
            char at = string.charAt(i);
            if (at != '\n' && at != '\f' && at != '\r' && at != '\t') {
                builder.append(at);
            }
        }
        return builder.toString().trim();
    }

    public static String join(Collection<?> collection, String s) {
        StringBuilder builder = new StringBuilder();
        for (Iterator<?> iterator = collection.iterator(); iterator.hasNext(); builder.append(iterator.next())) {
            if (builder.length() != 0) {
                builder.append(s);
            }
        }
        return builder.toString();
    }
}
