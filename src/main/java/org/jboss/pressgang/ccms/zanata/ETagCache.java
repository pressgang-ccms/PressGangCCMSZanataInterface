/*
  Copyright 2011-2014 Red Hat

  This file is part of PressGang CCMS.

  PressGang CCMS is free software: you can redistribute it and/or modify
  it under the terms of the GNU Lesser General Public License as published by
  the Free Software Foundation, either version 3 of the License, or
  (at your option) any later version.

  PressGang CCMS is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU Lesser General Public License for more details.

  You should have received a copy of the GNU Lesser General Public License
  along with PressGang CCMS.  If not, see <http://www.gnu.org/licenses/>.
*/

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
