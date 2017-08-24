package me.lshare.vangogh;

import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.provider.MediaStore;
import android.util.Log;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Vangogh implements ImageSelector {
  private static final String TAG = Vangogh.class.getSimpleName();
  private Filter filter;
  private WeakReference<Activity> contextReference;
  private static List<Album> tmpAlbumList;
  private static Map<Album, List<Image>> allImageMap;
  private Handler handler;

  private static final int MSG_IMAGE_LIST_DATA = 10001;

  private final String[] albumProjection = new String[] {
      MediaStore.Images.Media.BUCKET_ID, MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
      MediaStore.Images.Media.DATA
  };

  private final String[] imgProjection = new String[] {
      MediaStore.Images.Media._ID, MediaStore.Images.Media.DISPLAY_NAME,
      MediaStore.Images.Media.SIZE, MediaStore.Images.Media.DATA
  };

  static {
    tmpAlbumList = new ArrayList<>();
    allImageMap = new HashMap<>();
    reset();
  }

  private static Album getAlbum(long id) {
    for (Album album : allImageMap.keySet()) {
      if (album.getId() == id) {
        return album;
      }
    }
    return null;
  }

  public static void selectNone() {
    Set<Album> albumSet = allImageMap.keySet();
    for (Album album : albumSet) {
      album.setSelected(false);
      for (Image image : allImageMap.get(album)) {
        image.setSelected(false);
      }
    }
  }

  private StringBuilder where;

  public Vangogh bind(Activity context) {
    this.contextReference = new WeakReference<>(context);
    return this;
  }

  private Vangogh(Filter filter) {
    this.filter = filter;
    this.handler = new Handler(Looper.getMainLooper()) {
      @Override
      public void handleMessage(Message msg) {
        super.handleMessage(msg);
        switch (msg.what) {
          case MSG_IMAGE_LIST_DATA:
            Log.d(TAG, "image list data loaded: " + allImageMap.size());
            Set<Album> alba = allImageMap.keySet();
            for (Album album : alba) {
              Log.d(TAG, "" + allImageMap.get(album).toString());
            }
            break;
          default:
            break;
        }
      }
    };
    // filter mimType
    where = new StringBuilder();
    int i = 0;
    for (MimeType mimType : filter.getMimeTypeSet()) {
      where.append(MediaStore.Images.Media.MIME_TYPE + "=").append("'").append(mimType).append("'");
      if (i != filter.getMimeTypeSet().size() - 1) {
        where.append(" OR ");
      }
      i++;
    }

    // filter path
    if (filter.getPath() != null) {
      where.append(" AND ")
           .append(MediaStore.Images.Media.DATA)
           .append(" like ")
           .append("'")
           .append(filter.getPath())
           .append("%")
           .append("'");
    }
  }

  private static void reset() {
    allImageMap.clear();
    tmpAlbumList.clear();
    selectNone();
  }

  public void init() {
    reset();
    new Thread() {
      @Override
      public void run() {
        Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
        Cursor cursor = contextReference.get()
                                        .getApplicationContext()
                                        .getContentResolver()
                                        .query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                                               albumProjection,
                                               where.toString(),
                                               null,
                                               MediaStore.Images.Media.DATE_ADDED);
        if (cursor == null) {
          // error
          return;
        }

        ArrayList<Album> temp = new ArrayList<>(cursor.getCount());
        HashSet<Long> albumSet = new HashSet<>();
        File file;
        if (cursor.moveToLast()) {
          do {
            if (Thread.interrupted()) {
              return;
            }

            long albumId = cursor.getLong(cursor.getColumnIndex(albumProjection[0]));
            String album = cursor.getString(cursor.getColumnIndex(albumProjection[1]));
            String image = cursor.getString(cursor.getColumnIndex(albumProjection[2]));

            if (!albumSet.contains(albumId)) {
          /*
          It may happen that some image file paths are still present in cache,
          though image file does not exist. These last as long as media
          scanner is not run again. To avoid get such image file paths, check
          if image file exists.
           */
              file = new File(image);
              if (file.exists()) {
                temp.add(new Album(albumId, album, image));
                albumSet.add(albumId);
              }
            }

          } while (cursor.moveToPrevious());
        }
        cursor.close();
        tmpAlbumList.clear();
        tmpAlbumList.addAll(temp);

        countDown = tmpAlbumList.size();
        for (Album album : tmpAlbumList) {
          loadImageList(album);
        }
      }
    }.start();
  }

  private int countDown;

  private void loadImageList(final Album album) {
    new Thread() {
      @Override
      public void run() {
        Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
        super.run();
        File file;
        Cursor cursor = contextReference.get()
                                        .getContentResolver()
                                        .query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                                               imgProjection,
                                               where.toString() + " AND " +
                                               MediaStore.Images.Media.BUCKET_DISPLAY_NAME + " =?",
                                               new String[] {album.getName()},
                                               MediaStore.Images.Media.DATE_ADDED);
        if (cursor == null) {
          // error
          return;
        }

        ArrayList<Image> temp = new ArrayList<>();
        if (cursor.moveToLast()) {
          do {
            if (Thread.interrupted()) {
              return;
            }

            long id = cursor.getLong(cursor.getColumnIndex(imgProjection[0]));

            // filter name
            String name = cursor.getString(cursor.getColumnIndex(imgProjection[1]));
            if (!filter.filterName(name)) {
              continue;
            }

            // filter size
            long size = cursor.getLong(cursor.getColumnIndex(imgProjection[2]));
            if (!filter.filterSize(size)) {
              continue;
            }

            String path = cursor.getString(cursor.getColumnIndex(imgProjection[3]));
            file = new File(path);
            if (file.exists()) {
              temp.add(new Image(id, name, path));
            }

          } while (cursor.moveToPrevious());
        }
        cursor.close();

        if (!temp.isEmpty()) {
          allImageMap.put(album, temp);
        }
        synchronized (Vangogh.class) {
          countDown--;
          if (countDown == 0) {
            handler.sendEmptyMessage(MSG_IMAGE_LIST_DATA);
          }
        }
      }
    }.start();
  }

  private static Vangogh instance;

  public static Vangogh getInstance() {
    return instance;
  }

  public static List<Image> imageList(Album album) {
    List<Image> imageList = allImageMap.get(album);
    return imageList == null ? new ArrayList<Image>() : imageList;
  }

  public static Map<Album, List<Image>> selectedImageMap() {
    Map<Album, List<Image>> result = new HashMap<>();
    Set<Album> albumSet = allImageMap.keySet();
    for (Album album : albumSet) {
      if (album.isSelected()) {
        List<Image> imageList = new ArrayList<>();
        for (Image image : allImageMap.get(album)) {
          if (image.isSelected()) {
            imageList.add(image);
          }
        }
        if (!imageList.isEmpty()) {
          result.put(album, imageList);
        }
      }
    }
    return result;
  }

  public int getSelectLimit() {
    int result = filter.getLimit();
    if (filter.getLimit() == 0) {
      result = Integer.MAX_VALUE;
    }
    return result;
  }

  public static int selectedImageCount() {
    int count = 0;
    Set<Album> albumSet = allImageMap.keySet();
    for (Album album : albumSet) {
      if (album.isSelected()) {
        for (Image image : allImageMap.get(album)) {
          if (image.isSelected()) {
            count++;
          }
        }
      }
    }
    return count;
  }

  public static List<Album> albumList() {
    return new ArrayList<>(allImageMap.keySet());
  }

  public static Vangogh create(Filter filter) {
    instance = new Vangogh(filter);
    return instance;
  }

  @Override
  public boolean toggleSelect(Album alba, Image image) {
    Album album = getAlbum(alba.getId());
    if (album == null) {
      return false;
    }
    // check limit
    if (!image.isSelected() && selectedImageCount() >= getSelectLimit()) {
      return false;
    }

    image.setSelected(!image.isSelected());
    if (image.isSelected()) {
      album.setSelected(true);
    } else {
      List<Image> imageList = allImageMap.get(album);
      int i = 0;
      for (; i < imageList.size(); i++) {
        if (imageList.get(i).isSelected()) {
          album.setSelected(true);
          break;
        }
      }
      if (i == imageList.size()) {
        album.setSelected(false);
      }
    }
    return true;
  }

  @Override
  public boolean selectAll(Album alba) {
    Album album = getAlbum(alba.getId());
    if (album == null) {
      return false;
    }
    // check limit
    int selectedImageCount = selectedImageCount();
    if (selectedImageCount > getSelectLimit()) {
      return false;
    }
    album.setSelected(true);
    int countCanSelect = getSelectLimit() - selectedImageCount;
    for (Image img : allImageMap.get(album)) {
      if (countCanSelect <= 0) {
        break;
      }
      if (!img.isSelected()) {
        img.setSelected(true);
        countCanSelect--;
      }
    }
    return true;
  }

  @Override
  public boolean deselectAll(Album alba) {
    Album album = getAlbum(alba.getId());
    if (album == null) {
      return false;
    }
    album.setSelected(false);
    for (Image img : allImageMap.get(album)) {
      img.setSelected(false);
    }
    return true;
  }
}
