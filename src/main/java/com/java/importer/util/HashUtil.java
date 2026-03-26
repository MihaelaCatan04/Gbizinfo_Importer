package com.java.importer.util;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public final class HashUtil {

    private static final String NULL_TOKEN = "<NULL>";

    private HashUtil() {
    }

    public static String md5(String raw) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(raw.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder(digest.length * 2);
            for (byte b : digest) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("MD5 algorithm not available", e);
        }
    }

    public static String mergeKeyOrNull(String... parts) {
        if (parts == null || parts.length == 0) {
            return null;
        }

        boolean allNullTokens = Arrays.stream(parts)
                .allMatch(HashUtil::isNullToken);

        if (allNullTokens) {
            return null;
        }

        return md5(String.join("|", parts));
    }

    private static boolean isNullToken(String value) {
        return value == null || NULL_TOKEN.equals(value);
    }

    public static String normText(String value) {
        if (value == null) {
            return NULL_TOKEN;
        }
        String normalized = value.trim().replaceAll("\\s+", " ");
        return normalized.isEmpty() ? NULL_TOKEN : normalized;
    }

    public static String normInt(Integer value) {
        return value == null ? NULL_TOKEN : value.toString();
    }

    public static String normLong(Long value) {
        return value == null ? NULL_TOKEN : value.toString();
    }

    public static String normNumber(Number value) {
        if (value == null) {
            return NULL_TOKEN;
        }
        if (value instanceof BigDecimal bd) {
            return bd.stripTrailingZeros().toPlainString();
        }
        if (value instanceof Double || value instanceof Float) {
            return BigDecimal.valueOf(value.doubleValue()).stripTrailingZeros().toPlainString();
        }
        return String.valueOf(value);
    }

    public static String normDate(String value) {
        return normText(value);
    }

    public static String normTimestamp(String value) {
        return normText(value);
    }

    public static String normBool(boolean value) {
        return Boolean.toString(value);
    }
}