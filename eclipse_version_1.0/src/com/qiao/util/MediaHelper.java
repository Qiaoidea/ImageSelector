package com.qiao.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.qiao.bean.Bucket;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;
import android.provider.MediaStore.Images.ImageColumns;

public class MediaHelper {
	/**
	 * 获取图片索引
	 */
	public static final int IMAGE_ID = 0;
    public static final int IMAGE_PATH = 1;
    
    /**
     * 获取相册索引
     */
    public static final int BUCKET_ID     = 0;
	public static final int BUCKET_NAME   = 1;
	public static final int BUCKET_URL    = 2;
	private static final String[] PROJECTION_BUCKET = {
		ImageColumns.BUCKET_ID,
		ImageColumns.BUCKET_DISPLAY_NAME,
		ImageColumns.DATA,
		ImageColumns.SIZE};
	
	public static final int ALL = 999999;//所有图片
	
	/**
	 * 取得所有图片的游标
	 * @param context
	 * @return
	 */
	public static Cursor getImagesCursor(Context context) {
		return getImagesCursor(context,""+ALL);
	}
    
	/**
	 * 取得id为bucketId的相册下图片游标
	 * @param context
	 * @param bucketId
	 * @return
	 */
    public static Cursor getImagesCursor(Context context,String bucketId){
		Cursor imageCursor =null;
		try {
			
			final String orderBy = MediaStore.Images.Media.DATE_TAKEN;
			String searchParams = null;
			if(!(ALL+"").equals(bucketId))
				searchParams = "bucket_id = \"" + bucketId + "\" and _size > 1024 * 8";
			final String[] columns = { MediaStore.Images.Media._ID , MediaStore.Images.Media.DATA, MediaStore.Images.Media.SIZE};
			imageCursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns, searchParams, null, orderBy + " DESC");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return imageCursor;
	}
    
    /**
     * 获取相册列表
     * @param context
     * @return
     */
    public static ArrayList<Bucket> getBucketList(Context context){
    	ArrayList<Bucket> bucketList = new ArrayList<Bucket>();
    	Map<Integer, Bucket> map = new HashMap<Integer, Bucket>(); 
    	Bucket sumBucket = new Bucket(ALL, "最近照片", "");
    	bucketList.add(sumBucket);
    	Cursor mCursor =null;
		try {
			String searchParams = "_size > 1024 * 8";
			final String orderBy = MediaStore.Images.Media.DATE_TAKEN;
			mCursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, PROJECTION_BUCKET, searchParams, null, orderBy + " DESC");
			while (mCursor.moveToNext()) {
				
				String path = mCursor.getString(BUCKET_URL);
				int id = mCursor.getInt(BUCKET_ID);
				if (! map.containsKey(id)) {
					Bucket bucket = new Bucket(
							id,
							mCursor.getString(BUCKET_NAME),path);
					bucket.count++;
					bucketList.add(bucket);
					map.put(id, bucket);
					if(sumBucket.count == 0){
						sumBucket.bucketUrl = bucket.bucketUrl;
					}
				}else{
					map.get(id).count++;
				}
				sumBucket.count++;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			if(mCursor!=null){
				mCursor.close();
			}
		}
		return bucketList;
	}
}
