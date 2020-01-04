package com.jean.redis.client.util;

import java.io.InputStream;

public abstract class ResourceLoader {

    /**
     * 加载类路径下的资源
     *
     * @param resource
     * @return
     */
    public static InputStream loadAsStream(String resource) {
        return ResourceLoader.class.getResourceAsStream(resource);
    }
}
