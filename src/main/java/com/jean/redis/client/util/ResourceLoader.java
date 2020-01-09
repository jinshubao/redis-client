package com.jean.redis.client.util;

import java.net.URL;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class ResourceLoader {

    private static final Map<String, URL> RESOURCE_URL_CACHE = new ConcurrentHashMap<>();

    public interface Image {

        String redis_logo_24 = "/image/dbs_redis_24px.png";

        String redis_logo_32 = "/image/dbs_redis_32px.png";
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
