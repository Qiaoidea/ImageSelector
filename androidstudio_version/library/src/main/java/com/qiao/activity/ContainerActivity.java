package com.qiao.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.FrameLayout;

import com.qiao.fragment.ImageSelectorFragment;

public class ContainerActivity extends FragmentActivity{
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
        Intent intent = getIntent();
        Class<?> fragmentClass = (Class<?>) intent.getSerializableExtra("class");
        if(fragmentClass == null){
        	fragmentClass = ImageSelectorFragment.class;
        }
        try {
            Fragment fragment = (Fragment) fragmentClass.newInstance();
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            ft.add(android.R.id.content, fragment);
            ft.commitAllowingStateLoss();
        }catch (Exception e) {
            e.printStackTrace();
        }
	}
	
	public static Intent makeIntent(Context context, Class<? extends Fragment> clazz) {
		Intent intent = new Intent(context, ContainerActivity.class);
		intent.putExtra("class", clazz);
		return intent;
	}
}
