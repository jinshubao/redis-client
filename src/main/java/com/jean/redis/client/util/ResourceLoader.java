package com.jean.redis.client.util;

import java.net.URL;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class ResourceLoader {

    private static final Map<String, URL> RESOURCE_URL_CACHE = new ConcurrentHashMap<>();

    public interface Image {

        String redis_logo_24 = "/image/redis_logo_24.png";
        String redis_logo_32 = "/image/redis_logo_32.png";

        String add_16 = "/image/x16/add.png";
        String delete_16 = "/image/x16/shanchu.png";
        String refresh_16 = "/image/x16/xuexiao.png";
        String connect_16 = "/image/x16/lianjie.png";
        String disconnect_16 = "/image/x16/guanlianduankai.png";
        String server_16 = "/image/x16/shujuku.png";

        String save_32 = "/image/save-32.png";


    }


    /**
     * 获取类路径下的资源URL
     *
     * @param resource
     * @return
     */
    public static URL load(String resource) {
        if (!RESOURCE_URL_CACHE.containsKey(resource)) {
            URL url = ResourceLoader.class.getResource(resource);
            RESOURCE_URL_CACHE.put(resource, url);
        }
        return RESOURCE_URL_CACHE.get(resource);
    }
}
