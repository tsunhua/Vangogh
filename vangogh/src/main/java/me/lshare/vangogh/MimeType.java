/*
 * Copyright (C) 2014 nohana, Inc.
 * Copyright 2017 Zhihu Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an &quot;AS IS&quot; BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package me.lshare.vangogh;

import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;

/**
 * MIME Type enumeration to restrict selectable.
 * <p>
 * Good example of mime types Android supports:
 * https://android.googlesource.com/platform/frameworks/base/+/refs/heads/master/media/java/android/media/MediaFile.java
 */
public enum MimeType {
  JPEG("image/jpeg", new HashSet<String>() {
    {
      add("jpg");
      add("jpeg");
    }
  }), PNG("image/png", new HashSet<String>() {
    {
      add("png");
    }
  }), GIF("image/gif", new HashSet<String>() {
    {
      add("gif");
    }
  }), BMP("image/x-ms-bmp", new HashSet<String>() {
    {
      add("bmp");
    }
  }), WEBP("image/webp", new HashSet<String>() {
    {
      add("webp");
    }
  });

  private final String mMimeTypeName;
  private final Set<String> mExtensions;

  MimeType(String mimeTypeName, Set<String> extensions) {
    mMimeTypeName = mimeTypeName;
    mExtensions = extensions;
  }

  public static Set<MimeType> ofAll() {
    return EnumSet.allOf(MimeType.class);
  }

  public static Set<MimeType> of(MimeType type, MimeType... rest) {
    return EnumSet.of(type, rest);
  }

  @Override
  public String toString() {
    return mMimeTypeName;
  }
}