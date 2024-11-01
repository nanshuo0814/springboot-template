package icu.nanshuo.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;

/**
 * json 序列化工具类
 *
 * @author <a href="https://github.com/nanshuo0814">nanshuo(南烁)</a>
 * @date 2024/07/26
 */
@Slf4j
public class JsonUtils {

    public static final ObjectMapper OBJ_MAPPER = new ObjectMapper();

    /**
     * 普通对象之间类型的转化
     *
     * @param source 原对象
     * @param target 目标类型
     * @param <T>    目标参数类型
     * @return object after transformation
     */
    public static <T> T objParse(Object source, Class<T> target) {
        try {
            if (source.getClass().equals(target)) {
                return OBJ_MAPPER.convertValue(source, target);
            }
        } catch (Exception ignored) {
        }
        return null;
    }

    /**
     * 普通列表之间类型的转化
     *
     * @param source 原列表
     * @param target 目标列表类型
     * @param <T>    目标列表参数类型
     * @return list after transformation
     */
    public static <S, T> List<T> listParse(List<S> source, Class<T> target) {
        try {
            return OBJ_MAPPER.convertValue(source, OBJ_MAPPER.getTypeFactory().constructCollectionType(List.class, target));
        } catch (Exception e) {
            log.error("listParse error: {}", e.getMessage());
        }
        return null;
    }


    /**
     * 普通哈希列表之间类型的转化
     *
     * @param source    原哈希列表
     * @param keyType   键类型
     * @param valueType 值类型
     * @param <SK>      原键类型
     * @param <SV>      原值类型
     * @param <TK>      目标键类型
     * @param <TV>      目标值类型
     * @return map after transformation
     */
    public static <SK, SV, TK, TV> Map<TK, TV> mapParse(Map<SK, SV> source, Class<TK> keyType, Class<TV> valueType) {
        try {
            return OBJ_MAPPER.convertValue(source, OBJ_MAPPER.getTypeFactory().constructMapType(Map.class, keyType, valueType));
        } catch (Exception e) {
            log.error("mapParse error: {}", e.getMessage());
        }
        return null;
    }

    /**
     * 将对象转换成json字符串(序列化)
     *
     * @param obj 原对象
     * @return string after serialized object
     */
    public static String objToJson(Object obj) {
        if (obj == null) {
            return null;
        }
        if (obj instanceof String) {
            return (String) obj;
        }
        try {
            return OBJ_MAPPER.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            log.error("objToJson error: {}", e.getMessage());
            return "";
        }
    }


    /**
     * 将json转化为对象(反序列化)
     *
     * @param source 原对象json
     * @param target 目标类型
     * @param <T>    目标类参数类型
     * @return deserialized object
     */
    public static <T> T jsonToObj(String source, Class<T> target) {
        OBJ_MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        try {
            return OBJ_MAPPER.readValue(source, target);
        } catch (Exception e) {
            log.error("jsonToObj error: {}", e.getMessage());
        }
        return null;
    }

    /**
     * 将列表json转化为对象集合
     *
     * @param source 原对象json
     * @param target 目标类型
     * @param <T>    目标类参数类型
     * @return deserialized object collection
     */
    public static <T> List<T> jsonToList(String source, Class<T> target) {
        try {
            return OBJ_MAPPER.readValue(source, OBJ_MAPPER.getTypeFactory().constructCollectionType(List.class, target));
        } catch (Exception e) {
            log.error("jsonToList error: {}", e.getMessage());
        }
        return null;
    }


    /**
     * 将哈希表json转化为对象集合
     *
     * @param source    原对象json
     * @param keyType   键类型
     * @param valueType 值类型
     * @param <K>       键参数类型
     * @param <V>       值参数类型
     * @return deserialized map collection
     */
    public static <K, V> Map<K, V> jsonToMap(String source, Class<K> keyType, Class<V> valueType) {
        try {
            return OBJ_MAPPER.readValue(source, OBJ_MAPPER.getTypeFactory().constructMapType(Map.class, keyType, valueType));
        } catch (Exception e) {
            log.error("jsonToMap error: {}", e.getMessage());
        }
        return null;
    }


}


