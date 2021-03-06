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

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.IOException;

import static android.content.ContentResolver.SCHEME_ANDROID_RESOURCE;
import static com.hayukleung.app.widget.media.picasso.Picasso.LoadedFrom.DISK;

class ResRequestHandler extends RequestHandler {
    private final Context context;

    ResRequestHandler(Context context) {
        this.context = context;
    }

    private static Bitmap decodeResource(Resources resources, int id, Request data) {
        final BitmapFactory.Options options = createBitmapOptions(data);
        if (requiresInSampleSize(options)) {
            BitmapFactory.decodeResource(resources, id, options);
            calculateInSampleSize(data.targetWidth, data.targetHeight, options, data);
        }
        return BitmapFactory.decodeResource(resources, id, options);
    }

    @Override
    public boolean canHandleRequest(Request data) {
        if (data.resourceId != 0) {
            return true;
        }

        return SCHEME_ANDROID_RESOURCE.equals(data.uri.getScheme());
    }

    @Override
    public IResource load(Request data) throws IOException {
        Resources res = Utils.getResources(context, data);
        int id = Utils.getResourceId(res, data);
        return new ResResource(res, id, data);
    }

    private static class ResResource implements IResource {

        private Resources res;
        private int id;
        private Request data;

        public ResResource(Resources res, int id, Request data) {
            this.res = res;
            this.id = id;
            this.data = data;
        }

        @Override
        public IImage decode() throws IOException {
            IImage image = null;
            Bitmap bitmap = decodeResource(res, id, data);
            if (bitmap != null) {
                image = new BitmapImage(bitmap);
            }
            return image;
        }

        @Override
        public ImageFormat getFormat() throws IOException {
            return ImageFormat.PNG;
        }

        @Override
        public Picasso.LoadedFrom getLoadedFrom() {
            return DISK;
        }

        @Override
        public int getExifOrientation() {
            return 0;
        }

    }

}
