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
    public static void write(Object object, String filePath) throws FileNotFoundException {
        File file = new File(filePath);
        Yaml.dump(object, file);
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
        File dumpFile = new File(filePath);
        return Yaml.loadType(dumpFile, clazz);
    }
}
