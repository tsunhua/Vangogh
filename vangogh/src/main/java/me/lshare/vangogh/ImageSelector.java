package me.lshare.vangogh;

public interface ImageSelector {
  void toggleSelect(Album album, Image image);

  void selectAll(Album album);

  void deselectAll(Album album);
}
