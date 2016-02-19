package com.qiao.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Xfermode;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Checkable;
import android.widget.ImageView;

import com.qiao.imageselector.R;
import com.qiao.util.ImageLoadUtil;
import com.qiao.util.Util;

/**
 * Created by Qiao on 2016/2/16.
 */
public class ImageItemView extends ImageView implements Checkable {
    public final static String TAG = "ImageItemView:";
    private boolean isChecked;
    private Paint mPaint;
    private Xfermode mXfermode = new PorterDuffXfermode(PorterDuff.Mode.SRC_OVER);

    private String path;

    public ImageItemView(Context context){
        super(context);
        initalize();
    }

    public ImageItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initalize();
    }

    protected void initalize(){
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        setScaleType(ScaleType.CENTER_CROP);
//        setMaxHeight(Util.dp2px(getContext(),200));
//        setAdjustViewBounds(true);
        ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT,Util.dp2px(getContext(),100));
        setLayoutParams(lp);
    }

    @Override
    public void setChecked(boolean checked) {
        isChecked = checked;
        invalidate();
    }

    @Override
    public boolean isChecked() {
        return isChecked;
    }

    @Override
    public void toggle() {
        setChecked(!isChecked());
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(isChecked){
            setColorFilter(0xcc0081e4);
            // Draw CheckedBitmap.
            mPaint.reset();
            mPaint.setFilterBitmap(false);
            mPaint.setXfermode(mXfermode);

            Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.image_checked);
            float pad = bm.getWidth()/3;
            canvas.drawBitmap(bm,getWidth()-bm.getWidth()-pad,pad,mPaint);
        }else{
            setColorFilter(Color.TRANSPARENT);
        }
    }

    public void setView(String path,boolean isChecked){
        if (!Util.isNullOrWhiteSpace(path)) {
            this.path = path;
            setTag(TAG + path);
            Bitmap bm = ImageLoadUtil.getInstance().getBitmapFromLruCache(path);
            if(bm!=null){
                setImageBitmap(bm);
            }else{
                setImageResource(R.drawable.pic_default);
            }
        }else{
            setImageResource(R.drawable.pic_default);
        }
        setChecked(isChecked);
    }

    public void loadImage(){
        if (!Util.isNullOrWhiteSpace(path)) {
            ImageLoadUtil.getInstance(3,ImageLoadUtil.Type.LIFO).loadImage(path, this);
        }
    }

    public String getPath(){
        return path;
    }
}
