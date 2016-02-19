package com.qiao.util;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;
import android.provider.MediaStore.Images.Media;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;

import com.qiao.bean.Bucket;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Qiao
 */
public class MediaHelper {
    public static final int DATA_ID = 0;
    public static final int DATA_URL = 1;
    public static final int DATA_ALBUM_ID = 2;
    public static final int DATA_NAME = 3;

    public static final String ALBUM_ALL = "all_album";//所有图片

    private static final String[] COLUMNS_IMAGE = {
            Media._ID,
            Media.DATA,
            Media.BUCKET_ID,
            Media.BUCKET_DISPLAY_NAME,
            Media.DATE_ADDED
    };

    public static Loader<Cursor> createCursor(Context context,String albumId){
        //文件大小限定最小10kb
        StringBuffer searchParams = new StringBuffer(Media.SIZE).append("> 1024*8 ");
        //相册id
        if (!ALBUM_ALL.equals(albumId)) {
            searchParams.append("and ")
                    .append(Media.BUCKET_ID).append("='").append(albumId).append("'");
        }
        //按时间排序
        final String orderBy = new StringBuffer(Media.DATE_ADDED).append(" DESC").toString();
        return new CursorLoader(context,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                COLUMNS_IMAGE,searchParams.toString(), null, orderBy);
    }

    /**
     * 取得所有图片的游标
     * @param context
     * @return
     */
    public static Cursor getImagesCursor(Context context) {
        return getImagesCursor(context, ALBUM_ALL);
    }

    /**
     * 取得id为albumId的相册下图片游标
     * @param context
     * @param albumId
     * @return
     */
    public static Cursor getImagesCursor(Context context, String albumId) {
        Cursor imageCursor = null;
        try {
            //文件大小限定最小10kb
            StringBuffer searchParams = new StringBuffer(Media.SIZE).append("> 1024*8 ");
            //相册id
            if (!ALBUM_ALL.equals(albumId)) {
                searchParams.append("and ")
                        .append(Media.BUCKET_ID).append("='").append(albumId).append("'");
            }
            //按时间排序
            final String orderBy = new StringBuffer(Media.DATE_ADDED).append(" DESC").toString();

            imageCursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    COLUMNS_IMAGE,searchParams.toString(), null, orderBy);
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
    public static ArrayList<Bucket> getBucketList(Context context) {
        return getBucketList(getImagesCursor(context));
    }

    /**
     * 获取相册列表
     * @param mCursor
     * @return
     */
    public static ArrayList<Bucket> getBucketList(Cursor mCursor) {
        ArrayList<Bucket> bucketList = new ArrayList<Bucket>();
        Map<Integer, Bucket> map = new HashMap<Integer, Bucket>();
        Bucket sumBucket = new Bucket(-1, "最近照片", "");
        bucketList.add(sumBucket);
        try {
            while (mCursor.moveToNext()) {
                String path = mCursor.getString(DATA_URL);
                int id = mCursor.getInt(DATA_ALBUM_ID);
                if (!map.containsKey(id)) {
                    Bucket bucket = new Bucket(id,mCursor.getString(DATA_NAME), path);
                    bucket.count++;
                    bucketList.add(bucket);
                    map.put(id, bucket);
                    if (sumBucket.count == 0) {
                        sumBucket.bucketUrl = bucket.bucketUrl;
                    }
                } else {
                    map.get(id).count++;
                }
                sumBucket.count++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bucketList;
    }
}
