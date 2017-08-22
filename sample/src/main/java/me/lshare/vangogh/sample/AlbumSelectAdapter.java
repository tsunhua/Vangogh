package me.lshare.vangogh.sample;

import android.content.Context;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.List;

import me.lshare.vangogh.Album;

public class AlbumSelectAdapter extends GenericAdapter<Album> {
  public AlbumSelectAdapter(Context context, List<Album> albums) {
    super(context, albums);
  }

  public View getView(int position, View convertView, ViewGroup parent) {
    ViewHolder viewHolder;

    if (convertView == null) {
      convertView = layoutInflater.inflate(R.layout.list_item_album, null);

      viewHolder = new ViewHolder();
      viewHolder.imageView = (ImageView) convertView.findViewById(R.id.image_view_album_image);
      viewHolder.textView = (TextView) convertView.findViewById(R.id.text_view_album_name);
      convertView.setTag(viewHolder);

    } else {
      viewHolder = (ViewHolder) convertView.getTag();
    }

    viewHolder.imageView.getLayoutParams().width = size;
    viewHolder.imageView.getLayoutParams().height = size;

    viewHolder.textView.setText(arrayList.get(position).getName());
    Picasso.with(context)
           .load(Uri.fromFile(new File(arrayList.get(position).getCover())))
           .placeholder(R.drawable.image_placeholder)
           .centerCrop()
           .into(viewHolder.imageView);

    return convertView;
  }

  private static class ViewHolder {
    public ImageView imageView;
    public TextView textView;
  }
}
