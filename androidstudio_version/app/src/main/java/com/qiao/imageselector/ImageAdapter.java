package com.qiao.imageselector;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.kogitune.activity_transition.ActivityTransitionLauncher;
import com.qiao.activity.ContainerActivity;
import com.qiao.fragment.ImageBrowserFragment;
import com.qiao.util.ImageLoadUtil;

import java.util.ArrayList;

/**
 * Created by yuweichen on 15/12/10.
 */
public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageHolder>{
    private Context context;
    private ArrayList<String> images = new ArrayList<>();
    private int widget;
    public ImageAdapter(Context context){
        this.context = context;
        widget = context.getResources().getDisplayMetrics().widthPixels / 3;
    }

    @Override
    public ImageHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ImageHolder(new ImageView(context));
    }

    @Override
    public void onBindViewHolder(ImageHolder holder, int position) {
        holder.setData(getItem(position));
    }

    public void addData(ArrayList<String> images){
        this.images.addAll(images);
        notifyDataSetChanged();
    }

    public void clearAdapter(){
        this.images.clear();
        notifyDataSetChanged();
    }

    public String getItem(int position){
        return this.images.get(position);
    }

    @Override
    public int getItemCount() {
        return images.size();
    }

    class ImageHolder extends RecyclerView.ViewHolder{
        private ImageView imageView;
        public ImageHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView;
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(widget, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.setMargins(3,3,3,3);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setLayoutParams(params);
        }

        public void setData(final String imagePath){
            ImageLoadUtil.getInstance().loadImage(imagePath, imageView);
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    final Intent intent = new Intent(context, ImageBrowserActivity.class);
                    final Intent intent = ContainerActivity.makeIntent(context, ImageBrowserFragment.class);
                    intent.putExtra("currIndex",images.indexOf(imagePath));
                    intent.putStringArrayListExtra("dataList",images);
                    ActivityTransitionLauncher
                            .with((Activity) context)
                            .from(v)
                            .launch(intent);
                }
            });
        }
    }
}
