package me.lshare.vangogh.sample;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import me.lshare.vangogh.Album;
import me.lshare.vangogh.Vangogh;

public class SelectAlbumActivity extends AppCompatActivity
    implements View.OnClickListener, AdapterView.OnItemClickListener {

  private AlbumSelectAdapter albumSelectAdapter;
  private TextView titletextview;
  private ImageView doneImageView;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_select_album);
    findViewById(R.id.back_image_view).setOnClickListener(this);
    titletextview = (TextView) findViewById(R.id.title_text_view);
    doneImageView = (ImageView) findViewById(R.id.done_image_view);
    doneImageView.setOnClickListener(this);
    GridView gridView = (GridView) findViewById(R.id.grid_view);
    albumSelectAdapter = new AlbumSelectAdapter(this, Vangogh.albumList());
    albumSelectAdapter.setLayoutParams(getResources().getDisplayMetrics().widthPixels / 2);
    gridView.setAdapter(albumSelectAdapter);
    gridView.setNumColumns(2);
    gridView.setOnItemClickListener(this);
  }

  @Override
  protected void onResume() {
    super.onResume();
    albumSelectAdapter.notifyDataSetChanged();
    int selectedCount = Vangogh.selectedImageCount();
    if (selectedCount > 0) {
      titletextview.setText(
          "已选" + selectedCount + "张/限选" + Vangogh.getInstance().getSelectLimit() + "张");
      doneImageView.setVisibility(View.VISIBLE);
    } else {
      titletextview.setText("选择图片");
      doneImageView.setVisibility(View.INVISIBLE);
    }
  }

  @Override
  public void onClick(View v) {
    switch (v.getId()) {
      case R.id.back_image_view:
        setResult(RESULT_CANCELED);
        this.finish();
        break;
      case R.id.done_image_view:
        setResult(RESULT_OK);
        this.finish();
        break;
    }
  }

  @Override
  public void onBackPressed() {
    setResult(RESULT_CANCELED);
    super.onBackPressed();
  }

  @Override
  public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
    Album album = Vangogh.albumList().get(position);
    Intent intent = new Intent(this, SelectImageActivity.class);
    intent.putExtra(SelectImageActivity.EXTRA_ALBUM, album);
    startActivityForResult(intent, 0);
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (resultCode == RESULT_OK) {
      setResult(resultCode);
      this.finish();
    }
  }
}
