package me.lshare.vangogh;

public interface ImageSelector {
  boolean toggleSelect(Album album, Image image);

  boolean selectAll(Album album);

  boolean deselectAll(Album album);
}
