package io.github.ludongrong.netftp.util;

public class ArraysUtil {

    public static <T> T toEnum(int i, T[] enums) {

        if (i >= enums.length || i < 0) {
            throw new IllegalArgumentException("The type must less " + enums.length + " and more zero.");
        }

        return enums[i];
    }
}
