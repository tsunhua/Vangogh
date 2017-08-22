package me.lshare.vangogh;

public class Image {
  private long id;
  private String name;
  private String path;

  public Image(long id, String name, String path) {
    this.id = id;
    this.name = name;
    this.path = path;
  }

  public long getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public String getPath() {
    return path;
  }
}
