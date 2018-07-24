package com.jean.redis.client.utils;


import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;

/**
 *
 * @author jinshubao
 * @date 2016/11/29
 */
public class YamlUtils {

    /**
     * 写配置文件
     *
     * @param object
     * @param filePath
     * @param fileName
     * @throws IOException
     */
    public static void write(Object object, String filePath, String fileName) throws IOException {
        File file = new File(filePath, fileName);
        File parent = file.getParentFile();
        if (!parent.exists()) {
            parent.mkdirs();
        }
        FileWriter fileWriter = new FileWriter(file);
        new Yaml().dump(object,fileWriter);
        fileWriter.close();
    }

    /**
     * 读配置文件
     *
     * @param filePath
     * @param clazz
     * @param <T>
     * @return
     * @throws IOException
     */
    public static <T> T read(String filePath, Class<T> clazz) throws IOException {
        File file = new File(filePath);
        if (file.exists()) {
            FileInputStream is = new FileInputStream(file);
            T load = new Yaml().loadAs(is, clazz);
            is.close();
            return load;
        }
        return null;
    }
}
