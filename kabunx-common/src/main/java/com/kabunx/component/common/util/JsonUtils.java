package com.kabunx.component.common.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.kabunx.component.common.exception.JsonException;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

/**
 * json序列化与反序列工具类
 */
@Slf4j
public class JsonUtils {
    /**
     * ObjectMapper提供了读取和写入JSON的功能
     */
    private static final ObjectMapper objectMapper = new ObjectMapper();

    static {
        // 不存在的属性，不转化，否则报错
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        // LocalDateTime序列化和反序列化
        JavaTimeModule javaTimeModule = new JavaTimeModule();
        javaTimeModule.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        javaTimeModule.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        objectMapper.registerModule(javaTimeModule);
    }

    public static String object2Json(Object obj) throws JsonException {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            log.error("[JsonUtils] 序列化异常，object is {}", obj, e);
            throw new JsonException("序列化异常");
        }
    }

    public static byte[] object2JsonBytes(Object obj) throws JsonException {
        String json = object2Json(obj);
        return json.getBytes(StandardCharsets.UTF_8);
    }

    public static TypeFactory getTypeFactory() {
        return objectMapper.getTypeFactory();
    }

    public static Object json2Object(String json) {
        try {
            return objectMapper.readValue(json, Object.class);
        } catch (Exception e) {
            log.error("[JsonUtils] 反序列化异常，json is {}", json, e);
            throw new JsonException("反序列化异常");
        }
    }

    /**
     * 将JSON字符串反序列化为对象
     */
    public static <T> T json2Object(String json, Class<T> tClass) throws JsonException {
        try {
            return objectMapper.readValue(json, tClass);
        } catch (Exception e) {
            log.error("[JsonUtils] 反序列化异常，json is {}", json, e);
            throw new JsonException("反序列化异常");
        }
    }

    /**
     * 将JSON字符串转换为复杂类型的对象
     */
    public static <T> T json2Object(String json, TypeReference<T> typeReference) throws JsonException {
        try {
            return objectMapper.readValue(json, typeReference);
        } catch (Exception e) {
            log.error("[JsonUtils] 反序列化异常，json is {}", json, e);
            throw new JsonException("反序列化异常");
        }
    }

    /**
     * 将JSON字符串转换为list对象
     */
    public static <T> List<T> json2List(String json, Class<T> tClass) throws JsonException {
        try {
            JavaType javaType = getTypeFactory().constructParametricType(List.class, tClass);
            return objectMapper.readValue(json, javaType);
        } catch (Exception e) {
            log.error("[JsonUtils] 反序列化异常，json is {}", json, e);
            throw new JsonException("反序列化异常");
        }
    }

    /**
     * 将JSON字符串转换为Map对象
     */
    public static <K, V> Map<K, V> json2Map(String json, Class<K> kClass, Class<V> vClass) throws JsonException {
        try {
            JavaType javaType = getTypeFactory().constructParametricType(Map.class, kClass, vClass);
            return objectMapper.readValue(json, javaType);
        } catch (Exception e) {
            log.error("[JsonUtils] 反序列化异常，json is {}", json, e);
            throw new JsonException("反序列化异常");
        }
    }

    /**
     * 将对象序列化后再反序列化为目标对象
     */
    public static <T> T copy(Object obj, Class<T> tClass) throws JsonException {
        return json2Object(object2Json(obj), tClass);
    }

}
