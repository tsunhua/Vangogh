package me.lshare.vangogh.sample;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.util.List;

import me.lshare.vangogh.Album;
import me.lshare.vangogh.Filter;
import me.lshare.vangogh.MimeType;
import me.lshare.vangogh.Vangogh;

public class MainActivity extends AppCompatActivity {

  private static final String TAG = MainActivity.class.getSimpleName();
  private static final int REQUEST_PERMISSION_STORAGE = 1001;
  ContentObserver contentObserver;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    contentObserver = new ContentObserver(new Handler(Looper.getMainLooper())) {
      @Override
      public void onChange(boolean selfChange, Uri uri) {
        super.onChange(selfChange, uri);
        initVangogh();
      }
    };
    getContentResolver().registerContentObserver(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                                                 true,
                                                 contentObserver);
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    getContentResolver().unregisterContentObserver(contentObserver);
  }

  public void onClickInit(View view) {
    if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M &&
        ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) ==
        PackageManager.PERMISSION_DENIED) {

      ActivityCompat.requestPermissions(this,
                                        new String[] {Manifest.permission.READ_EXTERNAL_STORAGE},
                                        REQUEST_PERMISSION_STORAGE);
      return;
    }
    initVangogh();
  }

  @Override
  public void onRequestPermissionsResult(int requestCode,
                                         @NonNull String[] permissions,
                                         @NonNull int[] grantResults) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    if (requestCode == REQUEST_PERMISSION_STORAGE) {
      if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
        initVangogh();
      } else {
        Toast.makeText(this, R.string.external_storage_permission_denied, Toast.LENGTH_SHORT)
             .show();
        findViewById(R.id.select_image_button).setEnabled(false);
      }
    }
  }

  private void initVangogh() {
    Filter filter = new Filter.Builder().mimType(MimeType.JPEG)
                                        /*.nameRegex(".*wx_camera.*")*/
                                        /*.limitCount(3)*/
                                        /*.pathContain("/tencent/MicroMsg/WeiXin")*/.build();
    Vangogh.create(filter).init(this);
  }

  public void onClickSelectAlbum(View view) {
    startActivityForResult(new Intent(this, SelectAlbumActivity.class), 0);
  }

  public void onClickSelectImage(View view) {
    Intent intent = new Intent(this, SelectImageActivity.class);
    List<Album> alba = Vangogh.albumList();
    for (Album album : alba) {
      if (album.getName().equalsIgnoreCase("Camera")) {
        intent.putExtra(SelectImageActivity.EXTRA_ALBUM, album);
        startActivityForResult(intent, 1);
        break;
      }
    }
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    switch (resultCode) {
      case RESULT_OK:
        Log.i(TAG, "selected image count: " + Vangogh.selectedImageCount());
        break;
      case RESULT_CANCELED:
        Log.i(TAG, "cancel select");
        break;
    }
    Vangogh.getInstance().selectNone();
  }
}
