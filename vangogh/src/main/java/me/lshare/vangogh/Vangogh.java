package me.lshare.vangogh;

import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

public class Vangogh implements AlbumSelector, ImageSelector {
  private static final String TAG = Vangogh.class.getSimpleName();
  private Filter filter;
  private OnSelectResultCallback callback;
  private static List<Image> selectedImageList;
  private static Album selectedAlbum;
  private WeakReference<Activity> contextReference;
  private static List<Album> allAlbum;
  private static Map<Album, List<Image>> allImage;
  private Handler handler;
  private static final int MSG_ALBUM_LIST_DATA = 1;
  private static final int MSG_IMAGE_LIST_DATA = 2;

  private final String[] albumProjection = new String[] {
      MediaStore.Images.Media.BUCKET_ID, MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
      MediaStore.Images.Media.DATA
  };

  private final String[] imgProjection = new String[] {
      MediaStore.Images.Media._ID, MediaStore.Images.Media.DISPLAY_NAME,
      MediaStore.Images.Media.DATA, MediaStore.Images.Media.SIZE
  };

  static {
    allAlbum = new ArrayList<>();
    allImage = new HashMap<>();
    selectedAlbum = null;
    selectedImageList = new ArrayList<>();
  }

  private Uri uri;
  private StringBuilder where;

  public Vangogh bind(Activity context) {
    this.contextReference = new WeakReference<>(context);
    return this;
  }

  private Vangogh(Filter filter, OnSelectResultCallback callback) {
    this.filter = filter;
    this.callback = callback;
    this.handler = new Handler(Looper.getMainLooper()) {
      @Override
      public void handleMessage(Message msg) {
        super.handleMessage(msg);
        switch (msg.what) {
          case MSG_ALBUM_LIST_DATA:
            Log.d(TAG, "album list data loaded: " + allAlbum.toString());
            break;
          case MSG_IMAGE_LIST_DATA:
            Log.d(TAG, "image list data loaded: " + allImage.size());
            Set<Album> alba = allImage.keySet();
            for (Album album : alba) {
              Log.d(TAG, "" + allImage.get(album).toString());
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
      where.append(MediaStore.Images.Media.MIME_TYPE + "=").append("'" + mimType + "'");
      if (i != filter.getMimeTypeSet().size() - 1) {
        where.append(" OR ");
      }
      i++;
    }

    // filter path
    if (filter.getPath() != null) {
      uri = Uri.fromFile(new File(filter.getPath()));
    } else {
      uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
    }
  }

  public void start() {
  }

  public void init() {
    new Thread() {
      @Override
      public void run() {
        Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
        Cursor cursor = contextReference.get()
                                        .getApplicationContext()
                                        .getContentResolver()
                                        .query(uri,
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
        allAlbum.clear();
        allAlbum.addAll(temp);
        handler.sendEmptyMessage(MSG_ALBUM_LIST_DATA);

        countDown = allAlbum.size();
        for (Album album : allAlbum) {
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
        //    List<Image> images = new ArrayList<>();
        File file;
        //    HashSet<Long> selectedImages = new HashSet<>();
        //    if (images != null) {
        //      Image image;
        //      for (int i = 0, l = images.size(); i < l; i++) {
        //        image = images.get(i);
        //        file = new File(image.getPath());
        //        if (file.exists() &&  ) {
        //          selectedImages.add(image.getId());
        //        }
        //      }
        //    }


        Cursor cursor = contextReference.get()
                                        .getContentResolver()
                                        .query(uri,
                                               imgProjection,
                                               where.toString() + " AND " +
                                               MediaStore.Images.Media.BUCKET_DISPLAY_NAME + " =?",
                                               new String[] {album.getName()},
                                               MediaStore.Images.Media.DATE_ADDED);
        if (cursor == null) {
          // error
          return;
        }
    /*
    In case this runnable is executed to onChange calling loadImages,
    using countSelected variable can result in a race condition. To avoid that,
    tempCountSelected keeps track of number of selected images. On handling
    FETCH_COMPLETED message, countSelected is assigned value of tempCountSelected.
     */
        //        int tempCountSelected = 0;
        ArrayList<Image> temp = new ArrayList<>();
        if (cursor.moveToLast()) {
          do {
            if (Thread.interrupted()) {
              return;
            }

            long id = cursor.getLong(cursor.getColumnIndex(imgProjection[0]));
            String path = cursor.getString(cursor.getColumnIndex(imgProjection[2]));

            // filter name
            String name = cursor.getString(cursor.getColumnIndex(imgProjection[1]));
            if (!filter.filterName(name)) {
              return;
            }

            // filter size
            long size = cursor.getLong(cursor.getColumnIndex(imgProjection[3]));
            if (!filter.filterSize(size)) {
              return;
            }

            //        boolean isSelected = selectedImages.contains(id);
            //        if (isSelected) {
            //          tempCountSelected++;
            //        }
            file = new File(path);
            if (file.exists()) {
              temp.add(new Image(id, name, path));
            }

          } while (cursor.moveToPrevious());
        }
        cursor.close();

        allImage.put(album, temp);
        synchronized (Vangogh.class) {
          countDown--;
          if (countDown == 0) {
            handler.sendEmptyMessage(MSG_IMAGE_LIST_DATA);
          }
        }
      }
    }.start();
  }

  private Vangogh() {
  }

  private static Vangogh instance;

  public static Vangogh getInstance() {
    return instance;
  }

  public static List<Image> imageList(Album album) {
    return allImage.get(album);
  }

  public static List<Image> selectedImageList() {
    return selectedImageList;
  }

  public static Album selectedAlbum() {
    return selectedAlbum;
  }

  public static List<Album> albumList() {
    return allAlbum;
  }

  public static Vangogh create(Filter filter, OnSelectResultCallback callback) {
    instance = new Vangogh(filter, callback);
    return instance;
  }

  @Override
  public void select(Album album) {
    selectedAlbum = album;
  }

  @Override
  public void deselect(Album album) {
    selectedAlbum = null;
  }

  @Override
  public void toggleSelect(Album album) {
    if (selectedAlbum == album) {
      deselect(album);
    } else {
      select(album);
    }
  }

  @Override
  public void select(Image... images) {
    selectedImageList.addAll(Arrays.asList(images));
  }

  @Override
  public void deselect(Image... images) {
    selectedImageList.removeAll(Arrays.asList(images));
  }

  @Override
  public void toggleSelect(Image image) {
    if (selectedImageList.contains(image)) {
      deselect(image);
    } else {
      select(image);
    }
  }

  @Override
  public void selectAll() {
    selectedImageList.clear();
    selectedImageList.addAll(allImage.get(selectedAlbum));
  }

  @Override
  public void deselectAll() {
    selectedImageList.clear();
  }
}
