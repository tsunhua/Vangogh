package me.lshare.vangogh.sample;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import java.util.List;

import me.lshare.vangogh.Album;
import me.lshare.vangogh.Image;
import me.lshare.vangogh.Filter;
import me.lshare.vangogh.MimeType;
import me.lshare.vangogh.OnSelectResultCallback;
import me.lshare.vangogh.Vangogh;

public class MainActivity extends AppCompatActivity implements OnSelectResultCallback {


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
  }

  public void onClickInit(View view) {
    Filter filter = new Filter.Builder().mimType(MimeType.JPEG).nameRegex(".*wx_camera.*").build();
    Vangogh.create(filter, this).bind(this).init();
  }

  public void onClickSelectImage(View view) {
    startActivity(new Intent(this, SelectImageActivity.class));
  }

  @Override
  public void onSelectResult(Album album, List<Image> imageList) {

  }
}
