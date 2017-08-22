package me.lshare.vangogh;

public interface ImageSelector {
  void select(Image... images);

  void deselect(Image... images);

  void toggleSelect(Image image);

  void selectAll();

  void deselectAll();
}
