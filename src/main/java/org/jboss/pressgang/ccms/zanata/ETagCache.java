package org.jboss.pressgang.ccms.zanata;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ETagCache implements Serializable {
    private static final long serialVersionUID = 2495727787967967616L;
    protected Map<String, Map<String, String>> cache;

    public ETagCache() {
        cache = new ConcurrentHashMap<String, Map<String, String>>();
    }

    public void load(final File cacheFile) throws IOException {
        if (cacheFile.exists() && cacheFile.canRead()) {
            ObjectInputStream ois = null;
            try {
                ois = new ObjectInputStream(new FileInputStream(cacheFile));
                cache = (Map<String, Map<String, String>>) ois.readObject();
            } catch (ClassNotFoundException e) {
                // Ignore this exception since it shouldn't happen since we are only using core classes
            } finally {
                if (ois != null) {
                    ois.close();
                }
            }
        }
    }

    public void save(final File cacheFile) throws IOException {
        // Create the file if it doesn't exist
        if (!cacheFile.exists()) {
            final File parent = cacheFile.getParentFile();
            if (parent != null) {
                parent.mkdirs();
            }
            cacheFile.createNewFile();
        }

        // Save the cache
        ObjectOutputStream oos = null;
        try {
            oos = new ObjectOutputStream(new FileOutputStream(cacheFile));
            oos.writeObject(cache);
            oos.flush();
        } finally {
            if (oos != null) {
                oos.close();
            }
        }
    }

    public void put(final String uri, final String etag, final String contentType) {
        Map<String, String> map = cache.get(uri);
        if (map == null) {
            map = new ConcurrentHashMap<String, String>();
            cache.put(uri, map);
        }
        map.put(contentType, etag);
    }

    public String getAny(final String uri) {
        Map<String, String> map = cache.get(uri);
        if (map != null) {
            Iterator<String> iterator = map.values().iterator();
            if (iterator.hasNext()) return iterator.next();
        }

        return null;
    }

    public String get(final String uri, final String contentType) {
        Map<String, String> map = cache.get(uri);
        if (map != null) {
            return map.get(contentType);
        } else {
            return null;
        }
    }

    public void invalidate(final String uri) {
        cache.remove(uri);
    }

    public void invalidateAll() {
        cache.clear();
    }
}
