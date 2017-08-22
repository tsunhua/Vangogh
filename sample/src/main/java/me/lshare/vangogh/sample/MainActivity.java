package me.lshare.vangogh.sample;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import me.lshare.vangogh.Filter;
import me.lshare.vangogh.MimeType;
import me.lshare.vangogh.Vangogh;

public class MainActivity extends AppCompatActivity {

  private static final String TAG = MainActivity.class.getSimpleName();
  private static final int REQUEST_PERMISSION_STORAGE = 1001;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
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
        Toast.makeText(this, "读取外存权限未被授予", Toast.LENGTH_SHORT).show();
        findViewById(R.id.select_image_button).setEnabled(false);
      }
    }
  }

  private void initVangogh() {
    Filter filter =
        new Filter.Builder().mimType(MimeType.JPEG)/*.nameRegex(".*wx_camera.*")*/.build();
    Vangogh.create(filter).bind(this).init();
  }

  public void onClickSelectImage(View view) {
    startActivityForResult(new Intent(this, SelectAlbumActivity.class), 0);
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (resultCode == RESULT_OK) {
      Log.d(TAG,
            "selected album: " +
            (Vangogh.selectedAlbum() == null ? "null" : Vangogh.selectedAlbum().getName()));
      Log.d(TAG, "selected images: " + Vangogh.selectedImageList().toString());
      Vangogh.selectNone();
    }
  }
}
