package com.qiao.activity;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;

import com.qiao.adapter.BaseAdapter;
import com.qiao.adapter.ImageCursorAdapter;
import com.qiao.bean.Bucket;
import com.qiao.bean.SelectorParamContext;
import com.qiao.imageselector.R;
import com.qiao.util.AnimUtil;
import com.qiao.util.ImageLoadUtil;
import com.qiao.util.MediaHelper;
import com.qiao.util.Util;
import com.qiao.view.ActionSheet;
import com.qiao.view.ActionSheet.Action1;
import com.qiao.view.BucketView;
import com.qiao.view.CheckedImageView;

public class ImageSelectorActivty extends Activity  implements AbsListView.OnScrollListener{
	protected View backView; 
	protected TextView titleView,bucketView,picQuality,browserView,okView;
	/**
	 * 展示图片
	 */
	protected GridView imageGridView;
	protected ImageCursorAdapter imageAdapter;
	/**
	 * 弹出式album列表
	 */
	protected View albumRLayout;
	private ListView albumListView;
	private Cursor mCursor;
	
	private boolean isExplore = false;
	
	private SelectorParamContext paramContext;//配置选项
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_image_selector);
		initData();
		initViews();
		initListeners();
	}

	/**
	 * 初始化数据
	 */
	private void initData() {
		Intent intent = getIntent();
		if(intent.hasExtra("paramContext")){
			paramContext = (SelectorParamContext) intent.getSerializableExtra("params");
		}else{
			paramContext = new SelectorParamContext();
		}
		loadImages();
	}

	/**
	 * 初始化views
	 */
	private void initViews() {
		backView = findViewById(R.id.back_view);
		titleView = (TextView) findViewById(R.id.head_bar_title_view);
		bucketView = (TextView)findViewById(R.id.album_view);
		browserView = (TextView)findViewById(R.id.pic_browser);
		okView = (TextView)findViewById(R.id.btn_ok);
		picQuality = (TextView) findViewById(R.id.pic_quality);
		imageGridView = (GridView)findViewById(R.id.grid_image);
		imageGridView.setEmptyView(findViewById(R.id.result_llyt));
		albumRLayout = findViewById(R.id.layout_arrow);
		albumListView = (ListView)findViewById(R.id.ablum_arrow);
		Util.initListViewStyle(albumListView);
	}

	/**
	 * 绑定点击事件
	 */
	private void initListeners() {
		backView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				onBackPressed();
			}
		});
		bucketView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				changeAlbum();
			}
		});
		picQuality.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				choosePicQuality();
			}
		});
		browserView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(ImageSelectorActivty.this,ImageBrowserActivity.class);
				intent.putExtra("dataList", paramContext.getSelectedFile());
				startActivity(intent);
			}
		});
		okView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				onOKClick();
			}
		});
		imageGridView.setOnScrollListener(this);
	}

	/**
	 * 取本地图片
	 */
	private void loadImages() {
		new Thread(){
			@Override
			public void run() {
				mCursor = MediaHelper.getImagesCursor(ImageSelectorActivty.this);
				final ArrayList<Bucket> bucketList = MediaHelper.getBucketList(ImageSelectorActivty.this);
				
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						bindImageGridView(mCursor);
						bindBucketListView(bucketList);
						if(paramContext.isMult()){
							findViewById(R.id.pic_browser_bottom).setVisibility(View.VISIBLE);
							refreshUi();
						}else{
							findViewById(R.id.pic_browser_bottom).setVisibility(View.INVISIBLE);
						}
					}
				});
			}
		}.start();
	}
	
	private void bindImageGridView(Cursor cursor) {
		if(imageAdapter != null){
			isFirstEnter = true;
			imageAdapter.changeCursor(cursor);
		}else{
			imageAdapter = new ImageCursorAdapter(ImageSelectorActivty.this,cursor,paramContext.getSelectedFile()){
				@Override
				public void onItemClick(CheckedImageView itemView, String path) {
					if(!paramContext.isMult()){
						singleClick(path);
					}else if(paramContext.isChecked(path)){ //取消选中
						itemView.setChecked(false);
						paramContext.removeItem(path);
						refreshUi();
					}else if(paramContext.isAvaliable()){ //继续可选
						itemView.setChecked(true);
						paramContext.addItem(path);
						refreshUi();
					}
				}
			};
			imageGridView.setAdapter(imageAdapter);
		}
	}
	
	/**
	 * 选取单张图片
	 * @param path
	 */
	private void singleClick(String path) {
		Intent intent = new Intent();
//		intent.putExtra("filePath", path);
		paramContext.addItem(path);
		intent.putExtra(SelectorParamContext.TAG_SELECTOR, paramContext);
		setResult(RESULT_OK,intent);
		this.finish();
	}
	
	/**
	 * 多张图片返回
	 * path0,path1,path2..
	 */
	private void onOKClick(){
		Intent intent = new Intent();
//		intent.putExtra("filePath", getPathString(paramContext.getSelectedFile()));
		intent.putExtra(SelectorParamContext.TAG_SELECTOR, paramContext);
		setResult(RESULT_OK,intent);
		this.finish();
	}
	
	private String getPathString(ArrayList<String> list){
		if(list.size()<=0)
			return "";
		String text = list.get(0);
		for(int i=1;i<list.size();i++){
			text += ","+list.get(i);
		}
		return text;
	}

	private void bindBucketListView(ArrayList<Bucket> bucketList) {
		if(bucketList==null) return;
		albumListView.setAdapter(new BaseAdapter<Bucket>(ImageSelectorActivty.this,bucketList) {
			@Override
			public View getView(final int position, View convertView, ViewGroup parent) {
				if(convertView == null ){
					convertView = new BucketView(context);
				}
				BucketView item = (BucketView)convertView;
				final Bucket bucket  = (Bucket) getItem(position);
				item.setView(bucket);

				item.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						isExplore = (position != 0);
						titleView.setText(bucket.bucketName);
						bindImageGridView(MediaHelper.getImagesCursor(context, bucket.bucketId+""));
						hideAlbum();
					}
				});
				return item;
			}
		});
	}
	
	/**
	 * 弹出/隐藏相册列表
	 */
	protected void changeAlbum(){
		if (albumRLayout.getVisibility() == View.INVISIBLE) {
			popAlbum();
		} else {
			hideAlbum();
		}
	}
	
	/**
	 * 选择图片质量
	 */
	private void choosePicQuality() {
		final ActionSheet actionSheet = new ActionSheet(ImageSelectorActivty.this);
		actionSheet.show(paramContext.menuItems, new Action1<Integer>() {
			@Override
			public void invoke(Integer index) {
				actionSheet.hide();
				paramContext.setHighQulity(index==1);
				picQuality.setText(paramContext.getQuality());
			}
		});
	}
	
	private void refreshUi(){
		okView.setText("完成("+paramContext.getPercent()+")");
		if(paramContext.getSelectedFile().size()>0){
			browserView.setTextColor(paramContext.mcolor);
			browserView.setEnabled(true);
			picQuality.setText(paramContext.getQuality());
		}else{
			browserView.setTextColor(paramContext.gcolor);
			browserView.setEnabled(false);
		}
		picQuality.setVisibility(paramContext.hasQulityMenu() ?View.VISIBLE:View.INVISIBLE);
	}

	/**
	 *  弹出相册列表 
	 **/
	private void popAlbum() {
		albumRLayout.setVisibility(View.VISIBLE);
		new AnimUtil(ImageSelectorActivty.this, R.anim.translate_up_current).setLinearInterpolator().startAnimation(
				albumRLayout);
	}

	/**
	 * 
	 *  隐藏相册列表
	 **/
	private void hideAlbum() {
		new AnimUtil(ImageSelectorActivty.this, R.anim.translate_down).setLinearInterpolator().startAnimation(
				albumRLayout);
		albumRLayout.setVisibility(View.INVISIBLE);
	}
	
	@Override
	protected void onDestroy() {
		if(mCursor!=null)
			mCursor.close();
		super.onDestroy();
	}
	
	/**
     * 第一张可见图片的下标
     */
    private int mFirstVisibleItem;

    /**
     * 一屏有多少张图片可见
     */
    private int mVisibleItemCount;

    /**
     * 记录是否刚打开程序，用于解决进入程序不滚动屏幕，不会下载图片的问题。
     */
    private boolean isFirstEnter = true;

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        // 仅当GridView静止时才去下载图片，GridView滑动时取消所有正在下载的任务
        if (scrollState == SCROLL_STATE_IDLE) {
            loadImages(mFirstVisibleItem, mVisibleItemCount);
        }else{
        	ImageLoadUtil.getInstance().cancelAllTasks();
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount,
                         int totalItemCount) {
        mFirstVisibleItem = firstVisibleItem;
        mVisibleItemCount = visibleItemCount;
        // 下载的任务应该由onScrollStateChanged里调用，但首次进入程序时onScrollStateChanged并不会调用，
        // 因此在这里为首次进入程序开启下载任务。
        if (isFirstEnter && visibleItemCount > 0) {
            loadImages(firstVisibleItem, visibleItemCount);
            isFirstEnter = false;
        }
    }

    public void loadImages(int start,int count){
        for (int i = start+count-1 ; i >= start; i--) {
            String  path = imageAdapter.getItem(i);
            if(path!=null && !path.trim().equals("")){
                View view = imageGridView.findViewWithTag(CheckedImageView.TAG+path);
                if(view != null){
                    ((CheckedImageView)view).loadImage();
                }
            }
        }
    }
}
