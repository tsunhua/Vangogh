package me.lshare.vangogh;

import java.io.Serializable;

public class Image implements Serializable {
  private long id;
  private String name;
  private String path;
  private boolean isSelected;

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

  public boolean isSelected() {
    return isSelected;
  }

  public void setSelected(boolean selected) {
    isSelected = selected;
  }
}
