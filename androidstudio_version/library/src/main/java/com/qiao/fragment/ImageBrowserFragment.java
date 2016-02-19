package com.qiao.fragment;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.kogitune.activity_transition.ActivityTransition;
import com.kogitune.activity_transition.ExitActivityTransition;
import com.qiao.imageselector.R;
import com.qiao.util.ImageLoadUtil;
import com.qiao.util.ImageLoadUtil.ImageLoadListener;
import com.qiao.view.PageImageView;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by bingosoft on 15/4/17.
 */
public class ImageBrowserFragment extends Fragment{
	private Activity activity;
	private View titleView,backView;
	private int currIndex,count;
	private List<String> dataList;
	private ViewPager viewPager;
	private boolean isScale=true;
	
	@Override
	public void onAttach(Activity activity) {
		this.activity = activity;
		super.onAttach(activity);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		initData();
		return rootView = inflater.inflate(R.layout.activity_image_browser,null);
	}

	private ExitActivityTransition exitTransition;

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		if(isScale)
			exitTransition = ActivityTransition.with(activity.getIntent()).to(findViewById(R.id.view_pager)).start(savedInstanceState);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		initViews();
		initListeners();
	}

	private void initData() {
		Intent intent = activity.getIntent();
		currIndex = intent.getIntExtra("currIndex", 0);
		isScale = intent.getBooleanExtra("isScale",true);
		dataList = (List<String>) intent.getSerializableExtra("dataList");
		if (dataList == null) {
			dataList = new ArrayList<String>();
		}
		count = dataList.size();
	}
	
	protected View rootView;
	protected View findViewById(int viewId) {
		return rootView.findViewById(viewId);
	}

	private void initViews() {
		titleView = findViewById(R.id.top);
		titleView.setVisibility(View.INVISIBLE);
		backView = findViewById(R.id.back_view);
		viewPager = (ViewPager)findViewById(R.id.view_pager);
		viewPager.setAdapter(new ImagePagerAdapter());
		viewPager.setCurrentItem(currIndex);
		titleView.postDelayed(new Runnable() {
			@Override
			public void run() {
				titleView.setVisibility(View.VISIBLE);
			}
		}, 500);
	}

	private void initListeners() {
		backView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(exitTransition!=null) {
					titleView.setVisibility(View.INVISIBLE);
					exitTransition.exit(activity);
				}else{
					activity.onBackPressed();
				}
			}
		});
	}
	
	/**
	 * 图片适配器
	 */
	class ImagePagerAdapter extends PagerAdapter{
		protected PageImageView currView;

		@Override
		public int getCount() {
			return dataList.size();
		}

		@Override
		public boolean isViewFromObject(View view, Object object) {
			return view.equals(object);
		}

		@Override
		public void destroyItem(ViewGroup collection, int position, Object view){
			collection.removeView((View) view);
		}
		
		@Override
	    public void setPrimaryItem(ViewGroup container, int position, Object object) {
	        super.setPrimaryItem(container, position, object);
	        if (currIndex == position && currView!=null) return;
	        if(currView!=null){
				currView.getImageView().enable();
	        }
	        currIndex = position;
	        currView = (PageImageView)object;
			if (!currView.isLoadSuccess()) {
				Toast.makeText(activity, "图片加载失败,请确认图片或网络可用!", Toast.LENGTH_SHORT).show();
			}
	    }
		
		@Override
	    public Object instantiateItem(ViewGroup collection, int position){
			PageImageView convertView =  new PageImageView(activity);
			String path = dataList.get(position);
	        collection.addView(convertView, 0);
	        ImageLoadUtil.getInstance(3,ImageLoadUtil.Type.LIFO).loadImage(path,convertView.getImageView(),bindListener(convertView));
			return convertView;
		}
		
		private ImageLoadListener bindListener(final PageImageView pageImageView){
			return new ImageLoadListener() {
				
				@Override
				public void onLoadComplete(ImageView imageView, Bitmap bitmap,
						boolean isSuccess) {
					pageImageView.setLoadSuccess(isSuccess);
					pageImageView.getProgressBar().setVisibility(View.GONE);
					pageImageView.getImageView().setVisibility(View.VISIBLE);
				}
			};
		}
	}
}

