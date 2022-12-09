package com.kabunx.component.common.util;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * 通用IO操作工具类
 */
public class IOUtils {

    public static void closeQuietly(java.io.Closeable o) {
        if (Objects.isNull(o)) {
            return;
        }
        try {
            o.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String loadJarFile(ClassLoader loader, String resourceName) {
        InputStream in = loader.getResourceAsStream(resourceName);
        if (Objects.isNull(in)) {
            return null;
        }
        String out = null;
        try {
            int len = in.available();
            byte[] bytes = new byte[len];

            int readLength = in.read(bytes);
            if ((long) readLength < len) {
                throw new IOException("文件不完整");
            }
            out = new String(bytes, StandardCharsets.UTF_8);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeQuietly(in);
        }
        return out;
    }
}
