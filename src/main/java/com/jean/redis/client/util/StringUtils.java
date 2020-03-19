package com.jean.redis.client.util;

import com.jean.redis.client.constant.CommonConstant;

import java.nio.charset.Charset;
import java.util.Collection;
import java.util.Iterator;

public final class StringUtils {

    private StringUtils() {
    }

    public static boolean isEmpty(final CharSequence value) {
        return value == null || value.length() == 0;
    }

    public static boolean isNotEmpty(final CharSequence cs) {
        return !isEmpty(cs);
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

    public static String byteArrayToString(byte[] bytes) {
        return new String(bytes, CommonConstant.CHARSET_UTF8);
    }

    public static String byteArrayToString(byte[] bytes, Charset charset) {
        return new String(bytes, charset);
    }
}
