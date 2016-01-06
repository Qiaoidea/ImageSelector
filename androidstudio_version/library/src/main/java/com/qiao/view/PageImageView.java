package com.qiao.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import uk.co.senab.photoview.PhotoView;

public class PageImageView extends FrameLayout{
	private PhotoView imageView;
	private ProgressBar progressBar;
	private boolean isLoadSuccess;
	
	public PageImageView(Context context) {
		super(context);
		initalize();
	}

	public PageImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initalize();
	}

	private void initalize() {
		imageView = new PhotoView(getContext());
		addView(imageView, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		progressBar = new ProgressBar(getContext());
		LayoutParams flp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		flp.gravity = Gravity.CENTER;
		addView(progressBar,flp);
		
		imageView.setVisibility(View.INVISIBLE);
		progressBar.setVisibility(View.VISIBLE);
		isLoadSuccess = true;
	}

	public PhotoView getImageView() {
		return imageView;
	}

	public void setImageView(PhotoView imageView) {
		this.imageView = imageView;
	}

	public ProgressBar getProgressBar() {
		return progressBar;
	}

	public void setProgressBar(ProgressBar progressBar) {
		this.progressBar = progressBar;
	}

	public boolean isLoadSuccess() {
		return isLoadSuccess;
	}

	public void setLoadSuccess(boolean isLoadSuccess) {
		this.isLoadSuccess = isLoadSuccess;
	}
	
	
}
