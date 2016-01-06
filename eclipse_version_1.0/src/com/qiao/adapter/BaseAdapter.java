package com.qiao.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

public abstract class BaseAdapter<T> extends android.widget.BaseAdapter {

	protected Context context;
	protected ArrayList<T> dataList;

	public BaseAdapter(Context context, ArrayList<T> models) {
		this.context = context;
		if (models == null)
			this.dataList = new ArrayList<T>();
		else
			this.dataList = models;
	}

	@Override
	public int getCount() {
		if (dataList != null) {
			return dataList.size();
		}
		return 0;
	}

	@Override
	public Object getItem(int position) {
		return dataList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public abstract View getView(int position, View convertView, ViewGroup parent);

	/** 更新数据 */
	public void update(List<T> models) {
		if (models == null)
			return;
		this.dataList.clear();
		for (T t : models) {
			this.dataList.add(t);
		}
		notifyDataSetChanged();
	}
}
