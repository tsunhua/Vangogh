package me.lshare.vangogh.sample;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import me.lshare.vangogh.Filter;
import me.lshare.vangogh.MimeType;
import me.lshare.vangogh.Vangogh;

public class MainActivity extends AppCompatActivity {

  private static final String TAG = MainActivity.class.getSimpleName();

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
  }

  public void onClickInit(View view) {
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
