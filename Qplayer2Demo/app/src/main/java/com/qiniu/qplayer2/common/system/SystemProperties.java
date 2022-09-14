
package com.qiniu.qplayer2.common.system;

import androidx.annotation.NonNull;


import com.qiniu.qplayer2.common.type.StringUtils;

import java.lang.reflect.Method;

/**
 * Helper for {@code android.os.SystemProperties}
 *
 * @author yrom
 */
public class SystemProperties {
    private static final Method getStringProperty = getMethod(getClass("android.os.SystemProperties"));

    private static Class<?> getClass(String name) {
        try {
            Class<?> cls = Class.forName(name);
            if (cls == null) {
                throw new ClassNotFoundException();
            }
            return cls;
        } catch (ClassNotFoundException e) {
            try {
                return ClassLoader.getSystemClassLoader().loadClass(name);
            } catch (ClassNotFoundException e1) {
                return null;
            }
        }
    }

    private static Method getMethod(Class<?> clz) {
        if (clz == null) return null;
        try {
            return clz.getMethod("get", String.class);
        } catch (Exception e) {
            return null;
        }
    }

    @NonNull
    public static String get(String key) {
        if (getStringProperty != null) {
            try {
                Object value = getStringProperty.invoke(null, key);
                if (value == null) {
                    return StringUtils.EMPTY;
                }
                return StringUtils.trimToEmpty(value.toString());
            } catch (Exception ignored) {
            }
        }
        return StringUtils.EMPTY;
    }

    public static String get(String key, String def) {
        if (getStringProperty != null) {
            try {
                String value = (String) getStringProperty.invoke(null, key);
                return StringUtils.defaultString(StringUtils.trimToNull(value), def);
            } catch (Exception ignored) {
            }
        }
        return def;
    }
}
