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
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import me.lshare.vangogh.Album;
import me.lshare.vangogh.Image;
import me.lshare.vangogh.Vangogh;

public class SelectImageActivity extends AppCompatActivity
    implements View.OnClickListener, AdapterView.OnItemClickListener {

  public static final String EXTRA_ALBUM = "extra_album";
  private static final String TAG = SelectImageActivity.class.getSimpleName();
  private List<Image> imageList;
  private ImageSelectAdapter imageSelectAdapter;
  private Album album;
  private TextView selectAllTextView;
  private ImageView doneImageView;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_select_image);

    ImageView backImageView = (ImageView) findViewById(R.id.back_image_view);
    doneImageView = (ImageView) findViewById(R.id.done_image_view);
    TextView titleTextView = (TextView) findViewById(R.id.title_text_view);
    selectAllTextView = (TextView) findViewById(R.id.select_all_text_view);
    selectAllTextView.setOnClickListener(this);
    backImageView.setOnClickListener(this);
    doneImageView.setOnClickListener(this);

    album = (Album) getIntent().getSerializableExtra(EXTRA_ALBUM);
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
    if (Vangogh.selectedImageCount() > 0) {
      doneImageView.setVisibility(View.VISIBLE);
      selectAllTextView.setVisibility(View.VISIBLE);
    } else {
      doneImageView.setVisibility(View.INVISIBLE);
      selectAllTextView.setVisibility(View.INVISIBLE);
    }
    imageSelectAdapter.notifyDataSetChanged();
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
      case R.id.select_all_text_view:
        switch (selectAllTextView.getText().toString()) {
          case "取消全选":
            Vangogh.getInstance().deselectAll(album);
            selectAllTextView.setText("全选");
            break;
          case "全选":
            boolean all = Vangogh.getInstance().selectAll(album);
            if (all) {
              selectAllTextView.setText("取消全选");
            }
            break;
        }
        imageSelectAdapter.notifyDataSetChanged();
        break;
    }
  }

  @Override
  public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
    Image image = imageList.get(position);
    boolean result = Vangogh.getInstance().toggleSelect(album, image);
    int selectedImageCount = Vangogh.selectedImageCount();
    if (result) {
      if (selectedImageCount > 0) {
        selectAllTextView.setVisibility(View.VISIBLE);
        doneImageView.setVisibility(View.VISIBLE);
      } else {
        selectAllTextView.setVisibility(View.INVISIBLE);
        doneImageView.setVisibility(View.INVISIBLE);
      }
      imageSelectAdapter.notifyDataSetChanged();
    } else {
      Toast.makeText(this, "最多只能选择" + selectedImageCount + "张", Toast.LENGTH_SHORT).show();
    }
  }
}
