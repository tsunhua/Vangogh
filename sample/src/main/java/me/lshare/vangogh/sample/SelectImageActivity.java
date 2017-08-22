package me.lshare.vangogh.sample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import me.lshare.vangogh.Album;
import me.lshare.vangogh.Image;
import me.lshare.vangogh.Vangogh;

public class SelectImageActivity extends AppCompatActivity
    implements View.OnClickListener, AdapterView.OnItemClickListener {

  private static final String TAG = SelectImageActivity.class.getSimpleName();
  private List<Image> imageList;
  private ImageSelectAdapter imageSelectAdapter;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_select_image);

    ImageView backImageView = (ImageView) findViewById(R.id.back_image_view);
    ImageView doneImageView = (ImageView) findViewById(R.id.done_image_view);
    TextView titleTextView = (TextView) findViewById(R.id.title_text_view);
    backImageView.setOnClickListener(this);
    doneImageView.setOnClickListener(this);

    Album album = Vangogh.selectedAlbum();
    titleTextView.setText(album == null ? "" : album.getName());
    imageList = Vangogh.imageList(album);
    GridView gridView = (GridView) findViewById(R.id.grid_view);
    imageSelectAdapter = new ImageSelectAdapter(this, imageList);
    imageSelectAdapter.setLayoutParams(getResources().getDisplayMetrics().widthPixels / 3);
    gridView.setAdapter(imageSelectAdapter);
    gridView.setNumColumns(3);
    gridView.setOnItemClickListener(this);
  }

  @Override
  protected void onResume() {
    super.onResume();
    imageSelectAdapter.notifyDataSetChanged();
  }

  @Override
  public void onClick(View v) {
    switch (v.getId()) {
      case R.id.back_image_view:
        this.finish();
        break;
      case R.id.done_image_view:
        setResult(RESULT_OK);
        this.finish();
        break;
    }
  }

  @Override
  public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
    Image image = imageList.get(position);
    Vangogh.getInstance().toggleSelect(image);
    imageSelectAdapter.notifyDataSetChanged();
  }
}
