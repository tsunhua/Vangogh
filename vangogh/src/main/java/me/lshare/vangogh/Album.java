package me.lshare.vangogh;

public class Album {
  private long id;
  private String name;
  private String cover;

  public Album(long id, String name, String cover) {
    this.id = id;
    this.name = name;
    this.cover = cover;
  }

  public long getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public String getCover() {
    return cover;
  }
}
