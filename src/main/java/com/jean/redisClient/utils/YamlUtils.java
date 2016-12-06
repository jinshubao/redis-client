package com.jean.redisClient.utils;

import org.ho.yaml.Yaml;

import java.io.File;
import java.io.FileNotFoundException;

/**
 * Created by jinshubao on 2016/11/29.
 */
public class YamlUtils {

    /**
     * 写配置文件
     *
     * @param object
     * @param filePath
     * @throws FileNotFoundException
     */
    public static void write(Object object, String filePath, String fileName) throws FileNotFoundException {
        File file = new File(filePath);
        if (!file.exists()) {
            file.mkdirs();
        }
        Yaml.dump(object, new File(filePath + fileName));
    }

    /**
     * 读配置文件
     *
     * @param filePath
     * @param clazz
     * @param <T>
     * @return
     * @throws FileNotFoundException
     */
    public static <T> T read(String filePath, Class<T> clazz) throws FileNotFoundException {
        File file = new File(filePath);
        if (!file.exists()) {
            return null;
        }
        return Yaml.loadType(file, clazz);
    }
}
