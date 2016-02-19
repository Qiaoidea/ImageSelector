package com.qiao.imageselector;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.widget.Toast;

import com.qiao.activity.ContainerActivity;
import com.qiao.bean.SelectorParamContext;
import com.qiao.fragment.ImageSelectorFragment;
import com.qiao.imageselector.demo.R;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    public static final int PICK_REQUEST_CODE = 10086;

    RecyclerView recyclerView;
    ImageAdapter imageAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(3,StaggeredGridLayoutManager.VERTICAL));

        imageAdapter = new ImageAdapter(this);
        recyclerView.setAdapter(imageAdapter);
    }

    public void clickPick(View view){
        /**
         *选图时调用
         **/
        Intent intent = ContainerActivity.makeIntent(this, ImageSelectorFragment.class);
        SelectorParamContext params = new SelectorParamContext();
        params.setMult(true);
        params.setMaxCount(9);
        params.setHasQulityMenu(true);
        startActivityForResult(intent, PICK_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode!=RESULT_OK){
            return;
        }

        if(requestCode==PICK_REQUEST_CODE){
            SelectorParamContext params = (SelectorParamContext)data.getParcelableExtra(SelectorParamContext.TAG_SELECTOR);
            //你的处理逻辑
            ArrayList<String> pick = params.getSelectedFile();
            Toast.makeText(this,"pick size:"+pick.size(),Toast.LENGTH_SHORT).show();
            imageAdapter.clearAdapter();
            imageAdapter.addData(pick);
        }
    }

}
