package me.lshare.vangogh;

import android.text.TextUtils;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

public class Filter {
  private Set<MimeType> mimeTypeSet;
  private String nameRegex;
  private String pathContain;
  private long minSize;
  private long maxSize;
  private int limitCount;

  private Filter(Set<MimeType> mimeTypeSet,
                 String nameRegex,
                 String pathContain,
                 long minSize,
                 long maxSize,
                 int limitCount) {
    this.mimeTypeSet = mimeTypeSet;
    this.nameRegex = nameRegex;
    this.pathContain = pathContain;
    this.minSize = minSize;
    this.maxSize = maxSize;
    this.limitCount = limitCount;
  }

  public boolean filterName(String name) {
    if (TextUtils.isEmpty(nameRegex)) {
      return true;
    }
    Pattern pattern = Pattern.compile(nameRegex);
    return pattern.matcher(name).matches();
  }

  public boolean filterSize(long size) {
    return maxSize == 0 || size >= minSize && size <= maxSize;
  }

  public String getPathContain() {
    return pathContain;
  }

  public Set<MimeType> getMimeTypeSet() {
    return mimeTypeSet;
  }

  public int getLimitCount() {
    return limitCount;
  }

  public static class Builder {
    private Set<MimeType> mimeTypeSet;
    private String nameRegex;
    private String pathContain;
    private long minSize = 0L;
    private long maxSize = Long.MAX_VALUE;
    private int limitCount = Integer.MAX_VALUE;

    public Builder mimType(MimeType... mimeTypes) {
      this.mimeTypeSet = new HashSet<>(Arrays.asList(mimeTypes));
      return this;
    }

    public Builder nameRegex(String nameRegex) {
      this.nameRegex = nameRegex;
      return this;
    }

    public Builder pathContain(String pathContain) {
      this.pathContain = pathContain;
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

    public Builder limitCount(int limit) {
      if (limit <= 0) {
        throw new IllegalArgumentException("limitCount should >=0");
      }
      this.limitCount = limit;
      return this;
    }

    public Filter build() {
      return new Filter(mimeTypeSet, nameRegex, pathContain, minSize, maxSize, limitCount);
    }
  }
}
