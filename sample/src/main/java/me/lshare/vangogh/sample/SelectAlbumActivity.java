package me.lshare.vangogh.sample;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import me.lshare.vangogh.Album;
import me.lshare.vangogh.Image;
import me.lshare.vangogh.Vangogh;

public class SelectAlbumActivity extends AppCompatActivity
    implements View.OnClickListener, AdapterView.OnItemClickListener {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_select_album);
    findViewById(R.id.back_image_view).setOnClickListener(this);
    GridView gridView = (GridView) findViewById(R.id.grid_view);
    AlbumSelectAdapter albumSelectAdapter = new AlbumSelectAdapter(this, Vangogh.albumList());
    albumSelectAdapter.setLayoutParams(getResources().getDisplayMetrics().widthPixels / 2);
    gridView.setAdapter(albumSelectAdapter);
    gridView.setNumColumns(2);
    gridView.setOnItemClickListener(this);
  }

  @Override
  public void onClick(View v) {
    switch (v.getId()) {
      case R.id.back_image_view:
        this.finish();
        break;
    }
  }

  @Override
  public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
    Album album = Vangogh.albumList().get(position);
    Vangogh.getInstance().select(album);
    startActivityForResult(new Intent(this, SelectImageActivity.class), 0);
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (resultCode == RESULT_OK) {
      setResult(RESULT_OK);
      this.finish();
    }
  }
}
