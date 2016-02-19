package com.qiao.fragment;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Canvas;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ListView;
import android.widget.TextView;

import com.qiao.activity.ContainerActivity;
import com.qiao.adapter.BaseAdapter;
import com.qiao.adapter.RecyclerViewCursorAdapter;
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
import com.qiao.view.ImageItemView;

import java.util.ArrayList;


/**
 * Created by bingosoft on 15/4/17.
 */
public class ImageSelectorFragment extends Fragment  implements ViewTreeObserver.OnGlobalLayoutListener{
	private  Activity activity; 

	protected View backView; 
	protected TextView titleView,bucketView,picQuality,browserView,okView;
	/**
	 * 展示图片
	 */
	protected RecyclerView recyclerView;
	protected GridLayoutManager layoutManager;
	protected RecyclerViewCursorAdapter adapter;
	/**
	 * 弹出式album列表
	 */
	protected View albumRLayout;
	private ListView albumListView;
	private Cursor mCursor;

	private boolean isExplore = false;

	private SelectorParamContext paramContext;//配置选项
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		initData();
		return rootView=inflater.inflate(R.layout.fragment_image_selector,null);
	}
	
	@Override
	public void onAttach(Activity activity) {
		this.activity = activity;
		super.onAttach(activity);
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		initViews();
		initListeners();
	}

	/**
	 * 初始化数据
	 */
	private void initData() {
		Intent intent = getActivity().getIntent();
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
		recyclerView = (RecyclerView)findViewById(R.id.recyclerview);
		recyclerView.setLayoutManager(layoutManager=new GridLayoutManager(activity,3));
		recyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
			@Override
			public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
				super.onDraw(c, parent, state);
			}
		});

		albumRLayout = findViewById(R.id.layout_arrow);
		albumListView = (ListView)findViewById(R.id.ablum_arrow);
		Util.initListViewStyle(albumListView);
	}
	
	protected View rootView;
	protected View findViewById(int viewId) {
		return rootView.findViewById(viewId);
	}

	/**
	 * 绑定点击事件
	 */
	private void initListeners() {
		backView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				getActivity().onBackPressed();
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
				Intent intent = ContainerActivity.makeIntent(getActivity(), ImageBrowserFragment.class);
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
	}

	/**
	 * 取本地图片
	 */
	private void loadImages() {
		new Thread(){
			@Override
			public void run() {
				mCursor = MediaHelper.getImagesCursor(getActivity());
				final ArrayList<Bucket> bucketList = MediaHelper.getBucketList(getActivity());
				
				activity.runOnUiThread(new Runnable() {
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
	
	private void bindImageGridView(final Cursor cursor) {
		if(adapter == null){
			recyclerView.setAdapter(adapter = new RecyclerViewCursorAdapter() {
						@Override
						public void onBindViewHolder(RecyclerView.ViewHolder holder, Cursor cursor) {
							if (cursor != null) {
								String path = cursor.getString(MediaHelper.DATA_URL);
								((ImageItemView) holder.itemView).setView(path, paramContext.getSelectedFile().contains(path));
							}
						}

						@Override
						public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
							View view = new ImageItemView(parent.getContext());
							view.setOnClickListener(new OnClickListener() {
								@Override
								public void onClick(View v) {
									ImageItemView itemView = (ImageItemView)v;
									String path = itemView.getPath();
									if (!paramContext.isMult()) {
										singleClick(path);
									} else if (paramContext.isChecked(path)) { //取消选中
										itemView.setChecked(false);
										paramContext.removeItem(path);
										refreshUi();
									} else if (paramContext.isAvaliable()) { //继续可选
										itemView.setChecked(true);
										paramContext.addItem(path);
										refreshUi();
									}
								}
							});
							return new RecyclerView.ViewHolder(view) {
							};
						}
					});
			recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
				@Override
				public void onScrollStateChanged(RecyclerView recyclerView, int scrollState) {
					super.onScrollStateChanged(recyclerView, scrollState);
					if (scrollState == RecyclerView.SCROLL_STATE_IDLE) {
						reloadImage();
					} else {
						ImageLoadUtil.getInstance().cancelAllTasks();
					}
				}
			});
		}
		Cursor c = adapter.getCursor();
		if(mCursor != c && c!=null){
			c.close();
		}
		adapter.swapCursor(cursor);
		recyclerView.getViewTreeObserver().addOnGlobalLayoutListener(this);
	}

	@Override
	public void onGlobalLayout() {
		recyclerView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
		reloadImage();
	}

	private void reloadImage(){
		int count=recyclerView.getChildCount();
		if(count == 0){
			recyclerView.setVisibility(View.GONE);
			return;
		}
		for(int i=0;i<count;i++){
			((ImageItemView)recyclerView.getChildAt(i)).loadImage();
		}
	}
	
	/**
	 * 选取单张图片
	 * @param path
	 */
	private void singleClick(String path) {
		Intent intent = new Intent();
		paramContext.addItem(path);
		intent.putExtra(SelectorParamContext.TAG_SELECTOR, paramContext);
		activity.setResult(Activity.RESULT_OK,intent);
		activity.finish();
	}
	
	/**
	 * 多张图片返回
	 * path0,path1,path2..
	 */
	private void onOKClick(){
		Intent intent = new Intent();
		intent.putExtra(SelectorParamContext.TAG_SELECTOR, paramContext);
		activity.setResult(activity.RESULT_OK,intent);
		activity.finish();
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
		albumListView.setAdapter(new BaseAdapter<Bucket>(getActivity(),bucketList) {
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
						titleView.setText(bucket.bucketName);
						if (position != 0) {
							isExplore = true;
							bindImageGridView(MediaHelper.getImagesCursor(context, bucket.bucketId + ""));
						}else{
							bindImageGridView(mCursor);
						}
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
		final ActionSheet actionSheet = new ActionSheet(getActivity());
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
		new AnimUtil(getActivity(), R.anim.translate_up_current).setLinearInterpolator().startAnimation(
				albumRLayout);
	}

	/**
	 * 
	 *  隐藏相册列表
	 **/
	private void hideAlbum() {
		new AnimUtil(getActivity(), R.anim.translate_down).setLinearInterpolator().startAnimation(
				albumRLayout);
		albumRLayout.setVisibility(View.INVISIBLE);
	}
	
	@Override
	public void onDestroy() {
		if(mCursor!=null)
			mCursor.close();
		super.onDestroy();
	}
}

