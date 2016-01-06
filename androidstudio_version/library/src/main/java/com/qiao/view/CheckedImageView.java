package com.qiao.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.qiao.imageselector.R;
import com.qiao.util.ImageLoadUtil;
import com.qiao.util.Util;

/**
 * 图片类
 * @author Qiao
 * 2015-3-18
 */
public class CheckedImageView extends FrameLayout {
	public final static String TAG = "CheckedImageView:";
	final int checkedColor = Color.parseColor("#cc0081e4");
	
	private ImageView imageView; //图片
	private View checkedView; //选中框
	private String path;
	private boolean isChecked;
	
	public CheckedImageView(Context context) {
		super(context);
		initialize();
	}

	public CheckedImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initialize();
	}

	private void initialize() {
		LayoutInflater.from(getContext()).inflate(R.layout.checked_image_item, this);
		imageView = (ImageView) findViewById(R.id.image_view);
		checkedView = findViewById(R.id.choosed_view);
//		imageView.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
//			@Override
//			public void onGlobalLayout() {
//				imageView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
//				ViewGroup.LayoutParams vlp = imageView.getLayoutParams();
//				vlp.width = imageView.getWidth();
//				vlp.height = imageView.getHeight();
//				imageView.setLayoutParams(vlp);
//				if (path != null  && !path.trim().isEmpty()) {
//					ImageLoadUtil.getInstance(3,ImageLoadUtil.Type.LIFO).loadImage(path,imageView);
//				}
//			}
//		});
	}
	
	public void setView(String path,boolean isChecked){
		imageView.setImageResource(R.drawable.pic_default);
		if (!Util.isNullOrWhiteSpace(path)) {
			this.path = path;
			setTag(TAG + path);
			Bitmap bm = ImageLoadUtil.getInstance().getBitmapFromLruCache(path);
			if(bm!=null){
				imageView.setImageBitmap(bm);
			}
		}
		setChecked(isChecked);
	}
	
	public void loadImage(){
		if (!Util.isNullOrWhiteSpace(path)) {
			this.path = path;
			ImageLoadUtil.getInstance(3,ImageLoadUtil.Type.LIFO).loadImage(path,imageView);
		}
	}
	
	public void setChecked(boolean isChecked){
		this.isChecked = isChecked;
		if(isChecked){
			imageView.setColorFilter(checkedColor);
			checkedView.setVisibility(VISIBLE);
		}else{
			imageView.setColorFilter(Color.TRANSPARENT);
			checkedView.setVisibility(INVISIBLE);
		}
	}
	
	public String getPath(){
		return path;
	}
	
	public boolean isChecked(){
		return isChecked;
	}
}
