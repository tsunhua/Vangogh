package me.lshare.vangogh.sample;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import me.lshare.vangogh.Image;
import me.lshare.vangogh.Vangogh;

public class ImageSelectAdapter extends GenericAdapter<Image> {
  public ImageSelectAdapter(Context context, List<Image> images) {
    super(context, images);
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    ViewHolder viewHolder;

    if (convertView == null) {
      convertView = layoutInflater.inflate(R.layout.list_item_image, null);
      viewHolder = new ViewHolder();
      viewHolder.imageView = (ImageView) convertView.findViewById(R.id.image_view_image_select);
      viewHolder.view = convertView.findViewById(R.id.view_alpha);
      convertView.setTag(viewHolder);
    } else {
      viewHolder = (ViewHolder) convertView.getTag();
    }

    viewHolder.imageView.getLayoutParams().width = size;
    viewHolder.imageView.getLayoutParams().height = size;

    viewHolder.view.getLayoutParams().width = size;
    viewHolder.view.getLayoutParams().height = size;

    Image image = arrayList.get(position);
    if (image.isSelected()) {
      viewHolder.view.setAlpha(0.5f);
      ((FrameLayout) convertView).setForeground(context.getResources()
                                                       .getDrawable(R.drawable.ic_done_white));
    } else {
      viewHolder.view.setAlpha(0.0f);
      ((FrameLayout) convertView).setForeground(null);
    }
    Picasso.with(context)
           .load(new File(image.getPath()))
           .placeholder(R.drawable.image_placeholder)
           .resize(200, 200)
           .centerCrop()
           .into(viewHolder.imageView);

    return convertView;
  }

  private static class ViewHolder {
    public ImageView imageView;
    public View view;
  }
}
