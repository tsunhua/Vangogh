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
import java.util.Map;

import me.lshare.vangogh.Album;
import me.lshare.vangogh.Image;
import me.lshare.vangogh.Vangogh;

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

    Album album = arrayList.get(position);
    if (album.isSelected()) {
      viewHolder.textView.setText(
          album.getName() + "(" + Vangogh.selectedImageList(album).size() + "/" +
          Vangogh.imageList(album).size() + ")");
    } else {
      viewHolder.textView.setText(album.getName());
    }
    Picasso.with(context)
           .load(Uri.fromFile(new File(album.getCover())))
           .placeholder(R.drawable.image_placeholder)
           .resize(400, 400)
           .centerCrop()
           .into(viewHolder.imageView);

    return convertView;
  }

  private static class ViewHolder {
    public ImageView imageView;
    public TextView textView;
  }
}
