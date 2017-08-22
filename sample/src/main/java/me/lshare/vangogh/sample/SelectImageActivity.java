package me.lshare.vangogh.sample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import java.util.List;

import me.lshare.vangogh.Image;
import me.lshare.vangogh.Vangogh;

public class SelectImageActivity extends AppCompatActivity {

  private static final String TAG = SelectImageActivity.class.getSimpleName();
  private List<Image> imageList;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_select_image);
    imageList = Vangogh.imageList(Vangogh.albumList().get(0));
    GridView gridView = (GridView) findViewById(R.id.grid_view);
    final ImageSelectAdapter imageSelectAdapter = new ImageSelectAdapter(this, imageList);
    imageSelectAdapter.setLayoutParams(getResources().getDisplayMetrics().widthPixels / 3);
    gridView.setAdapter(imageSelectAdapter);
    gridView.setNumColumns(3);
    gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      @Override
      public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Image image = imageList.get(position);
        Vangogh.getInstance().toggleSelect(image);
        imageSelectAdapter.notifyDataSetChanged();
      }
    });
  }
}
