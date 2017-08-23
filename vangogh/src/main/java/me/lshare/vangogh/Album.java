package me.lshare.vangogh;

import java.io.Serializable;

public class Album implements Serializable {
  private long id;
  private String name;
  private String cover;
  private boolean isSelected;

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

  public boolean isSelected() {
    return isSelected;
  }

  public void setSelected(boolean selected) {
    isSelected = selected;
  }

  @Override
  public int hashCode() {
    return (int) (id ^ (id >>> 32));
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Album album = (Album) o;
    return id == album.id;
  }
}
