package me.lshare.vangogh.sample;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.List;

public abstract class GenericAdapter<T> extends BaseAdapter {
  protected List<T> arrayList;
  protected Context context;
  protected LayoutInflater layoutInflater;

  protected int size;

  public GenericAdapter(Context context, List<T> arrayList) {
    this.arrayList = arrayList;
    this.context = context;
    this.layoutInflater = LayoutInflater.from(this.context);
  }

  @Override
  public int getCount() {
    return arrayList.size();
  }

  public T getItem(int position) {
    return arrayList.get(position);
  }

  @Override
  public long getItemId(int position) {
    return position;
  }

  public void setLayoutParams(int size) {
    this.size = size;
  }

  public void releaseResources() {
    arrayList = null;
    context = null;
  }
}
