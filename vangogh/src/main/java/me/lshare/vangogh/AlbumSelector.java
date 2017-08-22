package me.lshare.vangogh;

public interface AlbumSelector {
  void select(Album album);

  void deselect(Album album);

  void toggleSelect(Album album);
}
