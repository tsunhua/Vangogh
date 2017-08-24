package me.lshare.vangogh;

import android.text.TextUtils;
import android.view.TextureView;

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

public class Filter {

  private Set<MimeType> mimeTypeSet;
  private String nameRegex;
  private String path;
  private long minSize;
  private long maxSize;
  private int limit;

  private Filter(Set<MimeType> mimeTypeSet,
                 String nameRegex,
                 String path,
                 long minSize,
                 long maxSize,
                 int limit) {
    this.mimeTypeSet = mimeTypeSet;
    this.nameRegex = nameRegex;
    this.path = path;
    this.minSize = minSize;
    this.maxSize = maxSize;
    this.limit = limit;
  }

  public Set<MimeType> getMimeTypeSet() {
    return mimeTypeSet;
  }

  public boolean filterName(String name) {
    if (TextUtils.isEmpty(nameRegex)) {
      return true;
    }
    Pattern pattern = Pattern.compile(nameRegex);
    return pattern.matcher(name).matches();
  }

  public String getPath() {
    return path;
  }

  public boolean filterSize(long size) {
    return maxSize == 0 || size >= minSize && size <= maxSize;
  }

  public int getLimit() {
    return limit;
  }

  public static class Builder {
    private Set<MimeType> mimeTypeSet;
    private String nameRegex;
    private String path;
    private long minSize;
    private long maxSize;
    private int limit;

    public Builder mimType(MimeType... mimeTypes) {
      this.mimeTypeSet = new HashSet<>(Arrays.asList(mimeTypes));
      return this;
    }

    public Builder nameRegex(String nameRegex) {
      this.nameRegex = nameRegex;
      return this;
    }

    public Builder path(String path) {
      File file = new File(path);
      if (!file.exists()) {
        throw new IllegalArgumentException("path not found!");
      }
      this.path = path;
      return this;
    }

    public Builder size(long minSize, long maxSize) {
      if (minSize > maxSize) {
        throw new IllegalArgumentException("minSize should <= maxSize");
      }
      this.minSize = minSize;
      this.maxSize = maxSize;
      return this;
    }

    public Builder limit(int limit) {
      if (limit <= 0) {
        throw new IllegalArgumentException("limit should >=0");
      }
      this.limit = limit;
      return this;
    }

    public Filter build() {
      return new Filter(mimeTypeSet, nameRegex, path, minSize, maxSize, limit);
    }
  }
}
