package com.qiao.util;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.qiao.imageselector.R;

import java.util.ArrayList;
import java.util.List;

public class Util {
	public static boolean isNullOrWhiteSpace(String text) {
		if (text == null)
			return true;
		return text.matches("^\\s*$");
	}
	
	public static void initListViewStyle(ListView listView) {
		listView.setDividerHeight(0);
		listView.setFadingEdgeLength(0);
	}
	
	public static void setItemStyle(ViewGroup viewGroup, int[] bgRes) {
		setItemStyle(viewGroup, bgRes, false);
	}

	public static void setItemStyle(ViewGroup viewGroup, int[] bgRes, boolean keepPadding) {
		boolean isVisible = false;
		View[] viewArray = new View[viewGroup.getChildCount()];
		for (int i = 0; i < viewArray.length; i++) {
			View child = viewArray[i] = viewGroup.getChildAt(i);
			isVisible |= child.getVisibility() == View.VISIBLE;
		}
		setItemStyle(viewArray, bgRes, keepPadding);
		viewGroup.setVisibility(isVisible ? View.VISIBLE : View.GONE);
	}

	public static void setItemStyle(View[] viewArray, int[] bgRes) {
		setItemStyle(viewArray, bgRes, false);
	}

	public static void setItemStyle(View[] viewArray, int[] bgRes, boolean keepPadding) {
		List<View> layout = new ArrayList<View>();
		for (int i = 0; i < viewArray.length; i++) {
			View v = viewArray[i];
			if (v.getVisibility() == View.VISIBLE) {
				layout.add(v);
			}
		}
		for (int i = 0, count = layout.size(); i < count; i++) {
			View view = layout.get(i);
			int l = view.getPaddingLeft();
			int t = view.getPaddingTop();
			int r = view.getPaddingRight();
			int b = view.getPaddingBottom();
			if (count == 1) {
				view.setBackgroundResource(bgRes[0]);
			} else if (count >= 2 && i == 0) {
				view.setBackgroundResource(bgRes[1]);
			} else if (count >= 2 && i == (count - 1)) {
				view.setBackgroundResource(bgRes[3]);
			} else {
				view.setBackgroundResource(bgRes[2]);
			}
			if (keepPadding) {
				view.setPadding(l, t, r, b);
			}
		}
	}

	public static int dp2px(Context context,int size){
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (size * scale + 0.5f);
	}

	public static void showFromBottom(View view){
		view.setVisibility(View.VISIBLE);
		new AnimUtil(view.getContext(), R.anim.translate_up_current).setLinearInterpolator().startAnimation(
				view);
	}

	public static void hideInBottom(View view){
		new AnimUtil(view.getContext(), R.anim.translate_down).setLinearInterpolator().startAnimation(
				view);
		view.setVisibility(View.INVISIBLE);
	}
}
