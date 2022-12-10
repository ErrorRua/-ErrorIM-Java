package com.errorim.util;

import com.errorim.annotation.MapIgnore;
import org.springframework.beans.BeanUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author ErrorRua
 * @date 2022/11/19
 * @description:
 */
public class BeanCopyUtils {

    private BeanCopyUtils() {
    }

    public static <V> V copyBean(Object source, Class<V> clazz) {
        //创建目标对象
        V result = null;
        try {
            result = clazz.newInstance();
            //实现属性copy
            BeanUtils.copyProperties(source, result);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //返回结果
        return result;
    }

    public static <O, V> List<V> copyBeanList(List<O> list, Class<V> clazz) {
        return list.stream()
                .map(o -> copyBean(o, clazz))
                .collect(Collectors.toList());
    }

    // 将对象转换为map
    public static Map<String, Object> beanToMap(Object obj) {
        Map<String, Object> map = new HashMap<>();
        if (obj == null) {
            return map;
        }
        Arrays.stream(obj.getClass().getDeclaredFields()).filter(field -> !field.isAnnotationPresent(MapIgnore.class)).forEach(field -> {
            try {
                field.setAccessible(true);
                map.put(field.getName(), field.get(obj));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        });
        return map;
    }

    // 将map转换为对象
    public static <T> T mapToBean(Map<String, Object> map, Class<T> clazz) {
        T obj = null;
        if (map == null) {
            return obj;
        }
        try {
            obj = clazz.newInstance();
            T finalObj = obj;
            Arrays.stream(obj.getClass().getDeclaredFields()).filter(field -> !field.isAnnotationPresent(MapIgnore.class)).forEach(field -> {
                try {
                    field.setAccessible(true);
                    field.set(finalObj, map.get(field.getName()));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return obj;
    }
}

