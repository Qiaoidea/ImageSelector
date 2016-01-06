package com.qiao.adapter;

import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.qiao.util.MediaHelper;
import com.qiao.view.CheckedImageView;

import java.util.ArrayList;
import java.util.List;

/**
 * 加载图盘的游标适配器
 * @author Qiao
 * 2015-3-18
 */
public class ImageCursorAdapter extends android.support.v4.widget.CursorAdapter{
	private List<String> selectedLsit;
	
	public ImageCursorAdapter(Context context, Cursor c) {
		super(context, c);
		selectedLsit = new ArrayList<String>();
	}
	
	public ImageCursorAdapter(Context context, Cursor c,ArrayList<String> list) {
		super(context, c);
		selectedLsit = list;
	}

	@Override
	public void bindView(final View view, Context context, Cursor cursor) {
		final String path = cursor.getString(MediaHelper.IMAGE_PATH);
		((CheckedImageView)view).setView(path, selectedLsit.contains(path));
		view.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				onItemClick((CheckedImageView)view,path);
			}
		});
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		return new CheckedImageView(context) ;
	}
	
	@Override
    public String getItem(int position) {
        Cursor cursor =  (Cursor)super.getItem(position);
        if(cursor!=null)
            return cursor.getString(MediaHelper.IMAGE_PATH);
        return null;
    }
	
	public void onItemClick(CheckedImageView item, String path){
		
	}
}
