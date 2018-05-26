package com.zero.common.utils;

import com.google.common.collect.Maps;
import com.zero.common.enmu.EnumDict;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.Optional;

public class EnumUtils {
    private static final Logger LOGGER = LogManager.getLogger(EnumUtils.class);

    public static Map<Integer, String> getEnum(String en) {
        EnumDict[] enumValues = new EnumDict[0];
        try {
            Class<?> statusClass = Class.forName(en);
            Method valueOfMethod = statusClass.getMethod("values");
            enumValues = (EnumDict[]) valueOfMethod.invoke(null);
        } catch (Exception e) {
            LOGGER.error("获取枚举类异常:", e);
            return Maps.newHashMap();
        }
        return enumValues[0].getDict();
    }

    public static String getValue(String en, int key){
        return Optional.ofNullable(getEnum(en).get(key)).orElse(key + "");
    }
}
