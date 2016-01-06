package com.qiao.imageselector.old;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.FrameLayout;

import com.qiao.fragment.ImageSelectorFragment;

public class DemoActivity extends FragmentActivity{
	protected View containerView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		containerView = new FrameLayout(this);
		containerView.setId(android.R.id.content);
		setContentView(containerView);

        initFragment();
	}

	private void initFragment() {
		Fragment fragment = new ImageSelectorFragment();
		FragmentManager fm = getSupportFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		ft.add(android.R.id.content, fragment);
		ft.commitAllowingStateLoss();
	}
}
