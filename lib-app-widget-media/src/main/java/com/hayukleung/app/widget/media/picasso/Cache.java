/*
 * Copyright (C) 2013 Square, Inc.
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

/**
 * A memory cache for storing the most recently used images.
 * <p/>
 * <em>Note:</em> The {@link com.hayukleung.app.widget.media.picasso.Cache} is accessed by multiple threads. You must ensure
 * your {@link com.hayukleung.app.widget.media.picasso.Cache} implementation is thread safe when {@link com.hayukleung.app.widget.media.picasso.Cache#get(String)} or {@link
 * com.hayukleung.app.widget.media.picasso.Cache#set(String, IImage)} is called.
 */
public interface Cache {
    /**
     * A cache which does not store any values.
     */
    com.hayukleung.app.widget.media.picasso.Cache NONE = new com.hayukleung.app.widget.media.picasso.Cache() {
        @Override
        public IImage get(String key) {
            return null;
        }

        @Override
        public void set(String key, IImage image) {
            // Ignore.
        }

        @Override
        public int size() {
            return 0;
        }

        @Override
        public int maxSize() {
            return 0;
        }

        @Override
        public void clear() {
        }

        @Override
        public void clearKeyUri(String keyPrefix) {
        }
    };

    /**
     * Retrieve an image for the specified {@code key} or {@code null}.
     */
    IImage get(String key);

    /**
     * Store an image in the cache for the specified {@code key}.
     */
    void set(String key, IImage image);

    /**
     * Returns the current size of the cache in bytes.
     */
    int size();

    /**
     * Returns the maximum size in bytes that the cache can hold.
     */
    int maxSize();

    /**
     * Clears the cache.
     */
    void clear();

    /**
     * Remove items whose key is prefixed with {@code keyPrefix}.
     */
    void clearKeyUri(String keyPrefix);
}