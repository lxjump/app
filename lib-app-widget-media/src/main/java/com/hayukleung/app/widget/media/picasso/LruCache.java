/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hayukleung.app.widget.media.picasso;

import android.content.Context;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import static com.hayukleung.app.widget.media.picasso.Utils.KEY_SEPARATOR;

/**
 * A memory cache which uses a least-recently used eviction policy.
 */
public class LruCache implements Cache {
    final LinkedHashMap<String, IImage> map;
    private final int maxSize;

    private int size;
    private int putCount;
    private int evictionCount;
    private int hitCount;
    private int missCount;

    /**
     * Create a cache using an appropriate portion of the available RAM as the maximum size.
     */
    public LruCache(Context context) {
        this(Utils.calculateMemoryCacheSize(context));
    }

    /**
     * Create a cache with a given maximum size in bytes.
     */
    public LruCache(int maxSize) {
        if (maxSize <= 0) {
            throw new IllegalArgumentException("Max size must be positive.");
        }
        this.maxSize = maxSize;
        this.map = new LinkedHashMap<String, IImage>(0, 0.75f, true);
    }

    @Override
    public IImage get(String key) {
        if (key == null) {
            throw new NullPointerException("key == null");
        }

        IImage mapValue;
        synchronized (this) {
            mapValue = map.get(key);
            if (mapValue != null) {
                hitCount++;
                return mapValue;
            }
            missCount++;
        }

        return null;
    }

    @Override
    public void set(String key, IImage image) {
        if (key == null || image == null) {
            throw new NullPointerException("key == null || bitmap == null");
        }

        IImage previous;
        synchronized (this) {
            putCount++;
            size += image.getSize();
            previous = map.put(key, image);
            if (previous != null) {
                size -= previous.getSize();
            }
        }

        trimToSize(maxSize);
    }

    private void trimToSize(int maxSize) {
        while (true) {
            String key;
            IImage value;
            synchronized (this) {
                if (size < 0 || (map.isEmpty() && size != 0)) {
                    throw new IllegalStateException(
                            getClass().getName() + ".sizeOf() is reporting inconsistent results!");
                }

                if (size <= maxSize || map.isEmpty()) {
                    break;
                }

                Map.Entry<String, IImage> toEvict = map.entrySet().iterator().next();
                key = toEvict.getKey();
                value = toEvict.getValue();
                map.remove(key);
                size -= value.getSize();
                evictionCount++;
            }
        }
    }

    /**
     * Clear the cache.
     */
    public final void evictAll() {
        trimToSize(-1); // -1 will evict 0-sized elements
    }

    @Override
    public final synchronized int size() {
        return size;
    }

    @Override
    public final synchronized int maxSize() {
        return maxSize;
    }

    @Override
    public final synchronized void clear() {
        evictAll();
    }

    @Override
    public final synchronized void clearKeyUri(String uri) {
        int uriLength = uri.length();
        for (Iterator<Map.Entry<String, IImage>> i = map.entrySet().iterator(); i.hasNext(); ) {
            String key = i.next().getKey();
            int newlineIndex = key.indexOf(KEY_SEPARATOR);
            if (newlineIndex == uriLength && key.substring(0, newlineIndex).equals(uri)) {
                i.remove();
            }
        }
    }

    /**
     * Returns the number of times {@link #get} returned a value.
     */
    public final synchronized int hitCount() {
        return hitCount;
    }

    /**
     * Returns the number of times {@link #get} returned {@code null}.
     */
    public final synchronized int missCount() {
        return missCount;
    }

    /**
     * Returns the number of times {@link #set(String, IImage)} was called.
     */
    public final synchronized int putCount() {
        return putCount;
    }

    /**
     * Returns the number of values that have been evicted.
     */
    public final synchronized int evictionCount() {
        return evictionCount;
    }
}
